package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.AnalyzePictureMetaDataCommand
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.query.projections.DirectoryProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.*
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.annotation.PreDestroy

@Service
class FileWatchService(
        private val directoryRepository: DirectoryRepository,
        private val pictureRepository: PictureRepository,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
        private val directoryStateUpdateService: DirectoryStateUpdateService,
        @Qualifier("fileWatchScheduler") private val fileWatchScheduler: Scheduler,
) {
    private var watchList: Map<DirectoryId, WatchService> = emptyMap()

    @EventListener(ContextRefreshedEvent::class)
    fun init() {
        directoryRepository.findAllByAutomaticScanEnabledIsTrue()
                .publishOn(fileWatchScheduler)
                .subscribe {
                    this.registerPath(
                            directoryId = DirectoryId(it.id),
                            directoryPath = Paths.get(it.location)
                    )
                }

        directoryRepository.subscribeToAll()
                .publishOn(fileWatchScheduler)
                .subscribe(this::handleDirectoryEvent)
    }

    @PreDestroy
    fun cleanUp() {
        watchList.values.forEach { it.close() }
    }

    private fun handleDirectoryEvent(event: ReactiveEvent<DirectoryProjection>) {
        logger.debug("Handling ${event.type} for directory ${event.entity.id}")

        val eType: EventType = event.type
        val automaticScanEnabled: Boolean = event.entity.automaticScanEnabled
        val directoryId = DirectoryId(event.entity.id)
        val path: Path = Paths.get(event.entity.location)

        if (eType == EventType.UPDATE && automaticScanEnabled) {
            this.registerPath(directoryId, path)
        } else {
            this.unregisterPath(directoryId)
        }
    }

    private fun registerPath(directoryId: DirectoryId, directoryPath: Path) {
        logger.debug("Registering directory $directoryId ($directoryPath)")

        if (!watchList.containsKey(directoryId)) {
            val watchService: WatchService = FileSystems.getDefault().newWatchService()
            watchList = watchList.plus(directoryId to watchService)

            directoryStateUpdateService.updateLocalState(directoryId, directoryPath)

            createWatchServiceFlux(directoryPath, watchService)
                    .subscribeOn(fileWatchScheduler)
                    .groupBy { it.context() }
                    .flatMap { it.take(Duration.ofSeconds(1)).collectList() }
                    .map { events ->
                        val creation: WatchEvent<Path>? = events.find { it.kind() === ENTRY_CREATE }
                        val deletion: WatchEvent<Path>? = events.find { it.kind() === ENTRY_DELETE }
                        val modification: WatchEvent<Path>? = events.find { it.kind() === ENTRY_MODIFY }

                        when {
                            creation != null && deletion != null -> null
                            creation != null -> creation
                            deletion != null -> deletion
                            else -> modification
                        }
                    }
                    .filter { it != null }
                    .map { it!!.kind() to directoryPath.resolve(it.context()) }
                    .subscribe { (kind: WatchEvent.Kind<Path>, eventPath: Path) ->
                        when (kind) {
                            ENTRY_CREATE -> handleCreateEvent(directoryId, eventPath)
                            ENTRY_MODIFY -> handleModifyEvent(directoryId, eventPath)
                            ENTRY_DELETE -> handleDeleteEvent(directoryId, eventPath)
                        }
                    }
        }
    }

    private fun handleCreateEvent(directoryId: DirectoryId, eventPath: Path) {
        logger.debug("New file or directory found for $directoryId ($eventPath)")

        val addPicture: () -> Unit = {
            logger.debug("Found new file in $eventPath (for $directoryId)")

            fileSystemService.readContentType(eventPath).subscribe { contentType ->
                commandGateway.send<Unit>(CreatePictureCommand(
                        pictureId = PictureId(),
                        location = eventPath,
                        contentType = contentType,
                        directoryId = directoryId,
                ))
            }
        }

        val addDirectory: () -> Unit = {
            logger.debug("Found new directory $eventPath (for $directoryId)")

            commandGateway.send<Unit>(
                    RegisterDirectoryCommand(
                            directoryId = DirectoryId(),
                            location = eventPath
                    )
            )
        }

        fileSystemService.isRegularFile(eventPath).subscribe { if (it) addPicture() else addDirectory() }
    }

    private fun handleModifyEvent(directoryId: DirectoryId, eventPath: Path) {
        pictureRepository.findByLocation(eventPath.toString()).subscribe {
            logger.debug("File modified $directoryId (for $directoryId)")

            commandGateway.send<Unit>(AnalyzePictureMetaDataCommand(
                    pictureId = PictureId(it.id),
            ))
        }
    }

    private fun handleDeleteEvent(directoryId: DirectoryId, eventPath: Path) {
        pictureRepository.findByLocation(eventPath.toString()).subscribe {
            logger.debug("File deleted $eventPath (for $directoryId)")

            commandGateway.send<Unit>(DeletePictureCommand(
                    pictureId = PictureId(it.id),
            ))
        }

        directoryRepository.findAllByLocationStartsWith(eventPath.toString()).subscribe {
            logger.debug("Directory deleted $eventPath (for $directoryId)")

            commandGateway.send<Unit>(
                    UnregisterDirectoryCommand(
                            directoryId = DirectoryId(it.id)
                    )
            )
        }
    }

    private fun createWatchServiceFlux(directory: Path, watchService: WatchService): Flux<WatchEvent<Path>> = Flux.create {
        directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
        var poll = true

        try {
            while (poll) {
                val key = watchService.take()
                for (pollEvent in key.pollEvents()) {
                    it.next(pollEvent as WatchEvent<Path>)
                }
                poll = key.reset()
            }
        } catch (ex: ClosedWatchServiceException) {
            logger.debug("Watch on $directory stopped due to the watch service being closed")
        }

        it.complete()
    }

    private fun unregisterPath(directoryId: DirectoryId) {
        if (watchList.containsKey(directoryId)) {
            logger.debug("Unregistering directory $directoryId")
            val watchService: WatchService? = watchList[directoryId]
            watchService?.close()
            watchList = watchList.minus(directoryId)
        }
    }

    companion object : LoggerCompanion(FileWatchService::class)
}

@Service
class DirectoryStateUpdateService(
        private val fileSystemService: SyncFileSystemService,
        private val pictureRepository: SyncPictureRepository,
        private val commandGateway: CommandGateway,
        @Qualifier("fileWatchExecutor") private val fileWatchExecutor: ExecutorService,
) {

    fun updateLocalState(directoryId: DirectoryId, directoryPath: Path): Future<*> =
            fileWatchExecutor.submit {
                logger.debug("Running local state update for $directoryId ($directoryPath)")

                val files: List<Path> = fileSystemService.listFiles(directoryPath)
                val knownPictures: List<PictureProjection> = pictureRepository.findAllByDirectoryId(directoryId.identifier)

                // Delete non-existent files
                knownPictures
                        .filter { !files.contains(Paths.get(it.location)) }
                        .forEach {
                            commandGateway.send<Unit>(DeletePictureCommand(
                                    pictureId = PictureId(it.id),
                            ))
                        }

                // Add new files
                files
                        .filter { path -> knownPictures.any { it.location === path.toString() } }
                        .map { path -> path to fileSystemService.readContentType(path) }
                        .forEach { (location, contentType) ->
                            commandGateway.send<Unit>(CreatePictureCommand(
                                    pictureId = PictureId(),
                                    location = location,
                                    contentType = contentType,
                                    directoryId = directoryId,
                            ))
                        }
            }

    companion object : LoggerCompanion(DirectoryStateUpdateService::class)
}

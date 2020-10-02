package nl.juraji.reactive.albums.query.filesystem

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureMovedEvent
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.services.FileWatchService
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.AllowReplay
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.Path
import java.time.Duration

@Service
@AllowReplay(false)
@ProcessingGroup(ProcessingGroups.FILE_IO)
class PictureFileEventHandler(
        private val fileSystemService: FileSystemService,
        private val fileWatchService: FileWatchService,
) {

    @EventHandler
    fun on(evt: PictureDeletedEvent) {
        Mono.just(evt.location)
                .filter { evt.physicallyDeleted }
                .flatMap { fileSystemService.deleteIfExists(it) }
                .block()
    }

    @EventHandler
    fun on(evt: PictureMovedEvent) {
        Mono.just(evt)
                .doOnNext { fileWatchService.lockEvents() }
                .delayElement(Duration.ofSeconds(1))
                .flatMap { fileSystemService.moveFile(it.sourceLocation, it.targetLocation) }
                .delayElement(Duration.ofSeconds(1))
                .doFinally { fileWatchService.unlockEvents() }
                .block()
    }
}

package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.util.extensions.deferIterableTo
import nl.juraji.reactive.albums.util.extensions.deferTo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.streams.toList

@Service
class SyncFileSystemService {
    fun exists(path: Path): Boolean = Files.exists(path)

    fun readContentType(path: Path): String =
            Files.probeContentType(path)

    fun readAttributes(path: Path): BasicFileAttributes =
            Files.readAttributes(path, BasicFileAttributes::class.java)

    fun deleteIfExists(path: Path): Boolean =
            Files.deleteIfExists(path)

    fun listFiles(location: Path): List<Path> =
            Files.list(location)
                    .filter { isRegularFile(it) }
                    .toList()

    fun listDirectoriesRecursive(location: Path): List<Path> =
            Files.walk(location, FileVisitOption.FOLLOW_LINKS)
                    .filter { Files.isDirectory(it) }
                    .toList()

    fun isRegularFile(path: Path): Boolean =
            Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)

    fun moveFile(location: Path, targetLocation: Path): Path? =
            Files.move(location, targetLocation, StandardCopyOption.ATOMIC_MOVE)
}

@Service
class FileSystemService(
        private val syncFileSystemService: SyncFileSystemService,
        @Qualifier("fileIoScheduler") private val scheduler: Scheduler,
) {
    fun exists(path: Path): Mono<Boolean> =
            deferTo(scheduler) { syncFileSystemService.exists(path) }

    fun readContentType(path: Path) =
            deferTo(scheduler) { syncFileSystemService.readContentType(path) }

    fun readAttributes(path: Path): Mono<BasicFileAttributes> =
            deferTo(scheduler) { syncFileSystemService.readAttributes(path) }

    fun deleteIfExists(path: Path): Mono<Boolean> =
            deferTo(scheduler) { syncFileSystemService.deleteIfExists(path) }

    fun listFiles(path: Path): Flux<Path> =
            deferIterableTo(scheduler) { syncFileSystemService.listFiles(path) }

    fun listDirectoriesRecursive(path: Path): Flux<Path> =
            deferIterableTo(scheduler) { syncFileSystemService.listDirectoriesRecursive(path) }

    fun isRegularFile(path: Path): Mono<Boolean> =
            deferTo(scheduler) { syncFileSystemService.isRegularFile(path) }

    fun moveFile(location: Path, targetLocation: Path) =
            deferTo(scheduler) { syncFileSystemService.moveFile(location, targetLocation) }
}

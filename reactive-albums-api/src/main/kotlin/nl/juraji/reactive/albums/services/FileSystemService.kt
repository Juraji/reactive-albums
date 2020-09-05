package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.util.extensions.deferFrom
import nl.juraji.reactive.albums.util.extensions.deferFromIterable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.streams.toList

@Service
class FileSystemService(
        @Qualifier("IOScheduler") private val scheduler: Scheduler,
) {
    fun exists(path: Path): Boolean = Files.exists(path)

    fun readContentType(path: Path): Mono<String> =
            deferFrom(scheduler) { Files.probeContentType(path) }

    fun readAttributes(path: Path): Mono<BasicFileAttributes> =
            deferFrom(scheduler) { Files.readAttributes(path, BasicFileAttributes::class.java) }

    fun createDirectories(path: Path): Mono<Path> =
            deferFrom(scheduler) { Files.createDirectories(path) }

    fun deleteIfExists(path: Path): Mono<Boolean> =
            deferFrom(scheduler) { Files.deleteIfExists(path) }

    fun listFiles(location: Path): Flux<Path> =
            deferFromIterable(scheduler) {
                Files.list(location)
                        .filter { Files.isRegularFile(it, LinkOption.NOFOLLOW_LINKS) }
                        .toList()
            }

    fun listDirectoriesRecursive(location: Path): Flux<Path> =
            deferFromIterable(scheduler) {
                Files.walk(location, FileVisitOption.FOLLOW_LINKS)
                        .filter { Files.isDirectory(it) }
                        .toList()
            }
}

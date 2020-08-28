package nl.juraji.reactive.albums.services

import org.springframework.stereotype.Service
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Stream
import kotlin.streams.toList

@Service
class FileSystemService {
    fun exists(path: Path): Boolean = Files.exists(path)
    fun readContentType(path: Path): String = Files.probeContentType(path)
    fun readAttributes(path: Path): BasicFileAttributes = Files.readAttributes(path, BasicFileAttributes::class.java)
    fun createDirectories(path: Path): Path = Files.createDirectories(path)
    fun deleteIfExists(path: Path): Boolean = Files.deleteIfExists(path)
    fun listFiles(location: Path): List<Path> = Files.list(location).filter { Files.isRegularFile(it, LinkOption.NOFOLLOW_LINKS) }.toList()
    fun listDirectoriesRecursive(location: Path): Stream<Path> = Files.walk(location, FileVisitOption.FOLLOW_LINKS).filter { Files.isDirectory(it) }
}

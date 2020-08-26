package nl.juraji.reactive.albums.services

import org.imgscalr.Scalr
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.file.Path
import javax.imageio.ImageIO

@Service
class ImageService(
        private val fileSystemService: FileSystemService,
) {

    fun getImageDimensions(location: Path): Pair<Int, Int> {
        val image = readImage(location)
        return image.width to image.height
    }

    fun createThumbnail(source: Path, target: Path, size: Int, pictureType: MediaType): Path {
        fileSystemService.createDirectories(target.parent)
        fileSystemService.deleteIfExists(target)

        val image = readImage(source)
        val scaledImage = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, size)

        if (!ImageIO.write(scaledImage, pictureType.subtype, target.toFile())) {
            throw IOException("Unable to write thumbnail as $pictureType to $target")
        }

        return target
    }

    private fun readImage(location: Path): BufferedImage = ImageIO.read(location.toFile())
}

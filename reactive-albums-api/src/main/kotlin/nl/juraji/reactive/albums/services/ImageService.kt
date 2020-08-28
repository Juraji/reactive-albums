package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.util.extensions.coordinateSequence
import nl.juraji.reactive.albums.util.extensions.indexedCoordinateSequence
import nl.juraji.reactive.albums.util.extensions.then
import org.imgscalr.Scalr
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.sqrt

@Service
class ImageService(
        private val fileSystemService: FileSystemService,
        private val pictureConfiguration: PicturesAggregateConfiguration,
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

    fun createContentHash(source: Path): BitSet {
        val image = readImage(source)
        val hash = BitSet()

        val borderCropped = autoCropBorders(image, pictureConfiguration.autoCropTolerance)
        val centeredSquare = cropCenteredSquare(borderCropped)
        val sample = Scalr.resize(centeredSquare, pictureConfiguration.hashingSampleSize, Scalr.OP_GRAYSCALE)

        indexedCoordinateSequence(sample.width - 1, sample.height)
                .map { (index, x, y) -> index to (sample.getRGB(x, y) > sample.getRGB(x + 1, y)) }
                .filter { (_, currentGtNext) -> currentGtNext }
                .forEach { (index) -> hash.set(index) }

        return hash
    }

    fun autoCropBorders(source: BufferedImage, tolerance: Float): BufferedImage {
        val width = source.width
        val height = source.height
        val baseLineArgb: IntArray = getArgbComponents(source.getRGB(0, 0))

        var topY = Int.MAX_VALUE
        var topX = Int.MAX_VALUE
        var bottomY = -1
        var bottomX = -1

        coordinateSequence(width, height)
                .map { (x, y) -> x to y then getArgbComponents(source.getRGB(x, y)) }
                .filter { (_, _, rgb) -> colorWithinTolerance(baseLineArgb, rgb, tolerance) }
                .forEach { (x, y) ->
                    if (x < topX) topX = x
                    if (y < topY) topY = y
                    if (x > bottomX) bottomX = x
                    if (y > bottomY) bottomY = y
                }


        val destWidth = bottomX - topX + 1
        val destHeight = bottomY - topY + 1

        val smallestDimension = minOf(topX, topY, destWidth, destHeight)
        return if (smallestDimension < 1) source
        else Scalr.crop(source, topX, topY, destWidth, destHeight)
    }

    fun colorWithinTolerance(rgbBaseline: IntArray, rgbPixel: IntArray, tolerance: Float): Boolean {
        val distance = sqrt((rgbBaseline[0] - rgbPixel[0]) * (rgbBaseline[0] - rgbPixel[0])
                + (rgbBaseline[1] - rgbPixel[1]) * (rgbBaseline[1] - rgbPixel[1])
                + (rgbBaseline[2] - rgbPixel[2]) * (rgbBaseline[2] - rgbPixel[2])
                + ((rgbBaseline[3] - rgbPixel[3]) * (rgbBaseline[3] - rgbPixel[3])).toFloat())
        val percentAway = distance / 510
        return percentAway > tolerance
    }

    fun getArgbComponents(rgb: Int): IntArray {
        return intArrayOf(
                (rgb shr 16 and 0xff) / 255, // Alpha level
                (rgb shr 8 and 0xff) / 255, // Red level
                (rgb and 0xff) / 255, // Green level
                (rgb shr 24 and 0xff) / 255 // Blue level
        )
    }

    fun cropCenteredSquare(image: BufferedImage): BufferedImage {
        val imageWidth: Int = image.width
        val imageHeight: Int = image.height
        val shortestEdge = min(imageWidth, imageHeight)

        val offsetX = (imageWidth - shortestEdge) / 2
        val offsetY = (imageHeight - shortestEdge) / 2

        return Scalr.crop(image, offsetX, offsetY, shortestEdge, shortestEdge)
    }

    private fun readImage(location: Path): BufferedImage = ImageIO.read(location.toFile())
}

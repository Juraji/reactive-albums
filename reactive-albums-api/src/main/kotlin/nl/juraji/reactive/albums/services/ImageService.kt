package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.util.extensions.coordinateSequence
import nl.juraji.reactive.albums.util.extensions.deferTo
import nl.juraji.reactive.albums.util.extensions.then
import org.imgscalr.Scalr
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.sqrt

@Service
class ImageService(
        private val pictureConfiguration: PicturesAggregateConfiguration,
        @Qualifier("fileIoScheduler") private val scheduler: Scheduler,
) {

    fun getImageDimensions(location: Path): Mono<Dimensions> =
            readImage(location)
                    .map { Dimensions(it.width, it.height) }

    fun createThumbnail(source: Path): Mono<Pair<MediaType, ByteArray>> {
        return readImage(source)
                .map { Scalr.resize(it, Scalr.Method.ULTRA_QUALITY, pictureConfiguration.thumbnailSize) }
                .map { image ->
                    val mediaType = MediaType.IMAGE_JPEG
                    val bytes = ByteArrayOutputStream().use { out ->
                        ImageIO.write(image, mediaType.subtype, out)
                        out.toByteArray()
                    }

                    mediaType to bytes
                }
    }

    fun createContentHash(source: Path): Mono<BitSet> =
            readImage(source)
                    .flatMap { autoCropBorders(it, pictureConfiguration.autoCropTolerance) }
                    .flatMap { cropCenteredSquare(it) }
                    .map { Scalr.resize(it, pictureConfiguration.hashingSampleSize, Scalr.OP_GRAYSCALE) }
                    .map { sampleImage ->
                        coordinateSequence(sampleImage.width - 1, sampleImage.height)
                                .mapIndexed { index, (x, y) -> index to (sampleImage.getRGB(x, y) > sampleImage.getRGB(x + 1, y)) }
                                .filter { (_, currentGtNext) -> currentGtNext }
                                .fold(BitSet()) { bs, (idx) ->
                                    bs.set(idx)
                                    bs
                                }
                    }


    fun autoCropBorders(source: BufferedImage, tolerance: Double): Mono<BufferedImage> {
        val rgbaComponents: (Int) -> IntArray = { rgb ->
            intArrayOf(
                    (rgb shr 16 and 0xff) / 255, // Alpha level
                    (rgb shr 8 and 0xff) / 255, // Red level
                    (rgb and 0xff) / 255, // Green level
                    (rgb shr 24 and 0xff) / 255 // Blue level
            )
        }

        val toleranceFilter: (IntArray, IntArray) -> Boolean = { baseline, pixel ->
            val distance = sqrt((baseline[0] - pixel[0]) * (baseline[0] - pixel[0])
                    + (baseline[1] - pixel[1]) * (baseline[1] - pixel[1])
                    + (baseline[2] - pixel[2]) * (baseline[2] - pixel[2])
                    + ((baseline[3] - pixel[3]) * (baseline[3] - pixel[3])).toDouble())
            (distance / 510.0) > tolerance
        }

        return deferTo(scheduler) {
            val baseline = rgbaComponents(source.getRGB(0, 0))
            var topY = Int.MAX_VALUE
            var topX = Int.MAX_VALUE
            var bottomY = -1
            var bottomX = -1

            coordinateSequence(source.width, source.height)
                    .map { (x, y) -> x to y then rgbaComponents(source.getRGB(x, y)) }
                    .filter { (_, _, rgb) -> toleranceFilter(baseline, rgb) }
                    .forEach { (x, y) ->
                        if (x < topX) topX = x
                        if (y < topY) topY = y
                        if (x > bottomX) bottomX = x
                        if (y > bottomY) bottomY = y
                    }

            val destWidth = bottomX - topX + 1
            val destHeight = bottomY - topY + 1
            val smallestDimension = minOf(topX, topY, destWidth, destHeight)

            if (smallestDimension < 1) source
            else Scalr.crop(source, topX, topY, destWidth, destHeight)
        }
    }

    fun cropCenteredSquare(image: BufferedImage): Mono<BufferedImage> =
            deferTo(scheduler) {
                val imageWidth: Int = image.width
                val imageHeight: Int = image.height
                val shortestEdge = min(imageWidth, imageHeight)

                val offsetX = (imageWidth - shortestEdge) / 2
                val offsetY = (imageHeight - shortestEdge) / 2

                Scalr.crop(image, offsetX, offsetY, shortestEdge, shortestEdge)
            }

    private fun readImage(location: Path): Mono<BufferedImage> =
            deferTo(scheduler) { ImageIO.read(location.toFile()) }
}

data class Dimensions(
        val width: Int,
        val height: Int,
)

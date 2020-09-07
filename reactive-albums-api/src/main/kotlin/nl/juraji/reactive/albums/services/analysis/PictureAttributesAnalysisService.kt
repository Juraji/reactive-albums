package nl.juraji.reactive.albums.services.analysis

import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.AddTagCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateAttributesCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateContentHashCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateThumbnailLocationCommand
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.RgbColor
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class PictureAttributesAnalysisService(
        private val commandGateway: CommandGateway,
        private val fileSystemService: FileSystemService,
        private val imageService: ImageService,
) {

    fun analyzePicture(pictureId: PictureId, location: Path): Mono<Unit> {
        return Flux.concat(
                analyzeFileAttributes(pictureId, location),
                autoTagPicture(pictureId, location),
                createThumbnail(pictureId, location),
                analyzeImageContent(pictureId, location)
        ).last()
    }

    private fun autoTagPicture(id: PictureId, filePath: Path): Flux<Unit> {
        logger.debug("Generating tags for $id")

        return Flux.fromIterable(filePath.parent)
                .map {
                    val label: String = it.fileName.toString()
                    val labelColor: RgbColor = RgbColor.of(label)
                    val textColor: RgbColor = labelColor.contrastColor()

                    AddTagCommand(
                            pictureId = id,
                            label = label,
                            labelColor = labelColor.toHexString(),
                            textColor = textColor.toHexString(),
                            tagLinkType = TagLinkType.AUTO
                    )
                }
                .flatMap { commandGateway.send<Unit>(it).toMono() }
    }

    private fun createThumbnail(id: PictureId, filePath: Path): Mono<Unit> {
        logger.debug("Generating thumbnail for $id")

        return imageService.createThumbnail(pictureId = id, source = filePath)
                .map {
                    UpdateThumbnailLocationCommand(
                            pictureId = id,
                            thumbnailLocation = it,
                    )
                }
                .flatMap { commandGateway.send<Unit>(it).toMono() }
    }

    private fun analyzeFileAttributes(id: PictureId, filePath: Path): Mono<Unit> {
        logger.debug("Analyzing file attributes for $id")

        val fileAttributes = fileSystemService.readAttributes(filePath)
        val imageDimensions = imageService.getImageDimensions(filePath)

        return Mono.zip(fileAttributes, imageDimensions)
                .map { (attrs, dim) ->
                    val lastModifiedTime: LocalDateTime = LocalDateTime.ofInstant(
                            attrs.lastModifiedTime().toInstant(),
                            ZoneId.systemDefault()
                    )

                    UpdateAttributesCommand(
                            pictureId = id,
                            fileSize = attrs.size(),
                            lastModifiedTime = lastModifiedTime,
                            imageWidth = dim.width,
                            imageHeight = dim.height,
                    )
                }
                .flatMap { commandGateway.send<Unit>(it).toMono() }
    }

    private fun analyzeImageContent(id: PictureId, filePath: Path): Mono<Unit> {
        logger.debug("Analyzing image content of $id")

        return imageService.createContentHash(filePath)
                .map {
                    UpdateContentHashCommand(
                            pictureId = id,
                            contentHash = it
                    )
                }
                .flatMap { commandGateway.send<Unit>(it).toMono() }
    }

    companion object : LoggerCompanion(PictureAttributesAnalysisService::class)
}

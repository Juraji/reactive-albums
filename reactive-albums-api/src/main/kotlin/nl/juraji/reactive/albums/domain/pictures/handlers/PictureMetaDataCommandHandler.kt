package nl.juraji.reactive.albums.domain.pictures.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.ExternalCommandHandler
import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.PictureAggregate
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.AnalyzePictureMetaDataCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.services.Dimensions
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.modelling.command.Repository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureMetaDataCommandHandler(
        @Qualifier("pictureAggregateRepository") repository: Repository<PictureAggregate>,
        private val imageService: ImageService,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
) : ExternalCommandHandler<PictureAggregate>(repository) {

    @CommandHandler
    fun handle(cmd: AnalyzePictureMetaDataCommand) {
        execute(cmd.pictureId) {
            val (fileSize, lastModifiedTime) = readFileAttributes(cmd.pictureId, getLocation())
            setFileAttributes(fileSize, lastModifiedTime)
        }

        execute(cmd.pictureId) {
            val (width, height) = getImageDimensions(cmd.pictureId, getLocation())
            setFileAttributes(imageWidth = width, imageHeight = height)
        }

        execute(cmd.pictureId) {
            val contentHash = analyzeImageContent(cmd.pictureId, getLocation())
            setContentHash(contentHash)
        }
    }

    @EventHandler
    fun on(evt: PictureCreatedEvent) {
        commandGateway.send<Unit>(AnalyzePictureMetaDataCommand(pictureId = evt.pictureId))
    }

    private fun readFileAttributes(pictureId: PictureId, location: Path): Pair<Long, LocalDateTime> {
        logger.debug("Reading file attributes for $pictureId")
        val convertLastModifiedTime: (BasicFileAttributes) -> LocalDateTime = {
            LocalDateTime.ofInstant(it.lastModifiedTime().toInstant(), ZoneId.systemDefault())
        }

        return fileSystemService.readAttributes(location)
                .map { it.size() to convertLastModifiedTime(it) }
                .block() ?: Validate.fail { "Failed reading file attributes of image $pictureId" }
    }

    private fun getImageDimensions(pictureId: PictureId, location: Path): Dimensions {
        return imageService.getImageDimensions(location).block()
                ?: Validate.fail { "Failed reading dimensions of image $pictureId" }
    }

    private fun analyzeImageContent(pictureId: PictureId, location: Path): BitSet {
        logger.debug("Analyzing image content of $pictureId")
        return imageService.createContentHash(location).block()
                ?: Validate.fail { "Failed creating content hash for image $pictureId" }
    }

    companion object : LoggerCompanion(PictureMetaDataCommandHandler::class)
}

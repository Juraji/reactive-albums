package nl.juraji.reactive.albums.domain.pictures.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.ExternalCommandHandler
import nl.juraji.reactive.albums.domain.pictures.PictureAggregate
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.AnalyzePictureMetaDataCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
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
import java.time.LocalDateTime
import java.time.ZoneId

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureMetaDataCommandHandler(
        @Qualifier("pictureAggregateRepository") repository: Repository<PictureAggregate>,
        private val imageService: ImageService,
        private val fileSystemService: FileSystemService,
        private val commandGateway: CommandGateway,
) : ExternalCommandHandler<PictureAggregate, PictureId>(repository) {

    @CommandHandler
    fun handle(cmd: AnalyzePictureMetaDataCommand): PictureId {
        fileSystemService.readAttributes(cmd.pictureLocation).subscribe { fileAttributes ->
            val lastModifiedTime = LocalDateTime.ofInstant(fileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault())
            execute(cmd.pictureId) {
                setFileAttributes(fileSize = fileAttributes.size(), lastModifiedTime = lastModifiedTime)
            }
        }

        imageService.getImageDimensions(cmd.pictureLocation).subscribe { (width, height) ->
            execute(cmd.pictureId) {
                setFileAttributes(imageWidth = width, imageHeight = height)
            }
        }

        imageService.createContentHash(cmd.pictureLocation).subscribe { contentHash ->
            execute(cmd.pictureId) {
                setContentHash(contentHash)
            }
        }

        return cmd.pictureId
    }

    @EventHandler
    fun on(evt: PictureCreatedEvent) {
        commandGateway.send<Unit>(AnalyzePictureMetaDataCommand(pictureId = evt.pictureId, pictureLocation = evt.location))
    }

    companion object : LoggerCompanion(PictureMetaDataCommandHandler::class)
}

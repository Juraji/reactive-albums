package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.commands.UpdatePictureAttributesCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureAnalysisRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureAttributesUpdatedEvent
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.ZoneId

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class AnalyzePictureAttributesSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    protected lateinit var fileSystemService: FileSystemService

    @Autowired
    protected lateinit var imageService: ImageService

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureAnalysisRequestedEvent) {
        logger.debug("Analyzing attributes for ${evt.pictureId} at ${evt.location}")

        val fileAttributes = fileSystemService.readAttributes(evt.location)
        val (imageWidth, imageHeight) = imageService.getImageDimensions(evt.location)

        val command = UpdatePictureAttributesCommand(
                pictureId = evt.pictureId,
                fileSize = fileAttributes.size(),
                lastModifiedTime = LocalDateTime.ofInstant(fileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault()),
                imageWidth = imageWidth,
                imageHeight = imageHeight,
        )

        commandGateway.send<Unit>(command)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureAttributesUpdatedEvent) {
        logger.debug("File attributes analyzed for ${evt.pictureId}")
    }

    companion object : LoggerCompanion() {
    }
}

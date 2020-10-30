package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.PictureAnalysisStatus
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.SetContentHashCommand
import nl.juraji.reactive.albums.domain.pictures.commands.SetFileAttributesCommand
import nl.juraji.reactive.albums.domain.pictures.commands.SetPictureAnalysisStatusCommand
import nl.juraji.reactive.albums.domain.pictures.events.ContentHashUpdatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.FileAttributesUpdatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.services.CommandDispatch
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.ZoneId

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureAnalysisSaga {

    @Autowired
    private lateinit var imageService: ImageService

    @Autowired
    private lateinit var fileSystemService: FileSystemService

    @Autowired
    private lateinit var commandDispatch: CommandDispatch

    var fileAttributesSet = false
    var imageDimensionsSet = false
    var contentHashSet = false

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureCreatedEvent) {
        this.runAnalysis(evt.pictureId, evt.location)
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: FileAttributesUpdatedEvent) {
        fileAttributesSet = fileAttributesSet || evt.fileSize != null
        imageDimensionsSet = imageDimensionsSet || evt.imageWidth != null
        this.onEventHandled(evt.pictureId)
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: ContentHashUpdatedEvent) {
        contentHashSet = true
        this.onEventHandled(evt.pictureId)
    }

    private fun onEventHandled(pictureId: PictureId) {
        if (fileAttributesSet && imageDimensionsSet && contentHashSet) {
            commandDispatch.dispatchAndForget(SetPictureAnalysisStatusCommand(
                    pictureId = pictureId,
                    status = PictureAnalysisStatus.COMPLETED
            ))

            SagaLifecycle.end()
        }
    }

    private fun runAnalysis(pictureId: PictureId, location: Path) {
        commandDispatch.dispatchAndForget(SetPictureAnalysisStatusCommand(
                pictureId = pictureId,
                status = PictureAnalysisStatus.IN_PROGRESS
        ))

        logger.debug("Reading file attributes for picture $pictureId at $location")
        fileSystemService.readAttributes(location).blockOptional()
                .map {
                    val lastModifiedTime = LocalDateTime.ofInstant(it.lastModifiedTime().toInstant(), ZoneId.systemDefault())
                    it.size() to lastModifiedTime
                }
                .ifPresent { (fileSize, lastModifiedTime) ->
                    commandDispatch.dispatchAndForget(SetFileAttributesCommand(
                            pictureId = pictureId,
                            fileSize = fileSize,
                            lastModifiedTime = lastModifiedTime
                    ))
                }

        logger.debug("Reading image dimensions for picture $pictureId at $location")
        imageService.getImageDimensions(location).blockOptional()
                .ifPresent { (width, height) ->
                    commandDispatch.dispatchAndForget(SetFileAttributesCommand(
                            pictureId = pictureId,
                            imageWidth = width,
                            imageHeight = height,
                    ))
                }

        logger.debug("Generating content hash for picture $pictureId at $location")
        imageService.createContentHash(location).blockOptional()
                .ifPresent {
                    commandDispatch.dispatchAndForget(SetContentHashCommand(
                            pictureId = pictureId,
                            contentHash = it
                    ))
                }
    }

    companion object : LoggerCompanion(PictureAnalysisSaga::class)
}

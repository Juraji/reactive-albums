package nl.juraji.reactive.albums.processing

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.commands.UpdateAttributesCommand
import nl.juraji.reactive.albums.domain.pictures.events.AnalysisRequestedEvent
import nl.juraji.reactive.albums.services.FileSystemService
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.time.LocalDateTime
import java.time.ZoneId

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureAttributesProcessor(
        private val commandGateway: CommandGateway,
        private val fileSystemService: FileSystemService,
        private val imageService: ImageService,
) {

    @EventSourcingHandler
    fun on(evt: AnalysisRequestedEvent) {
        logger.debug("Analyzing file attributes for ${evt.pictureId}")

        val fileAttributes = fileSystemService.readAttributes(evt.location)
        val imageDimensions = imageService.getImageDimensions(evt.location)

        Mono.zip(fileAttributes, imageDimensions)
                .map { (attrs, dim) ->
                    val lastModifiedTime: LocalDateTime = LocalDateTime.ofInstant(
                            attrs.lastModifiedTime().toInstant(),
                            ZoneId.systemDefault()
                    )

                    UpdateAttributesCommand(
                            pictureId = evt.pictureId,
                            fileSize = attrs.size(),
                            lastModifiedTime = lastModifiedTime,
                            imageWidth = dim.width,
                            imageHeight = dim.height,
                    )
                }
                .subscribe { commandGateway.send<Unit>(it) }
    }

    companion object : LoggerCompanion(PictureAttributesProcessor::class)
}

package nl.juraji.reactive.albums.processing

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.AddTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.AnalysisRequestedEvent
import nl.juraji.reactive.albums.util.Colors
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class AutoTagProcessor(
        private val commandGateway: CommandGateway,
) {
    @EventSourcingHandler
    fun on(evt: AnalysisRequestedEvent) {
        logger.info("Generating tags for ${evt.pictureId}")

        evt.location.parent
                .map { path ->
                    val label = path.fileName.toString()
                    val labelColor = Colors.generateColor(label)
                    val textColor = Colors.contrastColor(labelColor)

                    AddTagCommand(
                            pictureId = evt.pictureId,
                            label = label,
                            labelColor = labelColor.toHexString(),
                            textColor = textColor.toHexString(),
                            tagLinkType = TagLinkType.AUTO
                    )
                }
                .forEach { commandGateway.send<Unit>(it) }
    }

    companion object : LoggerCompanion()
}

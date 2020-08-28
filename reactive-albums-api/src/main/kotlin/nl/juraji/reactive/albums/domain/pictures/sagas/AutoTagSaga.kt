package nl.juraji.reactive.albums.domain.pictures.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.AddTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureAnalysisRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.events.TagAddedEvent
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.*
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class AutoTagSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureAnalysisRequestedEvent) {
        evt.location.parent.forEach {
            val label = it.fileName.toString()
            val color = labelAsHexColor(label)

            commandGateway.send<Unit>(
                    AddTagCommand(
                            pictureId = evt.pictureId,
                            label = label,
                            color = color,
                            tagLinkType = TagLinkType.AUTO
                    )
            ).thenRun { SagaLifecycle.associateWith(evt.pictureId.toString(), label) }
        }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun onEvent(evt: TagAddedEvent) {
        val scope = SagaLifecycle.getCurrentScope<AnnotatedSaga<Any>>()
        scope.associationValues.remove(AssociationValue(TAG_ID_ASSOCIATION, evt.label))

        if (scope.associationValues.none { it.key == TAG_ID_ASSOCIATION }) {
            logger.debug("Auto tagging completed for ${evt.pictureId}")
            SagaLifecycle.end()
        }
    }

    private fun labelAsHexColor(label: String): String = Integer
            .toHexString(label.hashCode())
            .padStart(8, '0')
            .substring(2)

    companion object : LoggerCompanion() {
        private const val TAG_ID_ASSOCIATION = "tag-id"
    }
}

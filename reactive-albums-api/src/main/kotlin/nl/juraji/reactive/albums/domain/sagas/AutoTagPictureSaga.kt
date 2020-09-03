package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.AddTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.AnalysisRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.events.TagAddedEvent
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.extensions.toHexColor
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.*
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class AutoTagPictureSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: AnalysisRequestedEvent) {
        evt.location.parent.forEach {
            val label = it.fileName.toString()
            val color = label.toHexColor()

            commandGateway.send<Unit>(
                    AddTagCommand(
                            pictureId = evt.pictureId,
                            label = label,
                            color = color,
                            tagLinkType = TagLinkType.AUTO
                    )
            ).thenRun { SagaLifecycle.associateWith(TAG_ASSOCIATION, label) }
        }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun onEvent(evt: TagAddedEvent) {
        val scope = SagaLifecycle.getCurrentScope<AnnotatedSaga<Any>>()
        scope.associationValues.remove(AssociationValue(TAG_ASSOCIATION, evt.label))

        if (scope.associationValues.none { it.key == TAG_ASSOCIATION }) {
            logger.debug("Auto tagging completed for ${evt.pictureId}")
            SagaLifecycle.end()
        }
    }

    companion object : LoggerCompanion() {
        private const val TAG_ASSOCIATION = "label"
    }
}

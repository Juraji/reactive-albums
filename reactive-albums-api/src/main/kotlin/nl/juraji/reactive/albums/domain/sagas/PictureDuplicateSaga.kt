package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.LinkDuplicateCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UnlinkDuplicateCommand
import nl.juraji.reactive.albums.domain.pictures.events.DuplicateLinkedEvent
import nl.juraji.reactive.albums.domain.pictures.events.DuplicateUnlinkedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.util.SagaAssociations
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureDuplicateSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: DuplicateLinkedEvent) {
        SagaAssociations.associateWith("targetId", evt.targetId.toString())

        commandGateway.runCatching {
            sendAndWait<Any>(LinkDuplicateCommand(
                    pictureId = evt.targetId,
                    targetId = evt.pictureId,
                    similarity = evt.similarity
            ))
        }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun onSourceUnlink(evt: DuplicateUnlinkedEvent) {
        if (SagaAssociations.hasAssociation("targetId", evt.targetId.toString())) {
            commandGateway.runCatching {
                sendAndWait<Any>(UnlinkDuplicateCommand(
                        pictureId = evt.targetId,
                        targetId = evt.pictureId,
                ))
            }
        }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureDeletedEvent) {
        val targetIds: List<PictureId> = SagaAssociations.getAssociatedValues("targetId", ::PictureId)
        targetIds.forEach { targetId ->
            commandGateway.runCatching {
                send<Any>(UnlinkDuplicateCommand(
                        pictureId = targetId,
                        targetId = evt.pictureId,
                ))
            }
        }
    }

    @SagaEventHandler(associationProperty = "targetId")
    fun onTargetUnlink(evt: DuplicateUnlinkedEvent) {
        SagaAssociations.removeAssociationWith("targetId", evt.targetId.toString())

        if (!SagaAssociations.hasAssociationKey("targetId")) {
            SagaLifecycle.end()
        }
    }
}

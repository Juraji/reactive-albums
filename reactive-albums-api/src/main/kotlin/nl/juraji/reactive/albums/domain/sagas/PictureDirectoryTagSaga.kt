package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.commands.LinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UnlinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureMovedEvent
import nl.juraji.reactive.albums.domain.pictures.events.TagUnlinkedEvent
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryTagLUTRepository
import nl.juraji.reactive.albums.services.CommandDispatch
import nl.juraji.reactive.albums.util.SagaAssociations
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureDirectoryTagSaga {

    @Autowired
    private lateinit var directoryTagLUTRepository: DirectoryTagLUTRepository

    @Autowired
    private lateinit var commandDispatch: CommandDispatch

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureCreatedEvent) {
        val tagId: TagId = directoryTagLUTRepository.findById(evt.directoryId.identifier)
                .map { TagId(it.tagId) }
                .orElseThrow()

        val cmd = LinkTagCommand(
                pictureId = evt.pictureId,
                tagId = tagId,
        )

        SagaAssociations.associateWith(LINKED_TAG_ASSOC_KEY, tagId.identifier)
        commandDispatch.dispatchBlocking<Unit>(cmd)
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureMovedEvent) {
        val linkedTagId: TagId? = SagaAssociations.getAssociatedValue(LINKED_TAG_ASSOC_KEY, ::TagId)
        val newTagId: TagId = directoryTagLUTRepository.findById(evt.targetDirectoryId.identifier)
                .map { TagId(it.tagId) }
                .orElseThrow()

        if (linkedTagId != null) {
            SagaAssociations.associateWith(UNLINKED_TAG_ASSOC_KEY, linkedTagId.identifier)
            commandDispatch.dispatchBlocking<Unit>(UnlinkTagCommand(
                    pictureId = evt.pictureId,
                    tagId = linkedTagId
            ))
        }

        SagaAssociations.associateWith(LINKED_TAG_ASSOC_KEY, newTagId.identifier)
        commandDispatch.dispatchBlocking<Unit>(LinkTagCommand(
                pictureId = evt.pictureId,
                tagId = newTagId,
        ))
    }

    @SagaEventHandler(associationProperty = "tagId", keyName = UNLINKED_TAG_ASSOC_KEY)
    fun on(evt: TagUnlinkedEvent) {
        if (SagaAssociations.hasAssociation("pictureId", evt.pictureId.identifier)) {
            SagaLifecycle.removeAssociationWith(UNLINKED_TAG_ASSOC_KEY, evt.tagId.toString())
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureDeletedEvent) {
    }

    companion object {
        const val LINKED_TAG_ASSOC_KEY = "linkedTagId"
        const val UNLINKED_TAG_ASSOC_KEY = "unlinkedTagId"
    }
}

package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.LinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UnlinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureMovedEvent
import nl.juraji.reactive.albums.domain.pictures.events.TagUnlinkedEvent
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.query.projections.repositories.SyncTagRepository
import nl.juraji.reactive.albums.util.SagaAssociations
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureDirectoryTagSaga {

    @Autowired
    private lateinit var tagRepository: SyncTagRepository

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureCreatedEvent) {
        val tagId: TagId = getDirectoryTagByPath(evt.location.parent)
        val cmd = LinkTagCommand(
                pictureId = evt.pictureId,
                tagId = tagId,
                tagLinkType = TagLinkType.AUTO
        )

        SagaAssociations.associateWith(LINKED_TAG_ASSOC_KEY, tagId.identifier)
        commandGateway.sendAndWait<Unit>(cmd)
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureMovedEvent) {
        val linkedTagId: TagId? = SagaAssociations.getAssociatedValue(LINKED_TAG_ASSOC_KEY) { TagId(it) }
        val newTagId: TagId = getDirectoryTagByPath(evt.targetLocation.parent)

        if (linkedTagId != null) {
            SagaAssociations.associateWith(UNLINKED_TAG_ASSOC_KEY, linkedTagId.identifier)
            commandGateway.sendAndWait<Unit>(UnlinkTagCommand(
                    pictureId = evt.pictureId,
                    tagId = linkedTagId
            ))
        }

        SagaAssociations.associateWith(LINKED_TAG_ASSOC_KEY, newTagId.identifier)
        commandGateway.sendAndWait<Unit>(LinkTagCommand(
                pictureId = evt.pictureId,
                tagId = newTagId,
                tagLinkType = TagLinkType.AUTO
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

    private fun getDirectoryTagByPath(path: Path): TagId {
        return tagRepository.findByTagTypeAndLabel(
                tagType = TagType.DIRECTORY,
                label = path.fileName.toString()
        ).map { TagId(it.id) }.orElseThrow()
    }

    companion object {
        const val LINKED_TAG_ASSOC_KEY = "linkedTagId"
        const val UNLINKED_TAG_ASSOC_KEY = "unlinkedTagId"
    }
}

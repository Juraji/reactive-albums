package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.LinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UnlinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureMovedEvent
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.query.projections.repositories.SyncTagRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path

@Saga
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

        commandGateway.send<Unit>(cmd)
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureMovedEvent) {
        val oldTagId: TagId = getDirectoryTagByPath(evt.location.parent)
        commandGateway.send<Unit>(UnlinkTagCommand(
                pictureId = evt.pictureId,
                tagId = oldTagId
        ))

        val newTagId: TagId = getDirectoryTagByPath(evt.targetLocation.parent)
        commandGateway.send<Unit>(LinkTagCommand(
                pictureId = evt.pictureId,
                tagId = newTagId,
                tagLinkType = TagLinkType.AUTO
        ))
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

    companion object : LoggerCompanion(PictureDirectoryTagSaga::class)
}

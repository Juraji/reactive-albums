package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.domain.tags.commands.DeleteTagCommand
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.domain.tags.events.TagDeletedEvent
import nl.juraji.reactive.albums.services.CommandDispatch
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.SagaAssociations
import org.axonframework.config.ProcessingGroup
import org.axonframework.messaging.MetaData
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class DirectoryTagSaga {

    @Autowired
    private lateinit var commandDispatch: CommandDispatch

    var location: Path? = null
    var directoryTagId: TagId? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryRegisteredEvent) {
        this.location = evt.location
        val tagId = TagId()

        SagaAssociations.associateWith("tagId", tagId.identifier)
        commandDispatch.dispatchBlocking<Unit>(
                command = CreateTagCommand(
                        tagId = tagId,
                        label = evt.displayName,
                        tagType = TagType.DIRECTORY
                ),
                metaData = MetaData.with("DIRECTORY_ID", evt.directoryId.identifier)
        )
    }

    @SagaEventHandler(associationProperty = "tagId")
    fun on(evt: TagCreatedEvent) {
        logger.debug("Tag \"${evt.label}\" for directory $location has been created")
        this.directoryTagId = evt.tagId
    }

    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryUnregisteredEvent) {
        if (directoryTagId != null) {
            commandDispatch.dispatchBlocking<Unit>(DeleteTagCommand(
                    tagId = directoryTagId!!
            ))
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "tagId")
    fun on(evt: TagDeletedEvent) {
        logger.debug("Tag for directory $location has been deleted")
    }

    companion object : LoggerCompanion(DirectoryTagSaga::class)
}

package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Path

@Saga
@ProcessingGroup(ProcessingGroups.DIRECTORY_SCANS)
class CreateDirectoryTagSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway
    private var location: Path? = null

    @StartSaga
    @SagaEventHandler(associationProperty = "directoryId")
    fun on(evt: DirectoryRegisteredEvent) {
        this.location = evt.location

        val cmd = CreateTagCommand(
                tagId = TagId(),
                label = evt.displayName,
                tagType = TagType.DIRECTORY
        )

        commandGateway.send<Unit>(cmd)
        SagaLifecycle.associateWith("tagId", cmd.tagId.toString())
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "tagId")
    fun on(evt: TagCreatedEvent) {
        logger.info("Registered tag with id ${evt.tagId} for directory $location (tagColor: ${evt.tagColor}, textColor: ${evt.textColor})")
    }

    companion object : LoggerCompanion(CreateDirectoryTagSaga::class)
}

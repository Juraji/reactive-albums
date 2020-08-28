package nl.juraji.reactive.albums.domain.directories

import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryScanRequestedEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.nio.file.Path

@Aggregate
class DirectoryAggregate() {

    @AggregateIdentifier
    private lateinit var directoryId: DirectoryId
    private lateinit var location: Path

    @CommandHandler
    constructor(cmd: RegisterDirectoryCommand) : this() {
        val displayName = cmd.location.fileName.toString()

        AggregateLifecycle.apply(
                DirectoryRegisteredEvent(
                        directoryId = cmd.directoryId,
                        location = cmd.location,
                        displayName = displayName
                )
        )

        AggregateLifecycle.apply(
                DirectoryScanRequestedEvent(
                        directoryId = cmd.directoryId,
                        location = cmd.location,
                        firstTime = true
                )
        )
    }

    @CommandHandler
    fun handle(cmd: UnregisterDirectoryCommand) {
        AggregateLifecycle.apply(
                DirectoryUnregisteredEvent(
                        directoryId = this.directoryId
                )
        )
    }

    @EventSourcingHandler
    fun on(evt: DirectoryRegisteredEvent) {
        this.directoryId = evt.directoryId
        this.location = evt.location
    }

    fun on(evt: DirectoryUnregisteredEvent) {
        AggregateLifecycle.markDeleted()
    }
}

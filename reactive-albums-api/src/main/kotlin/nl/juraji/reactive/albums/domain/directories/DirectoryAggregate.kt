package nl.juraji.reactive.albums.domain.directories

import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UpdateDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUpdatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class DirectoryAggregate() {

    @AggregateIdentifier
    private lateinit var directoryId: DirectoryId
    private var automaticScanEnabled = true

    @CommandHandler
    constructor(cmd: RegisterDirectoryCommand) : this() {
        val displayName = cmd.location.fileName.toString()

        AggregateLifecycle.apply(
                DirectoryRegisteredEvent(
                        directoryId = cmd.directoryId,
                        location = cmd.location,
                        displayName = displayName,
                        automaticScanEnabled = this.automaticScanEnabled
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

    @CommandHandler
    fun handle(cmd: UpdateDirectoryCommand) {
        AggregateLifecycle.apply(
                DirectoryUpdatedEvent(
                        directoryId = this.directoryId,
                        automaticScanEnabled = cmd.automaticScanEnabled ?: this.automaticScanEnabled
                )
        )
    }

    @EventSourcingHandler
    fun on(evt: DirectoryRegisteredEvent) {
        this.directoryId = evt.directoryId
    }

    @EventSourcingHandler
    fun on(evt: DirectoryUnregisteredEvent) {
        AggregateLifecycle.markDeleted()
    }

    @EventSourcingHandler
    fun on(evt: DirectoryUpdatedEvent) {
        this.automaticScanEnabled = evt.automaticScanEnabled
    }
}

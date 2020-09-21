package nl.juraji.reactive.albums.domain.directories

import nl.juraji.reactive.albums.domain.directories.commands.RegisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UnregisterDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.commands.UpdateDirectoryCommand
import nl.juraji.reactive.albums.domain.directories.events.DirectoryRegisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.directories.events.DirectoryUpdatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.MetaData
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.nio.file.Path

@Aggregate
class DirectoryAggregate() {

    @AggregateIdentifier
    private lateinit var directoryId: DirectoryId
    private lateinit var location: Path
    private var automaticScanEnabled = true

    @CommandHandler
    constructor(cmd: RegisterDirectoryCommand) : this() {
        val displayName = cmd.location.fileName.toString()

        AggregateLifecycle.apply(
                DirectoryRegisteredEvent(
                        directoryId = cmd.directoryId,
                        location = cmd.location,
                        displayName = displayName,
                        automaticScanEnabled = this.automaticScanEnabled,
                ),
                MetaData.with("AUDIT", "Registered new directory: ${cmd.location}")
        )
    }

    @CommandHandler
    fun handle(cmd: UnregisterDirectoryCommand): DirectoryId {
        AggregateLifecycle.apply(
                DirectoryUnregisteredEvent(
                        directoryId = this.directoryId
                ),
                MetaData.with("AUDIT", "Unregistered directory: $location")
        )

        return directoryId
    }

    @CommandHandler
    fun handle(cmd: UpdateDirectoryCommand): DirectoryId {
        val automaticScanEnabled = cmd.automaticScanEnabled ?: this.automaticScanEnabled

        AggregateLifecycle.apply(
                DirectoryUpdatedEvent(
                        directoryId = this.directoryId,
                        automaticScanEnabled = automaticScanEnabled
                ),
                MetaData.with("AUDIT",
                        if (automaticScanEnabled) "Enabled automatic scan of $location"
                        else "Disabled automatic scan of $location")
        )

        return directoryId
    }

    @EventSourcingHandler
    fun on(evt: DirectoryRegisteredEvent) {
        this.directoryId = evt.directoryId
        this.location = evt.location
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

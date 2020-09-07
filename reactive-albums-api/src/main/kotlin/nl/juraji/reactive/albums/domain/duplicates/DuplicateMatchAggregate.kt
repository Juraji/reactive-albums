package nl.juraji.reactive.albums.domain.duplicates

import nl.juraji.reactive.albums.domain.duplicates.commands.LinkDuplicateCommand
import nl.juraji.reactive.albums.domain.duplicates.commands.UnlinkDuplicateCommand
import nl.juraji.reactive.albums.domain.duplicates.events.DuplicateLinkedEvent
import nl.juraji.reactive.albums.domain.duplicates.events.DuplicateUnlinkedEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class DuplicateMatchAggregate() {

    @AggregateIdentifier
    private lateinit var duplicateMatchId: DuplicateMatchId
    private lateinit var sourceId: PictureId
    private lateinit var targetId: PictureId

    constructor(cmd: LinkDuplicateCommand) : this() {

        AggregateLifecycle.apply(
                DuplicateLinkedEvent(
                        duplicateMatchId = cmd.duplicateMatchId,
                        sourceId = cmd.sourceId,
                        targetId = cmd.targetId,
                        similarity = cmd.similarity,
                )
        )
    }

    @CommandHandler
    fun handle(cmd: UnlinkDuplicateCommand) {
        AggregateLifecycle.apply(
                DuplicateUnlinkedEvent(
                        duplicateMatchId = this.duplicateMatchId,
                        sourceId = this.sourceId,
                        targetId = this.targetId,
                )
        )
    }

    @EventSourcingHandler
    fun on(evt: DuplicateLinkedEvent) {
        this.duplicateMatchId = evt.duplicateMatchId
        this.sourceId = evt.sourceId
        this.targetId = evt.targetId
    }
}

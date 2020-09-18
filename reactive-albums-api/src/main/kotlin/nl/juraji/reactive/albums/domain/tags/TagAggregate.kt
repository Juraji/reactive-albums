package nl.juraji.reactive.albums.domain.tags

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.domain.tags.commands.DeleteTagCommand
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.domain.tags.events.TagDeletedEvent
import nl.juraji.reactive.albums.util.RgbColor
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class TagAggregate() {

    @AggregateIdentifier
    private lateinit var tagId: TagId

    @CommandHandler
    constructor(cmd: CreateTagCommand) : this() {
        Validate.isFalse(cmd.label.isBlank()) { "Tag label may not be blank" }

        val tagColor: RgbColor = cmd.tagColor ?: RgbColor.of(cmd.label)
        val textColor: RgbColor = cmd.textColor ?: tagColor.contrastColor()

        AggregateLifecycle.apply(
                TagCreatedEvent(
                        tagId = cmd.tagId,
                        label = cmd.label,
                        tagColor = tagColor,
                        textColor = textColor,
                )
        )
    }

    @CommandHandler
    fun handle(cmd: DeleteTagCommand) {
        AggregateLifecycle.apply(TagDeletedEvent(tagId = tagId))
    }

    @EventSourcingHandler
    fun on(evt: TagCreatedEvent) {
        this.tagId = evt.tagId
    }

    @EventSourcingHandler
    fun on(evt: TagDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}

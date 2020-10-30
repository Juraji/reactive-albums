package nl.juraji.reactive.albums.domain.tags

import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.domain.tags.commands.DeleteTagCommand
import nl.juraji.reactive.albums.domain.tags.commands.UpdateTagCommand
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.domain.tags.events.TagDeletedEvent
import nl.juraji.reactive.albums.domain.tags.events.TagUpdatedEvent
import nl.juraji.reactive.albums.util.RgbColor
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.MetaData
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class TagAggregate() {

    @AggregateIdentifier
    private lateinit var tagId: TagId
    private lateinit var label: String
    private lateinit var tagType: TagType

    @CommandHandler
    constructor(cmd: CreateTagCommand, metaData: MetaData) : this() {
        Validate.isFalse(cmd.label.isBlank()) { "Tag label may not be blank" }

        val tagColor: RgbColor = cmd.tagColor ?: RgbColor.of(cmd.label)
        val textColor: RgbColor = cmd.textColor ?: tagColor.contrastColor()

        val evtMetaData: MetaData = MetaData
                .with("AUDIT", "Tag created with label ${cmd.label}")
                .mergedWith(metaData)

        AggregateLifecycle.apply(
                TagCreatedEvent(
                        tagId = cmd.tagId,
                        label = cmd.label,
                        tagColor = tagColor,
                        textColor = textColor,
                        tagType = cmd.tagType
                ),
                evtMetaData
        )
    }

    @CommandHandler
    fun handle(cmd: UpdateTagCommand): TagId {
        Validate.ignoreWhen(cmd.label == null) { isFalse(cmd.label.isNullOrBlank()) { "Tag label may not be blank" } }
        Validate.isFalse(cmd.label == null && cmd.tagColor == null && cmd.textColor == null) { "No properties are updated" }

        val auditMessage = "Tag $label updated:" +
                listOfNotNull(
                        if (cmd.label != null) "label to ${cmd.label}" else null,
                        if (cmd.tagColor != null) "tag color to ${cmd.tagColor}" else null,
                        if (cmd.textColor != null) "text color to ${cmd.textColor}" else null
                ).reduce { acc, s -> "$acc, $s" }

        AggregateLifecycle.apply(
                TagUpdatedEvent(
                        tagId = tagId,
                        label = cmd.label,
                        tagColor = cmd.tagColor,
                        textColor = cmd.textColor
                ),
                MetaData.with("AUDIT", auditMessage)
        )

        return tagId
    }

    @CommandHandler
    fun handle(cmd: DeleteTagCommand): TagId {
        AggregateLifecycle.apply(
                TagDeletedEvent(tagId = tagId),
                MetaData.with("AUDIT", "Tag $label was deleted")
        )
        return tagId
    }

    @EventSourcingHandler
    fun on(evt: TagCreatedEvent) {
        this.tagId = evt.tagId
        this.tagType = evt.tagType
        this.label = evt.label
    }

    @EventSourcingHandler
    fun on(evt: TagUpdatedEvent) {
        this.label = evt.label ?: this.label
    }

    @EventSourcingHandler
    fun on(evt: TagDeletedEvent) {
        AggregateLifecycle.markDeleted()
    }
}

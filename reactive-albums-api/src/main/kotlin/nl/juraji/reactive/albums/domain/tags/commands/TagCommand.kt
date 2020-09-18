package nl.juraji.reactive.albums.domain.tags.commands

import nl.juraji.reactive.albums.domain.tags.TagId
import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class TagCommand(
        @TargetAggregateIdentifier
        open val tagId: TagId,
)

package nl.juraji.reactive.albums.domain.duplicates.commands

import nl.juraji.reactive.albums.domain.duplicates.DuplicateMatchId
import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class DuplicateMatchCommand(
        @TargetAggregateIdentifier
        open val duplicateMatchId: DuplicateMatchId,
)

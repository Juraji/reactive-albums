package nl.juraji.reactive.albums.domain.directories.commands

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class DirectoryCommand(
        @TargetAggregateIdentifier
        open val directoryId: DirectoryId,
)

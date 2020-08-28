package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.modelling.command.TargetAggregateIdentifier

abstract class PictureCommand(
        @TargetAggregateIdentifier
        open val pictureId: PictureId,
)

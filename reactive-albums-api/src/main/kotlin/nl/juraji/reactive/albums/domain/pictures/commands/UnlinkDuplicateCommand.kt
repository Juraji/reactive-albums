package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId

data class UnlinkDuplicateCommand(
        override val pictureId: PictureId,
        val matchId: DuplicateMatchId,
) : PictureCommand(pictureId)

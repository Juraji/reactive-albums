package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId

data class DuplicateUnlinkedEvent(
        override val pictureId: PictureId,
        val matchId: DuplicateMatchId,
) : PictureEvent

package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId

data class DuplicateLinkedEvent(
        override val pictureId: PictureId,
        val targetId: PictureId,
        val matchId: DuplicateMatchId,
        val similarity: Float
) : PictureEvent

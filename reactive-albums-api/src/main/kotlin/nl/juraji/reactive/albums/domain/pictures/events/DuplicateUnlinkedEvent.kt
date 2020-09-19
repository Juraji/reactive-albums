package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.serialization.Revision

@Revision("1.0")
data class DuplicateUnlinkedEvent(
        override val pictureId: PictureId,
        val matchId: DuplicateMatchId,
) : PictureEvent

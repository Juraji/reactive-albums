package nl.juraji.reactive.albums.domain.duplicates.events

import nl.juraji.reactive.albums.domain.duplicates.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId

data class DuplicateUnlinkedEvent(
        override val duplicateMatchId: DuplicateMatchId,
        val sourceId: PictureId,
        val targetId: PictureId,
) : DuplicateMatchEvent

package nl.juraji.reactive.albums.domain.duplicates.commands

import nl.juraji.reactive.albums.domain.duplicates.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureId

data class LinkDuplicateCommand(
        override val duplicateMatchId: DuplicateMatchId,
        val sourceId: PictureId,
        val targetId: PictureId,
        val similarity: Float,
) : DuplicateMatchCommand(duplicateMatchId)

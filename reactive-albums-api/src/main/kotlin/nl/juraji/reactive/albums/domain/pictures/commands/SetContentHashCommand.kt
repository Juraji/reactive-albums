package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.util.*

data class SetContentHashCommand(
        override val pictureId: PictureId,
        val contentHash: BitSet,
) : PictureCommand(pictureId)

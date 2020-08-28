package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId
import java.time.LocalDateTime
import java.util.*

data class UpdatePictureAttributesCommand(
        override val pictureId: PictureId,
        val fileSize: Long? = null,
        val lastModifiedTime: LocalDateTime? = null,
        val imageWidth: Int? = null,
        val imageHeight: Int? = null,
        val contentHash: BitSet? = null,
) : PictureCommand(pictureId)

package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureId

data class AnalyzePictureMetaDataCommand(
        override val pictureId: PictureId,
) : PictureCommand(pictureId)

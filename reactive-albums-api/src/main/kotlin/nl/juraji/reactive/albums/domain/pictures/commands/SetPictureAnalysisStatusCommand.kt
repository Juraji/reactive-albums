package nl.juraji.reactive.albums.domain.pictures.commands

import nl.juraji.reactive.albums.domain.pictures.PictureAnalysisStatus
import nl.juraji.reactive.albums.domain.pictures.PictureId

data class SetPictureAnalysisStatusCommand(
        override val pictureId: PictureId,
        val status: PictureAnalysisStatus,
) : PictureCommand(pictureId)

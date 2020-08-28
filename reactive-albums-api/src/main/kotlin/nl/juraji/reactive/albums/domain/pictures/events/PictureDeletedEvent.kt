package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId

data class PictureDeletedEvent(
        override val pictureId: PictureId
) : PictureEvent

package nl.juraji.reactive.albums.domain.pictures.events

import nl.juraji.reactive.albums.domain.pictures.PictureId

interface PictureEvent {
    val pictureId: PictureId
}

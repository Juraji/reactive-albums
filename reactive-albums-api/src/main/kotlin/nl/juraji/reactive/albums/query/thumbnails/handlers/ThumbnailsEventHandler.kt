package nl.juraji.reactive.albums.query.thumbnails.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.thumbnails.Thumbnail
import nl.juraji.reactive.albums.query.thumbnails.repositories.ReactiveThumbnailRepository
import nl.juraji.reactive.albums.services.ImageService
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class ThumbnailsEventHandler(
        private val thumbnailRepository: ReactiveThumbnailRepository,
        private val imageService: ImageService,
) {

    @EventHandler
    fun on(evt: PictureCreatedEvent) {
        imageService.createThumbnail(evt.location)
                .map {
                    Thumbnail(
                            id = evt.pictureId.identifier,
                            thumbnail = it
                    )
                }
                .flatMap { thumbnailRepository.save(it) }
                .block()
    }

    @EventHandler
    fun on(evt: PictureDeletedEvent) {
        thumbnailRepository
                .deleteById(evt.pictureId.identifier)
                .block()
    }
}

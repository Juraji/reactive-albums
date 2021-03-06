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
import java.time.LocalDateTime

@Service
@ProcessingGroup(ProcessingGroups.THUMBNAILS)
class ThumbnailsEventHandler(
        private val thumbnailRepository: ReactiveThumbnailRepository,
        private val imageService: ImageService,
) {

    @EventHandler
    fun on(evt: PictureCreatedEvent) {
        imageService.createThumbnail(evt.location)
                .map { (mediaType, bytes) ->
                    Thumbnail(
                            id = evt.pictureId.identifier,
                            thumbnail = bytes,
                            lastModifiedTime = LocalDateTime.now(),
                            contentType = mediaType.toString()
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

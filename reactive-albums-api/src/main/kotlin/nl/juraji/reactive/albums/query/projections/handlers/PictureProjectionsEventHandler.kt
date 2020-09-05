package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.*
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class PictureProjectionsEventHandler(
        private val pictureRepository: ReactivePictureRepository,
) {

    @EventSourcingHandler
    fun on(evt: PictureCreatedEvent) {
        val projection = PictureProjection(
                id = evt.pictureId.identifier,
                directoryId = evt.directoryId.identifier,
                displayName = evt.displayName,
                location = evt.location.toString(),
                parentLocation = evt.location.parent.toString(),
                pictureType = evt.pictureType
        )

        pictureRepository
                .save(projection)
                .doOnError { logger.error("Failed persisting picture ${evt.pictureId}", it) }
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: AttributesUpdatedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    it.copy(
                            fileSize = evt.fileSize ?: it.fileSize,
                            lastModifiedTime = evt.lastModifiedTime ?: it.lastModifiedTime,
                            imageWidth = evt.imageWidth ?: it.imageWidth,
                            imageHeight = evt.imageHeight ?: it.imageHeight,
                    )
                }
                .doOnError { logger.error("Failed updating file attributes of picture ${evt.pictureId}", it) }
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: ContentHashUpdatedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    it.copy(
                            contentHash = evt.contentHash
                    )
                }
                .doOnError { logger.error("Failed updating content hash of picture ${evt.pictureId}", it) }
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: ThumbnailLocationUpdatedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    it.copy(
                            thumbnailLocation = evt.thumbnailLocation.toString(),
                            thumbnailType = evt.thumbnailType
                    )
                }
                .doOnError { logger.error("Failed updating thumbnail location of picture ${evt.pictureId}", it) }
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: TagAddedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    val tags = it.tags.plus(TagProjection(
                            label = evt.label,
                            labelColor = evt.labelColor,
                            textColor = evt.textColor,
                            linkType = evt.linkType,
                    ))

                    it.copy(tags = tags)
                }
                .doOnError { logger.error("Failed adding tag to picture ${evt.pictureId}", it) }
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: TagRemovedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    val tags = it.tags.filter { t -> t.label != evt.label }.toSet()
                    it.copy(tags = tags)
                }
                .doOnError { logger.error("Failed removing tag from picture ${evt.pictureId}", it) }
                .subscribe()
    }

    @EventSourcingHandler
    fun on(evt: PictureDeletedEvent) {
        pictureRepository
                .deleteById(evt.pictureId.identifier)
                .doOnError { logger.error("Failed deleting picture ${evt.pictureId}", it) }
                .subscribe()
    }

    companion object : LoggerCompanion()
}

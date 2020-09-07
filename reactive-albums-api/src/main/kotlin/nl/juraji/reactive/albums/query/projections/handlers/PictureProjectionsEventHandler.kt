package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.duplicates.events.DuplicateLinkedEvent
import nl.juraji.reactive.albums.domain.duplicates.events.DuplicateUnlinkedEvent
import nl.juraji.reactive.albums.domain.pictures.events.*
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

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
                .block()
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
                .block()
    }

    @EventSourcingHandler
    fun on(evt: ContentHashUpdatedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    it.copy(contentHash = evt.contentHash)
                }
                .block()
    }

    @EventSourcingHandler
    fun on(evt: ThumbnailLocationUpdatedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    it.copy(thumbnailLocation = evt.thumbnailLocation.toString())
                }
                .block()
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
                .block()
    }

    @EventSourcingHandler
    fun on(evt: TagRemovedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    val tags = it.tags.filter { t -> t.label != evt.label }.toSet()
                    it.copy(tags = tags)
                }
                .block()
    }

    @EventSourcingHandler
    fun on(evt: DuplicateLinkedEvent) {
        Mono.zip(
                pictureRepository.update(evt.sourceId.identifier) { it.copy(duplicateCount = it.duplicateCount + 1) },
                pictureRepository.update(evt.targetId.identifier) { it.copy(duplicateCount = it.duplicateCount + 1) }
        ).block()
    }

    @EventSourcingHandler
    fun on(evt: DuplicateUnlinkedEvent) {
        Mono.zip(
                pictureRepository.update(evt.sourceId.identifier) { it.copy(duplicateCount = it.duplicateCount - 1) },
                pictureRepository.update(evt.targetId.identifier) { it.copy(duplicateCount = it.duplicateCount - 1) }
        ).block()
    }

    @EventSourcingHandler
    fun on(evt: PictureDeletedEvent) {
        pictureRepository
                .deleteById(evt.pictureId.identifier)
                .block()
    }
}

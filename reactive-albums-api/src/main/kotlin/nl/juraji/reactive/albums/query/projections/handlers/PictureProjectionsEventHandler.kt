package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.events.*
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.TagProjection
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.projections.repositories.TagRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class PictureProjectionsEventHandler(
        private val pictureRepository: PictureRepository,
        private val tagRepository: TagRepository,
) {

    @EventHandler
    fun on(evt: PictureCreatedEvent) {
        val projection = PictureProjection(
                id = evt.pictureId.identifier,
                displayName = evt.displayName,
                location = evt.location.toString(),
                parentLocation = evt.location.parent.toString(),
                pictureType = evt.pictureType
        )

        pictureRepository
                .save(projection)
                .block()
    }

    @EventHandler
    fun on(evt: FileAttributesUpdatedEvent) {
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

    @EventHandler
    fun on(evt: TagLinkedEvent) {
        tagRepository.findById(evt.tagId.identifier)
                .switchIfEmpty { tagRepository.subscribeFirst(Duration.ofMinutes(1)) { it.id === evt.tagId.identifier } }
                .flatMap { tag ->
                    pictureRepository
                            .update(evt.pictureId.identifier) { picture ->
                                val tags: Set<TagProjection> = picture.tags.plus(tag)
                                picture.copy(tags = tags)
                            }
                }
                .block()
    }

    @EventHandler
    fun on(evt: TagUnlinkedEvent) {
        pictureRepository.update(evt.pictureId.identifier) {
            val tags: Set<TagProjection> = it.tags.filter { t -> t.id != evt.tagId.identifier }.toSet()
            it.copy(tags = tags)
        }.block()
    }

    @EventHandler
    fun on(evt: DuplicateLinkedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) { it.copy(duplicateCount = it.duplicateCount + 1) }
                .block()
    }

    @EventHandler
    fun on(evt: DuplicateUnlinkedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) { it.copy(duplicateCount = it.duplicateCount - 1) }
                .block()
    }

    @EventHandler
    fun on(evt: PictureMovedEvent) {
        pictureRepository
                .update(evt.pictureId.identifier) {
                    it.copy(
                            location = evt.targetLocation.toString(),
                            parentLocation = evt.targetLocation.parent.toString()
                    )
                }
                .block()
    }

    @EventHandler
    fun on(evt: PictureDeletedEvent) {
        pictureRepository
                .deleteById(evt.pictureId.identifier)
                .block()
    }

    @ResetHandler
    fun onReset() {
        pictureRepository.getRepository().deleteAll()
    }
}

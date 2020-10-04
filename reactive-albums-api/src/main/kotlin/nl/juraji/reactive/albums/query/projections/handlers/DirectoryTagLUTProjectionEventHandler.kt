package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.domain.tags.events.TagCreatedEvent
import nl.juraji.reactive.albums.domain.tags.events.TagDeletedEvent
import nl.juraji.reactive.albums.query.projections.DirectoryTagLUTProjection
import nl.juraji.reactive.albums.query.projections.repositories.DirectoryTagLUTRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PROJECTIONS)
class DirectoryTagLUTProjectionEventHandler(
        private val directoryTagLUTRepository: DirectoryTagLUTRepository,
) {

    @EventHandler
    fun on(evt: TagCreatedEvent, @MetaDataValue(CreateTagCommand.META_DIRECTORY_ID, required = false) directoryId: String?) {
        if (evt.tagType == TagType.DIRECTORY && !directoryId.isNullOrEmpty()) {
            val entity = DirectoryTagLUTProjection(
                    tagId = evt.tagId.identifier,
                    directoryId = directoryId
            )

            directoryTagLUTRepository.save(entity)
        }
    }

    @EventHandler
    fun on(evt: TagDeletedEvent) {
        directoryTagLUTRepository.deleteById(evt.tagId.identifier)
    }

    @ResetHandler
    fun onReset() {
        directoryTagLUTRepository.deleteAll()
    }
}

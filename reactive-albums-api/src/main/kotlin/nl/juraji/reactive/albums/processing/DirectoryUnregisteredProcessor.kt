package nl.juraji.reactive.albums.processing

import nl.juraji.reactive.albums.domain.directories.events.DirectoryUnregisteredEvent
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventsourcing.EventSourcingHandler
import org.springframework.stereotype.Service

@Service
class DirectoryUnregisteredProcessor(
        private val commandGateway: CommandGateway,
        private val pictureRepository: ReactivePictureRepository,
) {

    @EventSourcingHandler
    fun on(evt: DirectoryUnregisteredEvent) {
        pictureRepository.findAllByDirectoryId(evt.directoryId.identifier)
                .map { DeletePictureCommand(pictureId = PictureId(it.id)) }
                .subscribe { commandGateway.send<Unit>(it) }
    }
}

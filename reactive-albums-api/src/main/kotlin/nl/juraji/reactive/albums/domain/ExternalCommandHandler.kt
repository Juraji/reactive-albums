package nl.juraji.reactive.albums.domain

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.modelling.command.Repository

abstract class ExternalCommandHandler<A, ID : EntityId>(
        private val repository: Repository<A>,
) {

    protected fun execute(id: ID, f: A.() -> Unit): ID {
        repository.load(id.identifier).execute { it.apply(f) }
        return id
    }
}

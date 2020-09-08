package nl.juraji.reactive.albums.domain

import nl.juraji.reactive.albums.domain.pictures.PictureId
import org.axonframework.modelling.command.Repository

abstract class ExternalCommandHandler<A>(
        private val repository: Repository<A>,
) {

    protected fun execute(id: PictureId, f: A.() -> Unit) =
            repository.load(id.identifier).execute { it.apply(f) }
}

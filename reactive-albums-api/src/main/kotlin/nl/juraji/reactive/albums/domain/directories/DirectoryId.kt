package nl.juraji.reactive.albums.domain.directories

import nl.juraji.reactive.albums.domain.EntityId
import org.axonframework.common.IdentifierFactory

data class DirectoryId(
        override val identifier: String = IdentifierFactory.getInstance().generateIdentifier(),
) : EntityId(identifier)

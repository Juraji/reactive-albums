package nl.juraji.reactive.albums.domain.directories

import nl.juraji.reactive.albums.domain.EntityId
import org.axonframework.common.IdentifierFactory

data class DirectoryId(
        override val identifier: String,
) : EntityId {
    constructor() : this(IdentifierFactory.getInstance().generateIdentifier())

    override fun toString(): String = identifier
}

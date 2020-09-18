package nl.juraji.reactive.albums.domain.tags

import nl.juraji.reactive.albums.domain.EntityId
import org.axonframework.common.IdentifierFactory

data class TagId(
        override val identifier: String
) : EntityId {
    constructor() : this(IdentifierFactory.getInstance().generateIdentifier())

    override fun toString(): String = identifier
}

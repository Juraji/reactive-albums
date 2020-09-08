package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.EntityId
import org.axonframework.common.IdentifierFactory


data class DuplicateMatchId(
        override val identifier: String,
) : EntityId {
    constructor() : this(IdentifierFactory.getInstance().generateIdentifier())

    override fun toString(): String = identifier
}

package nl.juraji.reactive.albums.domain.duplicates

import nl.juraji.reactive.albums.domain.EntityId
import org.axonframework.common.IdentifierFactory


data class DuplicateMatchId(
        override val identifier: String = IdentifierFactory.getInstance().generateIdentifier(),
) : EntityId(identifier)

package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.EntityId
import org.axonframework.common.IdentifierFactory

data class PictureId(
        override val identifier: String = IdentifierFactory.getInstance().generateIdentifier(),
) : EntityId(identifier)

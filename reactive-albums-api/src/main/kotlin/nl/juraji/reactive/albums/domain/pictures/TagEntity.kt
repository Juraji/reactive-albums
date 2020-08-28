package nl.juraji.reactive.albums.domain.pictures

import org.axonframework.modelling.command.EntityId

data class TagEntity(
        @EntityId val label: String
)

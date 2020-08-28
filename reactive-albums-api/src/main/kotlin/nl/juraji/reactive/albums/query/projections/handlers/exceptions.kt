package nl.juraji.reactive.albums.query.projections.handlers

import nl.juraji.reactive.albums.domain.EntityId

data class NoSuchEntityException(
        val entityName: String,
        val entityId: EntityId,
        override val message: String? = null,
        override val cause: Throwable? = null,
) : RuntimeException(message, cause)

data class DuplicateEntityException(
        val entityName: String,
        val entityId: EntityId,
        override val message: String? = null,
        override val cause: Throwable? = null,
) : RuntimeException(message, cause)

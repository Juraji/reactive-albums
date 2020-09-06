package nl.juraji.reactive.albums.query.projections.handlers

data class NoSuchEntityException(
        val entityName: String,
        val entityId: String,
        override val message: String? = "$entityName with id $entityId not found!",
        override val cause: Throwable? = null,
) : RuntimeException(message, cause)

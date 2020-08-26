package nl.juraji.reactive.albums.domain

abstract class EntityId(
        open val identifier: String
) {
    override fun toString() = identifier
}

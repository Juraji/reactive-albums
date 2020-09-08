package nl.juraji.reactive.albums.domain

import java.io.Serializable

interface EntityId : Serializable {
    val identifier: String
}

package nl.juraji.reactive.albums.query.projections

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class TagProjection(
        @Id val id: String,
        @Column(unique = true) val label: String,
        val tagColor: String,
        val textColor: String,
)

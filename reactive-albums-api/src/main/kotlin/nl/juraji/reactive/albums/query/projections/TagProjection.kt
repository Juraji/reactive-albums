package nl.juraji.reactive.albums.query.projections

import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class TagProjection(
        val label: String,
        val color: String,
        @Enumerated(EnumType.STRING) val linkType: TagLinkType,
)

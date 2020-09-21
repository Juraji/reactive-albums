package nl.juraji.reactive.albums.query.projections

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@Suppress("unused")
@EntityListeners(AuditingEntityListener::class)
abstract class AuditedProjection(
        @CreatedDate @Column(updatable = false) var createdAt: LocalDateTime? = null,
        @LastModifiedDate var lastModifiedAt: LocalDateTime? = null,
)

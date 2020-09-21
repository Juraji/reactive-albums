package nl.juraji.reactive.albums.query.projections

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
@Suppress("unused")
@EntityListeners(ProjectionsAuditingEntityListener::class)
abstract class AuditedProjection(
        @CreatedDate @Column(updatable = false) open var createdAt: LocalDateTime?,
        @LastModifiedDate open var lastModifiedAt: LocalDateTime?,
)

class ProjectionsAuditingEntityListener {
    @PrePersist
    fun prePersist(o: AuditedProjection) {
        o.createdAt = LocalDateTime.now()
        o.lastModifiedAt = o.createdAt
    }

    @PreUpdate
    fun preUpdate(o: AuditedProjection) {
        o.lastModifiedAt = LocalDateTime.now()
    }
}

package nl.juraji.reactive.albums.query.audit

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class AuditLogEntry(
        @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long = 0,
        val timestamp: LocalDateTime = LocalDateTime.now(),
        @Enumerated(EnumType.STRING) val aggregateType: AggregateType,
        val aggregateId: String,
        val message: String
)

enum class AggregateType {
    DIRECTORY, PICTURE, TAG
}

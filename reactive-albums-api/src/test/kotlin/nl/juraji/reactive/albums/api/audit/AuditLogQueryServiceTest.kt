package nl.juraji.reactive.albums.api.audit

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import nl.juraji.reactive.albums.query.audit.repositories.AuditLogEntryRepository
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class AuditLogQueryServiceTest {

    @MockK
    private lateinit var auditLogEntryRepository: AuditLogEntryRepository

    @InjectMockKs
    private lateinit var auditLogQueryService: AuditLogQueryService

    @Test
    fun `findLogEntriesFor should return all log entries when aggregateId is null`() {
        val pageable: Pageable = Pageable.unpaged()
        val expectedPageable: Page<AuditLogEntry> = PageImpl(emptyList(), pageable, 0)

        every { auditLogEntryRepository.findAll(pageable) } returnsMonoOf expectedPageable

        val result = auditLogQueryService.findLogEntriesFor(pageable, null)

        StepVerifier.create(result)
                .expectNext(expectedPageable)
                .verifyComplete()
    }

    @Test
    fun `findLogEntriesFor should return log entries for aggregateId when not null`() {
        val pageable: Pageable = Pageable.unpaged()
        val aggregateId = PictureId().identifier
        val expectedPageable: Page<AuditLogEntry> = PageImpl(emptyList(), pageable, 0)

        every { auditLogEntryRepository.findByAggregateId(aggregateId, pageable) } returnsMonoOf expectedPageable

        val result = auditLogQueryService.findLogEntriesFor(pageable, aggregateId)

        StepVerifier.create(result)
                .expectNext(expectedPageable)
                .verifyComplete()
    }
}

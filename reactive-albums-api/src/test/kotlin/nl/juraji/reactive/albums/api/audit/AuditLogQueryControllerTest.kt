package nl.juraji.reactive.albums.api.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import com.ninjasquad.springmockk.MockkBean
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.slot
import nl.juraji.reactive.albums.api.ApiTestConfiguration
import nl.juraji.reactive.albums.query.audit.AggregateType
import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@Import(ApiTestConfiguration::class)
@WebFluxTest(AuditLogQueryController::class)
@AutoConfigureWebTestClient
internal class AuditLogQueryControllerTest {

    private val fixture = Fixture {
        register(LocalDateTime::class) { LocalDateTime.now() }
        register(AggregateType::class) { AggregateType.PICTURE }
    }

    @MockkBean
    private lateinit var auditLogQueryService: AuditLogQueryService

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    @Qualifier("objectMapper")
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `getAuditLogEntries should render a page of all log entries`() {
        val logEntries: List<AuditLogEntry> = listOf(fixture.next())

        val pageableSlot: CapturingSlot<Pageable> = slot()
        every { auditLogQueryService.findLogEntriesFor(capture(pageableSlot), null) } answers {
            Mono.just(PageImpl(logEntries, firstArg(), 6))
        }

        val returnResult: EntityExchangeResult<String> = webTestClient.get()
                .uri("/api/audit-log")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().returnResult()

        val expectedJson: String = objectMapper.writeValueAsString(PageImpl(logEntries, pageableSlot.captured, 6));
        Assertions.assertEquals(expectedJson, returnResult.responseBody)
        Assertions.assertEquals(0, pageableSlot.captured.pageNumber)
        Assertions.assertEquals(50, pageableSlot.captured.pageSize)
        Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "timestamp"), pageableSlot.captured.sort)
    }

    @Test
    fun `getAuditLogEntries should render a page of log entries filtered by aggregate id`() {
        val logEntries: List<AuditLogEntry> = listOf(fixture.next())

        val pageableSlot: CapturingSlot<Pageable> = slot()
        every { auditLogQueryService.findLogEntriesFor(capture(pageableSlot), logEntries[0].aggregateId) } answers {
            Mono.just(PageImpl(logEntries, firstArg(), 6))
        }

        val returnResult: EntityExchangeResult<String> = webTestClient.get()
                .uri("/api/audit-log?aggregateId=${logEntries[0].aggregateId}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().returnResult()

        val expectedJson: String = objectMapper.writeValueAsString(PageImpl(logEntries, pageableSlot.captured, 6));
        Assertions.assertEquals(expectedJson, returnResult.responseBody)
        Assertions.assertEquals(0, pageableSlot.captured.pageNumber)
        Assertions.assertEquals(50, pageableSlot.captured.pageSize)
        Assertions.assertEquals(Sort.by(Sort.Direction.DESC, "timestamp"), pageableSlot.captured.sort)
    }
}

package nl.juraji.reactive.albums.api.audit

import nl.juraji.reactive.albums.configuration.PaginationDefaults
import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import nl.juraji.reactive.albums.query.audit.repositories.AuditLogEntryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuditLogQueryController(
        private val auditLogEntryRepository: AuditLogEntryRepository
) {

    @GetMapping("/api/audit-log")
    fun getAuditLogEntries(
            @RequestParam(name = "page", defaultValue = PaginationDefaults.DEFAULT_PAGE_NO) page: Int,
            @RequestParam(name = "size", defaultValue = PaginationDefaults.DEFAULT_PAGE_SIZE) size: Int,
    ): Mono<Page<AuditLogEntry>> = auditLogEntryRepository.findAll(PageRequest.of(page, size))
}

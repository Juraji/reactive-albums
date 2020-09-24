package nl.juraji.reactive.albums.api.audit

import nl.juraji.reactive.albums.configuration.PaginationDefaults
import nl.juraji.reactive.albums.query.audit.AuditLogEntry
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuditLogQueryController(
        val auditLogQueryService: AuditLogQueryService,
) {

    @GetMapping("/api/audit-log")
    fun getAuditLogEntries(
            @RequestParam(name = "page", defaultValue = PaginationDefaults.DEFAULT_PAGE_NO) page: Int,
            @RequestParam(name = "size", defaultValue = PaginationDefaults.DEFAULT_PAGE_SIZE) size: Int,
            @RequestParam(name = "sort", defaultValue = "timestamp,desc") sort: Sort,
            @RequestParam("aggregateId", required = false) aggregateId: String?,
    ): Mono<Page<AuditLogEntry>> {
        val pageable: Pageable = PageRequest.of(page, size, sort)
        return auditLogQueryService.findLogEntriesFor(pageable, aggregateId)
    }
}

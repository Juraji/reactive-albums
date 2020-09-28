package nl.juraji.reactive.albums.api.admin

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/admin/replay")
class ReplayController(
        val eventProcessorConfiguration: EventProcessingConfiguration,
) {
    @PostMapping(ProcessingGroups.PROJECTIONS)
    fun replayProjections() {
        resetProcessorTokens(ProcessingGroups.PROJECTIONS)
    }

    @PostMapping(ProcessingGroups.AUDIT)
    fun replayAudit() {
        resetProcessorTokens(ProcessingGroups.AUDIT)
    }

    private fun resetProcessorTokens(name: String) {
        logger.info("Preparing replay for event processor: {}", name)
        eventProcessorConfiguration
                .eventProcessor<TrackingEventProcessor>(name)
                .ifPresent { eventProcessor ->
                    eventProcessor.shutDown()
                    eventProcessor.resetTokens()
                    eventProcessor.start()
                }
    }

    companion object : LoggerCompanion(ReplayController::class) {
    }
}

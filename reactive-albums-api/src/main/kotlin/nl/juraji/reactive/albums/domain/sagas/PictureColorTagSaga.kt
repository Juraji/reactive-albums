package nl.juraji.reactive.albums.domain.sagas

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.pictures.TagLinkType
import nl.juraji.reactive.albums.domain.pictures.commands.LinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.TagLinkedEvent
import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.query.projections.repositories.ColorTagLUTRepository
import nl.juraji.reactive.albums.services.ImageService
import nl.juraji.reactive.albums.util.SagaAssociations
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.serialization.Revision
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration

@Saga
@Revision("1.0")
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureColorTagSaga {

    @Autowired
    private lateinit var imageService: ImageService

    @Autowired
    private lateinit var colorTagLUTRepository: ColorTagLUTRepository

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var deadlineManager: DeadlineManager


    @StartSaga
    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: PictureCreatedEvent) {
        deadlineManager.schedule(Duration.ofMinutes(30), SAGA_DEADLINE)

        imageService.getImageDominantColors(evt.location, 5)
                .map { colorTagLUTRepository.findClosestColorTag(it.red, it.green, it.blue) }
                .distinct { it.tagId }
                .map { TagId(it.tagId) }
                .collectList()
                .block()
                ?.map { tagId ->
                    LinkTagCommand(
                            pictureId = evt.pictureId,
                            tagId = tagId,
                            tagLinkType = TagLinkType.AUTO
                    )
                }
                ?.forEach { cmd ->
                    SagaAssociations.associateWith("tagId", cmd.tagId.identifier)
                    commandGateway.send<Any>(cmd).exceptionally {
                        SagaAssociations.removeAssociationWith("tagId", cmd.tagId.identifier)
                    }
                }
    }

    @SagaEventHandler(associationProperty = "pictureId")
    fun on(evt: TagLinkedEvent) {
        SagaAssociations.removeAssociationWith("tagId", evt.tagId.identifier)

        if (!SagaAssociations.hasAssociationKey("tagId")) {
            deadlineManager.cancelAllWithinScope(SAGA_DEADLINE)
            SagaLifecycle.end()
        }
    }

    @DeadlineHandler(deadlineName = SAGA_DEADLINE)
    fun onDeadline() {
        SagaLifecycle.end()
    }

    companion object {
        const val SAGA_DEADLINE = "PictureColorTagSagaDeadline"
    }
}

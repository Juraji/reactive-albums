package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureAnalysisRequestedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Paths

internal class PictureAggregateTest {

    private lateinit var fixture: AggregateTestFixture<PictureAggregate>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(PictureAggregate::class.java)
    }

    @Test
    fun `should create picture`() {
        val pictureId = PictureId("picture#1")
        val location = Paths.get("/test/location/picture.jpg")

        fixture
                .`when`(
                        CreatePictureCommand(
                                pictureId = pictureId,
                                location = location,
                                contentType = "image/jpeg",
                                displayName = null
                        )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                        PictureCreatedEvent(
                                pictureId = pictureId,
                                displayName = "picture.jpg",
                                location = location,
                                pictureType = PictureType.JPEG
                        ),
                        PictureAnalysisRequestedEvent(
                                pictureId = pictureId,
                                location = location,
                        )
                )
    }
}

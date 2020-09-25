package nl.juraji.reactive.albums.domain.pictures

import nl.juraji.reactive.albums.domain.directories.DirectoryId
import nl.juraji.reactive.albums.domain.pictures.commands.CreatePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.MovePictureCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureMovedEvent
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
        val directoryId = DirectoryId("directory#1")
        val pictureId = PictureId("picture#1")
        val location = Paths.get("/test/location/picture.jpg")

        fixture
                .`when`(
                        CreatePictureCommand(
                                pictureId = pictureId,
                                location = location,
                                contentType = "image/jpeg",
                                displayName = null,
                                directoryId = directoryId
                        )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                        PictureCreatedEvent(
                                pictureId = pictureId,
                                displayName = "picture.jpg",
                                location = location,
                                pictureType = PictureType.JPEG,
                                directoryId = directoryId
                        )
                )
    }

    @Test
    fun `should move picture`() {
        val directoryId = DirectoryId("directory#1")
        val pictureId = PictureId("picture#1")
        val location = Paths.get("/test/location/picture.jpg")
        val targetLocation = Paths.get("/test/other-location/picture.jpg")

        fixture
                .given(
                        PictureCreatedEvent(
                                pictureId = pictureId,
                                displayName = "picture.jpg",
                                location = location,
                                pictureType = PictureType.JPEG,
                                directoryId = directoryId
                        )
                )
                .`when`(
                        MovePictureCommand(
                                pictureId = pictureId,
                                targetLocation = targetLocation
                        )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                        PictureMovedEvent(
                                pictureId = pictureId,
                                location = location,
                                targetLocation = targetLocation
                        )
                )
    }

    @Test
    fun `should delete picture`() {
        val directoryId = DirectoryId()
        val pictureId = PictureId()
        val location = Paths.get("/test/location/picture.jpg")

        fixture
                .given(
                        PictureCreatedEvent(
                                pictureId = pictureId,
                                displayName = "picture.jpg",
                                location = location,
                                pictureType = PictureType.JPEG,
                                directoryId = directoryId
                        )
                )
                .`when`(
                        DeletePictureCommand(
                                pictureId = pictureId,
                        )
                )
                .expectSuccessfulHandlerExecution()
                .expectEvents(
                        PictureDeletedEvent(
                                pictureId = pictureId,
                                location = location,
                                physicallyDeleted = false
                        )
                )
                .expectMarkedDeleted()
    }
}

package nl.juraji.reactive.albums.api.pictures

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.query.projections.DuplicateMatchProjection
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.DuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.EventType
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveEvent
import nl.juraji.reactive.albums.util.andThenMonoOf
import nl.juraji.reactive.albums.util.extensions.ServerSentEventFlux
import nl.juraji.reactive.albums.util.returnsFluxOf
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class PictureEventsServiceTest {

    private val fixture = Fixture {
        register(PictureType::class) { PictureType.JPEG }
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @MockK
    private lateinit var duplicateMatchRepository: DuplicateMatchRepository

    @InjectMockKs
    private lateinit var pictureEventsService: PictureEventsService

    @Test
    fun `getDuplicateMatchCountStream should result in a stream of the current count, initial and on update`() {

        every { duplicateMatchRepository.count() } returnsMonoOf 10L andThenMonoOf 21L andThenMonoOf 36L
        every { duplicateMatchRepository.subscribeToAll() } returnsFluxOf listOf(fixture.next(), fixture.next())

        val result = pictureEventsService.getDuplicateMatchCountStream()

        StepVerifier.create(result)
                .expectNextMatches { it.data() == 10L }
                .expectNextMatches { it.data() == 21L }
                .expectNextMatches { it.data() == 36L }
                .thenCancel()
                .verify()
    }

    @Test
    fun `getAllDuplicateMatchesStream should result in a stream of all matches, initial and on update`() {
        val initialEntities: List<DuplicateMatchProjection> = listOf(fixture.next(), fixture.next())
        val updateEntities: List<DuplicateMatchProjection> = listOf(fixture.next(), fixture.next())

        every { duplicateMatchRepository.findAll() } returnsFluxOf initialEntities
        every { duplicateMatchRepository.subscribeToAll() } returnsFluxOf updateEntities.map {
            ReactiveEvent(type = EventType.UPSERT, entity = it, entityType = "")
        }

        every { pictureRepository.findById(any()) } answers {
            Mono.just(fixture.next<PictureProjection>().copy(id = firstArg()))
        }

        val result: ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> = pictureEventsService.getAllDuplicateMatchesStream()

        fun verifyMatchUpdate(e: DuplicateMatchProjection?): Boolean {
            return e?.picture != null && e.pictureId == e.picture?.id
                    && e.target != null && e.targetId == e.target?.id
        }

        StepVerifier.create(result.filter { it.event() != "ping" }.take(4).map { it.data()?.entity })
                .expectNextMatches { verifyMatchUpdate(it) }
                .expectNextMatches { verifyMatchUpdate(it) }
                .expectNextMatches { verifyMatchUpdate(it) }
                .expectNextMatches { verifyMatchUpdate(it) }
                .verifyComplete()
    }

    @Test
    fun `getDuplicateMatchStreamByPictureId should result in a stream of matches for the given picture id, initial and on update`() {
        val sourcePictureId = PictureId()
        val initialEntities: List<DuplicateMatchProjection> = listOf(fixture.next(), fixture.next())
        val updateEntities: List<DuplicateMatchProjection> = listOf(fixture.next(), fixture.next())

        every { duplicateMatchRepository.findAllByPictureId(sourcePictureId.identifier) } returnsFluxOf initialEntities
        every { duplicateMatchRepository.subscribe(any()) } returnsFluxOf updateEntities.map {
            ReactiveEvent(type = EventType.UPSERT, entity = it, entityType = "")
        }

        every { pictureRepository.findById(any()) } answers {
            Mono.just(fixture.next<PictureProjection>().copy(id = firstArg()))
        }

        val result: ServerSentEventFlux<ReactiveEvent<DuplicateMatchProjection>> = pictureEventsService.getDuplicateMatchStreamByPictureId(sourcePictureId.identifier)

        fun verifyMatchUpdate(e: ServerSentEvent<ReactiveEvent<DuplicateMatchProjection>?>): Boolean {
            val entity: DuplicateMatchProjection? = e.data()?.entity
            return entity?.target != null && entity.targetId == entity.target?.id
        }

        StepVerifier.create(result.filter { it.event() != "ping" }.take(4))
                .expectNextMatches { verifyMatchUpdate(it) }
                .expectNextMatches { verifyMatchUpdate(it) }
                .expectNextMatches { verifyMatchUpdate(it) }
                .expectNextMatches { verifyMatchUpdate(it) }
                .verifyComplete()
    }
}

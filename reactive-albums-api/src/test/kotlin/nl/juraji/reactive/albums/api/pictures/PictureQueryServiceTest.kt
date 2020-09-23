package nl.juraji.reactive.albums.api.pictures

import com.marcellogalhardo.fixture.Fixture
import com.marcellogalhardo.fixture.next
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.PictureType
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import nl.juraji.reactive.albums.util.returnsEmptyMono
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class PictureQueryServiceTest {

    private val fixture = Fixture {
        register(ByteArray::class) { ByteArray(0) }
        register(PictureType::class) { PictureType.JPEG }
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @InjectMockKs
    private lateinit var pictureQueryService: PictureQueryService

    @Test
    fun `getPictures should result in a page of pictures, when no filter is set`() {
        val pageable: Pageable = PageRequest.of(0, 50)
        val pictures: List<PictureProjection> = listOf(fixture.next(), fixture.next(), fixture.next())

        every { pictureRepository.findAll(pageable) } returnsMonoOf PageImpl(pictures, pageable, 6)

        val result: Mono<Page<PictureProjection>> = pictureQueryService.getPictures(null, pageable)

        StepVerifier.create(result.map { it.content })
                .expectNext(pictures)
                .verifyComplete()
    }

    @Test
    fun `getPictures should result in a page of pictures, when filter on tag`() {
        val pageable: Pageable = PageRequest.of(0, 50)
        val pictures: List<PictureProjection> = listOf(fixture.next(), fixture.next(), fixture.next())
        val filter = "tag:some tag"

        every { pictureRepository.findAllByTagStartsWithIgnoreCase("some tag", pageable) } returnsMonoOf PageImpl(pictures, pageable, 6)

        val result: Mono<Page<PictureProjection>> = pictureQueryService.getPictures(filter, pageable)

        StepVerifier.create(result.map { it.content })
                .expectNext(pictures)
                .verifyComplete()
    }

    @Test
    fun `getPictures should result in a page of pictures, when generic filter`() {
        val pageable: Pageable = PageRequest.of(0, 50)
        val pictures: List<PictureProjection> = listOf(fixture.next(), fixture.next(), fixture.next())
        val filter = "generic filter input"

        every { pictureRepository.findAllByLocationContainsIgnoreCase(filter, pageable) } returnsMonoOf PageImpl(pictures, pageable, 6)

        val result: Mono<Page<PictureProjection>> = pictureQueryService.getPictures(filter, pageable)

        StepVerifier.create(result.map { it.content })
                .expectNext(pictures)
                .verifyComplete()
    }

    @Test
    fun `getPicture should result in the requested picture`() {
        val pictureId = PictureId()
        val picture: PictureProjection = fixture.next()

        every { pictureRepository.findById(pictureId.identifier) } returnsMonoOf picture

        val result = pictureQueryService.getPicture(pictureId.identifier)

        StepVerifier.create(result)
                .expectNext(picture)
                .verifyComplete()
    }

    @Test
    fun `getPicture should throw if requested picture not exists`() {
        val pictureId = PictureId()

        every { pictureRepository.findById(pictureId.identifier) }.returnsEmptyMono()

        val result = pictureQueryService.getPicture(pictureId.identifier)

        StepVerifier.create(result)
                .expectError(NoSuchEntityException::class.java)
                .verify()
    }
}

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
import nl.juraji.reactive.albums.query.thumbnails.Thumbnail
import nl.juraji.reactive.albums.query.thumbnails.repositories.ReactiveThumbnailRepository
import nl.juraji.reactive.albums.util.returnsEmptyMono
import nl.juraji.reactive.albums.util.returnsMonoOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class PictureImageServiceTest {

    private val fixture = Fixture {
        register(ByteArray::class) { ByteArray(0) }
        register(PictureType::class) { PictureType.JPEG }
        register(LocalDateTime::class) { LocalDateTime.now() }
    }

    @MockK
    private lateinit var pictureRepository: PictureRepository

    @MockK
    private lateinit var thumbnailRepository: ReactiveThumbnailRepository

    @InjectMockKs
    private lateinit var pictureImageService: PictureImageService

    @Test
    fun `getThumbnail should find picture thumbnails`() {
        val pictureId = PictureId()
        val thumbnail: Thumbnail = fixture.next()

        every { thumbnailRepository.findById(any()) } returnsMonoOf thumbnail

        val result: Mono<ByteArray> = pictureImageService.getThumbnail(pictureId.identifier)

        StepVerifier.create(result)
                .expectNext(thumbnail.thumbnail)
                .verifyComplete()
    }

    @Test
    fun `getThumbnail should throw if thumbnail does not exist`() {
        val pictureId = PictureId()

        every { thumbnailRepository.findById(any()) }.returnsEmptyMono()

        val result: Mono<ByteArray> = pictureImageService.getThumbnail(pictureId.identifier)

        StepVerifier.create(result)
                .expectError(NoSuchEntityException::class.java)
                .verify()
    }

    @Test
    fun `getPictureLocation should find picture location on disk`() {
        val pictureId = PictureId()
        val picture: PictureProjection = fixture.next()

        every { pictureRepository.findById(any()) } returnsMonoOf picture

        val result = pictureImageService.getPictureLocation(pictureId.identifier)

        StepVerifier.create(result)
                .expectNext(picture.location)
                .verifyComplete()
    }

    @Test
    fun `getPictureLocation should throw if picture not exists`() {
        val pictureId = PictureId()

        every { pictureRepository.findById(any()) }.returnsEmptyMono()

        val result: Mono<String> = pictureImageService.getPictureLocation(pictureId.identifier)

        StepVerifier.create(result)
                .expectError(NoSuchEntityException::class.java)
                .verify()
    }
}

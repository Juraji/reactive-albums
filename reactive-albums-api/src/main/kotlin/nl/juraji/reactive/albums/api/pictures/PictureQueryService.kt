package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.query.projections.repositories.PictureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class PictureQueryService(
        private val pictureRepository: PictureRepository,
) {

    fun getPictures(filter: String?, pageable: Pageable): Mono<Page<PictureProjection>> = when {
        filter.isNullOrBlank() -> pictureRepository.findAll(pageable)
        filter.startsWith("tag:") -> pictureRepository.findAllByTagStartsWithIgnoreCase(filter.substring(4), pageable)
        else -> pictureRepository.findAllByLocationContainsIgnoreCase(filter, pageable)
    }

    fun getPicture(pictureId: String): Mono<PictureProjection> = pictureRepository
            .findById(pictureId)
            .switchIfEmpty { Mono.error(NoSuchEntityException("Picture", pictureId)) }
}

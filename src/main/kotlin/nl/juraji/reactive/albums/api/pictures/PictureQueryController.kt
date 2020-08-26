package nl.juraji.reactive.albums.api.pictures

import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.handlers.FindAllPicturesQuery
import nl.juraji.reactive.albums.query.projections.handlers.PictureSearchParameter
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class PictureQueryController(
        private val queryGateway: QueryGateway,
) {

    @GetMapping("/api/pictures")
    fun getPictures(
            @RequestParam("q", required = false) search: PictureSearchParameter?,
    ): Flux<PictureProjection> {
        val query = queryGateway.subscriptionQuery(
                FindAllPicturesQuery(search),
                ResponseTypes.multipleInstancesOf(PictureProjection::class.java),
                ResponseTypes.instanceOf(PictureProjection::class.java)
        )

        return Flux.concat(
                query.initialResult().flatMapMany { Flux.fromIterable(it) },
                query.updates()
        )
    }
}

package nl.juraji.reactive.albums.services.analysis

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.domain.duplicates.DuplicateMatchId
import nl.juraji.reactive.albums.domain.duplicates.commands.LinkDuplicateCommand
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.query.projections.PictureProjection
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDuplicateMatchRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactivePictureRepository
import nl.juraji.reactive.albums.util.extensions.then
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
class DuplicateAnalysisService(
        private val pictureRepository: ReactivePictureRepository,
        private val commandGateway: CommandGateway,
        private val configuration: PicturesAggregateConfiguration,
) {

    fun analyzeDuplicates(pictureId: PictureId): Mono<Unit> {
        val others = pictureRepository
                .findAll()
                .filter { it.id !== pictureId.identifier && it.contentHash != null }
                .cache()

        return pictureRepository.findById(pictureId.identifier)
                .filter { it.contentHash == null }
                .switchIfEmpty { Mono.error(IllegalStateException("No content available for $pictureId")) }
                .flatMapMany { findDuplicates(it, others) }
                .flatMap { commandGateway.sendAndWait<Unit>(it).toMono() }
                .last()
    }

    private fun findDuplicates(picture: PictureProjection, others: Flux<PictureProjection>): Flux<LinkDuplicateCommand> {
        val sourceId = PictureId(picture.id)
        val sourceHash = picture.contentHash!!

        return others
                .map { compareHashes(sourceHash, it.contentHash!!, it.id) }
                .filter { (_, _, isMatch) -> isMatch }
                .map { (targetId, similarity) ->
                    LinkDuplicateCommand(
                            duplicateMatchId = DuplicateMatchId(),
                            sourceId = sourceId,
                            targetId = PictureId(targetId),
                            similarity = similarity
                    )
                }
    }

    fun compareHashes(source: BitSet, target: BitSet, targetId: String): Triple<String, Float, Boolean> {
        val similarity: Float =
                if (source == target) 1.0f
                else {
                    val xorOp = source.clone() as BitSet
                    xorOp.xor(target)
                    xorOp.flip(0, xorOp.length() - 1) // Convert to XNOR
                    val bitCount = xorOp.length().toFloat()
                    val similarBitCount = xorOp.cardinality().toFloat()
                    similarBitCount / bitCount
                }

        return targetId to similarity then (similarity >= configuration.duplicateSimilarity)
    }
}

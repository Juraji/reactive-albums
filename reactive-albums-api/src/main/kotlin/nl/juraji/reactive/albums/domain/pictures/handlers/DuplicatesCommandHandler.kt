package nl.juraji.reactive.albums.domain.pictures.handlers

import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.ExternalCommandHandler
import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.DuplicateMatchId
import nl.juraji.reactive.albums.domain.pictures.PictureAggregate
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.ScanDuplicatesCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UnlinkDuplicateCommand
import nl.juraji.reactive.albums.domain.pictures.events.ContentHashUpdatedEvent
import nl.juraji.reactive.albums.domain.pictures.events.PictureDeletedEvent
import nl.juraji.reactive.albums.query.projections.repositories.ContentHashRepository
import nl.juraji.reactive.albums.query.projections.repositories.ReactiveDuplicateMatchRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.modelling.command.Repository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class DuplicatesCommandHandler(
        @Qualifier("pictureAggregateRepository") repository: Repository<PictureAggregate>,
        private val contentHashRepository: ContentHashRepository,
        private val duplicateMatchRepository: ReactiveDuplicateMatchRepository,
        private val configuration: PicturesAggregateConfiguration,
        private val commandGateway: CommandGateway,
) : ExternalCommandHandler<PictureAggregate>(repository) {

    @CommandHandler
    fun handle(cmd: ScanDuplicatesCommand) = execute(cmd.pictureId) {
        val contentHash = cmd.contentHash ?: contentHashRepository
                .findById(cmd.pictureId.identifier)
                .map { it.contentHash }
                .orElseGet { Validate.fail { "No content hash available for ${cmd.pictureId}" } }

        contentHashRepository.findAll()
                .filter { it.pictureId != cmd.pictureId.identifier }
                .map { compareHashes(contentHash, it.pictureId, it.contentHash) }
                .filter { (_, similarity) -> similarity >= configuration.duplicateSimilarity }
                .forEach { (targetId, similarity) ->
                    this.runCatching { linkDuplicate(PictureId(targetId), similarity) }
                            .onFailure { logger.debug("Failed linking $targetId to ${cmd.pictureId}: ${it.message}") }
                }
    }

    @CommandHandler
    fun handle(cmd: UnlinkDuplicateCommand) = execute(cmd.pictureId) {
        unlinkDuplicate(cmd.matchId)
    }

    @EventHandler
    fun on(evt: ContentHashUpdatedEvent) {
        commandGateway.send<Unit>(ScanDuplicatesCommand(pictureId = evt.pictureId, contentHash = evt.contentHash))
    }

    @EventHandler
    fun on(evt: PictureDeletedEvent) {
        // Send unlink commands to all picture aggregates that have duplicate matches referencing the deleted picture
        duplicateMatchRepository
                .findAllByTargetId(evt.pictureId.identifier)
                .flatMap {
                    commandGateway.send<Unit>(UnlinkDuplicateCommand(
                            pictureId = PictureId(it.pictureId),
                            matchId = DuplicateMatchId(it.id)
                    )).toMono()
                }
                .subscribe()
    }

    private fun compareHashes(source: BitSet, targetId: String, target: BitSet): Pair<String, Float> {
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

        return targetId to similarity
    }

    companion object : LoggerCompanion(DuplicatesCommandHandler::class)
}

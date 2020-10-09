package nl.juraji.reactive.albums.domain.pictures.handlers

import nl.juraji.reactive.albums.api.CommandSenderService
import nl.juraji.reactive.albums.configuration.PicturesAggregateConfiguration
import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.Validate
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.LinkDuplicateCommand
import nl.juraji.reactive.albums.domain.pictures.commands.ScanDuplicatesCommand
import nl.juraji.reactive.albums.domain.pictures.events.ContentHashUpdatedEvent
import nl.juraji.reactive.albums.query.projections.repositories.SyncContentHashRepository
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service
import java.util.*

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class DuplicatesCommandHandler(
        commandGateway: CommandGateway,
        private val syncContentHashRepository: SyncContentHashRepository,
        private val configuration: PicturesAggregateConfiguration,
) : CommandSenderService(commandGateway) {

    @CommandHandler
    fun handle(cmd: ScanDuplicatesCommand): PictureId {
        val contentHash = cmd.contentHash ?: syncContentHashRepository
                .findById(cmd.pictureId.identifier)
                .map { it.contentHash }
                .orElseGet { Validate.fail { "No content hash available for ${cmd.pictureId}" } }

        syncContentHashRepository.findAll()
                .filter { it.pictureId != cmd.pictureId.identifier }
                .map { compareHashes(contentHash, it.pictureId, it.contentHash) }
                .filter { (_, similarity) -> similarity >= configuration.duplicateSimilarity }
                .forEach { (targetId, similarity) ->
                    send<Any>(LinkDuplicateCommand(
                            pictureId = cmd.pictureId,
                            targetId = PictureId(targetId),
                            similarity = similarity
                    ))
                }

        return cmd.pictureId
    }

    @EventHandler
    fun on(evt: ContentHashUpdatedEvent) {
        send<Unit>(ScanDuplicatesCommand(pictureId = evt.pictureId, contentHash = evt.contentHash))
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

package nl.juraji.reactive.albums.domain.pictures.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.ExternalCommandHandler
import nl.juraji.reactive.albums.domain.pictures.PictureAggregate
import nl.juraji.reactive.albums.domain.pictures.commands.AddTagCommand
import nl.juraji.reactive.albums.domain.pictures.commands.AutoTagPictureCommand
import nl.juraji.reactive.albums.domain.pictures.commands.RemoveTagCommand
import nl.juraji.reactive.albums.domain.pictures.events.PictureCreatedEvent
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.RgbColor
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.modelling.command.Repository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureTagsCommandHandler(
        @Qualifier("pictureAggregateRepository") repository: Repository<PictureAggregate>,
        private val commandGateway: CommandGateway
) : ExternalCommandHandler<PictureAggregate>(repository) {

    @CommandHandler
    fun handle(cmd: AutoTagPictureCommand) = execute(cmd.pictureId) {
        logger.debug("Generating tags for ${getLocation()}")

        getLocation().parent
                .map { it.fileName.toString() }
                .forEach { label ->
                    val labelColor: RgbColor = RgbColor.of(label)
                    val textColor: RgbColor = labelColor.contrastColor()

                    addTag(
                            label = label,
                            labelColor = labelColor.toHexString(),
                            textColor = textColor.toHexString(),
                            tagLinkType = nl.juraji.reactive.albums.domain.pictures.TagLinkType.AUTO
                    )
                }
    }

    @CommandHandler
    fun handle(cmd: AddTagCommand) = execute(cmd.pictureId) {
        addTag(
                label = cmd.label,
                labelColor = cmd.labelColor,
                textColor = cmd.textColor,
                tagLinkType = cmd.tagLinkType
        )
    }

    @CommandHandler
    fun handle(cmd: RemoveTagCommand) = execute(cmd.pictureId) {
        removeTag(label = cmd.label)
    }

    @EventHandler
    fun on(evt: PictureCreatedEvent) {
        commandGateway.send<Unit>(AutoTagPictureCommand(pictureId = evt.pictureId))
    }

    companion object : LoggerCompanion(PictureTagsCommandHandler::class)
}

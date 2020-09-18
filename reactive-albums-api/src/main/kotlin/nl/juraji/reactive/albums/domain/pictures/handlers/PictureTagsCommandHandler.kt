package nl.juraji.reactive.albums.domain.pictures.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.ExternalCommandHandler
import nl.juraji.reactive.albums.domain.pictures.PictureAggregate
import nl.juraji.reactive.albums.domain.pictures.commands.LinkTagCommand
import nl.juraji.reactive.albums.domain.pictures.commands.UnlinkTagCommand
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.command.Repository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class PictureTagsCommandHandler(
        @Qualifier("pictureAggregateRepository") repository: Repository<PictureAggregate>,
) : ExternalCommandHandler<PictureAggregate>(repository) {

    @CommandHandler
    fun handle(cmd: LinkTagCommand) = execute(cmd.pictureId) {
        addTag(
                tagId = cmd.tagId,
                tagLinkType = cmd.tagLinkType
        )
    }

    @CommandHandler
    fun handle(cmd: UnlinkTagCommand) = execute(cmd.pictureId) {
        removeTag(tagId = cmd.tagId)
    }

    companion object : LoggerCompanion(PictureTagsCommandHandler::class)
}

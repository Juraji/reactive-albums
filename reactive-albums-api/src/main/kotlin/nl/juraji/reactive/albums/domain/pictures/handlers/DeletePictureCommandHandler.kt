package nl.juraji.reactive.albums.domain.pictures.handlers

import nl.juraji.reactive.albums.configuration.ProcessingGroups
import nl.juraji.reactive.albums.domain.ExternalCommandHandler
import nl.juraji.reactive.albums.domain.pictures.PictureAggregate
import nl.juraji.reactive.albums.domain.pictures.PictureId
import nl.juraji.reactive.albums.domain.pictures.commands.DeletePictureCommand
import nl.juraji.reactive.albums.services.FileSystemService
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.command.Repository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
@ProcessingGroup(ProcessingGroups.PICTURE_ANALYSIS)
class DeletePictureCommandHandler(
        @Qualifier("pictureAggregateRepository") repository: Repository<PictureAggregate>,
        private val fileSystemService: FileSystemService,
) : ExternalCommandHandler<PictureAggregate, PictureId>(repository) {

    @CommandHandler
    fun handle(cmd: DeletePictureCommand) = execute(cmd.pictureId) {
        fileSystemService.deleteIfExists(getLocation())
        deletePicture()
    }
}

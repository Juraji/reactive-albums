package nl.juraji.reactive.albums.domain.duplicates.commands

import nl.juraji.reactive.albums.domain.duplicates.DuplicateMatchId

data class UnlinkDuplicateCommand(
        override val duplicateMatchId: DuplicateMatchId,
) : DuplicateMatchCommand(duplicateMatchId)

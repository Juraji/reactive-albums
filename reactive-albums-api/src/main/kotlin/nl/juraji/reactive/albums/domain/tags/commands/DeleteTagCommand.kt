package nl.juraji.reactive.albums.domain.tags.commands

import nl.juraji.reactive.albums.domain.tags.TagId

data class DeleteTagCommand(
        override val tagId: TagId,
) : TagCommand(tagId)

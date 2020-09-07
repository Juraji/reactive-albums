package nl.juraji.reactive.albums.domain.duplicates.events

import nl.juraji.reactive.albums.domain.duplicates.DuplicateMatchId

interface DuplicateMatchEvent {
    val duplicateMatchId: DuplicateMatchId
}

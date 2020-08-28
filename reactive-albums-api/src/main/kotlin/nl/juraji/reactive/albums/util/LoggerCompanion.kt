package nl.juraji.reactive.albums.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class LoggerCompanion {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
}

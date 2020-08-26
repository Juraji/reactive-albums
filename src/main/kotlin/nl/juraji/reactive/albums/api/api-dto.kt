package nl.juraji.reactive.albums.api

import org.springframework.http.HttpStatus

data class ApiErrorResult(
        val status: HttpStatus,
        val message: String,
)

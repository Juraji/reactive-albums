package nl.juraji.reactive.albums.api

import com.fasterxml.jackson.databind.ObjectMapper
import nl.juraji.reactive.albums.domain.ValidationException
import nl.juraji.reactive.albums.projections.NoSuchEntityException
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import java.io.FileNotFoundException

@Order(-2)
@Configuration
class RestControllerExceptionHandler(
        private val objectMapper: ObjectMapper,
) : WebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return when (ex) {
            is ValidationException -> handleException(exchange, ex, HttpStatus.UNPROCESSABLE_ENTITY)
            is NoSuchEntityException -> handleException(exchange, ex, HttpStatus.NOT_FOUND)
            is FileNotFoundException -> handleException(exchange, ex, HttpStatus.NOT_FOUND)
            else -> {
                logger.error("Error running request: ${exchange.request.path}", ex)
                handleException(exchange, ex, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }

    private fun handleException(exchange: ServerWebExchange, ex: Throwable, status: HttpStatus): Mono<Void> {
        exchange.response.statusCode = status

        return if (exchange.request.headers.accept.contains(MediaType.APPLICATION_JSON)) {
            val body: Mono<DataBuffer> = Mono
                    .just(ApiError(status = status.value(), message = ex.localizedMessage))
                    .map { objectMapper.writeValueAsBytes(it) }
                    .map { exchange.response.bufferFactory().wrap(it) }

            exchange.response.writeWith(body)
        } else {
            exchange.response.setComplete()
        }
    }

    companion object : LoggerCompanion(RestControllerExceptionHandler::class)
}

data class ApiError(
        val status: Int,
        val message: String,
)

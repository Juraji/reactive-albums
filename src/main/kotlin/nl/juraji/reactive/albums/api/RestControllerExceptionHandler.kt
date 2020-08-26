package nl.juraji.reactive.albums.api

import com.fasterxml.jackson.databind.ObjectMapper
import nl.juraji.reactive.albums.domain.ValidationException
import nl.juraji.reactive.albums.query.projections.handlers.DuplicateEntityException
import nl.juraji.reactive.albums.query.projections.handlers.NoSuchEntityException
import nl.juraji.reactive.albums.util.LoggerCompanion
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
class RestControllerExceptionHandler(
        private val objectMapper: ObjectMapper,
) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON;
        return exchange.response.writeWith(Mono.create {
            val result: ApiErrorResult = when (ex) {
                is ValidationException -> handleValidationException(ex)
                is DuplicateEntityException -> handleDuplicateEntityException(ex)
                is NoSuchEntityException -> handleNoSuchEntityException(ex)
                is IncorrectResultSizeDataAccessException -> handleIncorrectResultSizeDataAccessException(ex)
                else -> handleDefault(ex)
            }

            val bufferFactory: DataBufferFactory = exchange.response.bufferFactory();
            it.success(bufferFactory.wrap(objectMapper.writeValueAsBytes(result)))
        })
    }

    fun handleValidationException(ex: ValidationException): ApiErrorResult {
        logger.trace(ex.localizedMessage, ex)
        return ApiErrorResult(status = HttpStatus.BAD_REQUEST, message = ex.localizedMessage)
    }

    fun handleDuplicateEntityException(ex: DuplicateEntityException): ApiErrorResult {
        val msg = ex.localizedMessage ?: "${ex.entityName} already exists with id ${ex.entityId.identifier}"
        logger.trace(msg, ex)
        return ApiErrorResult(status = HttpStatus.CONFLICT, message = msg)
    }

    fun handleNoSuchEntityException(ex: NoSuchEntityException): ApiErrorResult {
        val msg = ex.localizedMessage ?: "${ex.entityName} not found by id ${ex.entityId.identifier}"
        logger.trace(msg, ex)
        return ApiErrorResult(status = HttpStatus.NOT_FOUND, message = msg)
    }

    fun handleIncorrectResultSizeDataAccessException(ex: IncorrectResultSizeDataAccessException): ApiErrorResult =
            ApiErrorResult(status = HttpStatus.CONFLICT, message = ex.localizedMessage)

    private fun handleDefault(ex: Throwable): ApiErrorResult =
            ApiErrorResult(status = HttpStatus.INTERNAL_SERVER_ERROR, message = ex.localizedMessage)

    companion object : LoggerCompanion() {
    }
}

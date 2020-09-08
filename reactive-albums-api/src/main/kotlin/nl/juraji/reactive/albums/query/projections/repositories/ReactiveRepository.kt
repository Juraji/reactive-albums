package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.util.extensions.deferFrom
import nl.juraji.reactive.albums.util.extensions.deferFromIterable
import nl.juraji.reactive.albums.util.extensions.deferFromOptional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.kotlin.core.publisher.toMono
import java.util.*

abstract class ReactiveRepository<T : JpaRepository<E, ID>, E, ID>(
        private val repository: T,
        private val scheduler: Scheduler,
        private val transactionTemplate: TransactionTemplate,
) {
    private val updatesProcessor: DirectProcessor<ReactiveEvent<E>> = DirectProcessor.create()

    fun findById(id: ID): Mono<E> = fromOptional { it.findById(id) }

    fun findAll(): Flux<E> = fromIterator { it.findAll() }

    fun save(entity: E): Mono<E> =
            executeInTransaction { it.save(entity) }
                    .doOnNext { emitUpdate(EventType.UPDATE, it) }

    fun deleteById(id: ID): Mono<E> = findById(id).flatMap { delete(it) }

    fun delete(entity: E): Mono<E> =
            executeInTransaction { it.delete(entity) }
                    .map { entity }
                    .doOnNext { emitUpdate(EventType.DELETE, it) }

    fun update(id: ID, update: (E) -> E): Mono<E> = findById(id).flatMap { save(update(it)) }

    fun subscribeToAll(): Flux<ReactiveEvent<E>> = updatesProcessor

    fun subscribe(filter: (E) -> Boolean): Flux<ReactiveEvent<E>> = updatesProcessor.filter { filter(it.entity) }

    fun subscribeFirst(filter: (E) -> Boolean): Mono<E> =
            subscribe(filter).map { it.entity }.toMono()

    protected fun <R> from(f: (T) -> R?): Mono<R> =
            deferFrom(scheduler) { f(repository) }

    protected fun <R> fromOptional(f: (T) -> Optional<R>): Mono<R> =
            deferFromOptional(scheduler) { f(repository) }

    protected fun <R> fromIterator(f: (T) -> Iterable<R>): Flux<R> =
            deferFromIterable(scheduler) { f(repository) }

    private fun <R> executeInTransaction(f: (T) -> R): Mono<R> =
            deferFrom(scheduler) { transactionTemplate.execute { f(repository) } }

    private fun emitUpdate(type: EventType, entity: E) = updatesProcessor
            .onNext(ReactiveEvent(
                    type = type,
                    entity = entity,
                    entityType = entity?.let { it::class.simpleName } ?: "Entity"
            ))
}

data class ReactiveEvent<T>(
        val type: EventType,
        val entityType: String,
        val entity: T,
)

enum class EventType {
    UPDATE, DELETE
}

package nl.juraji.reactive.albums.query.projections.repositories

import nl.juraji.reactive.albums.util.extensions.deferIterableTo
import nl.juraji.reactive.albums.util.extensions.deferOptionalTo
import nl.juraji.reactive.albums.util.extensions.deferTo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.*

abstract class ReactiveRepository<T : JpaRepository<E, ID>, E : Any, ID>(
        private val repository: T,
        private val scheduler: Scheduler,
        private val transactionTemplate: TransactionTemplate,
) {
    private val updatesProcessor: DirectProcessor<ReactiveEvent<E>> = DirectProcessor.create()

    fun findById(id: ID): Mono<E> = fromOptional { it.findById(id) }

    fun findAll(): Flux<E> = fromIterator { it.findAll() }

    fun findAll(pageable: Pageable): Mono<Page<E>> = from { it.findAll(pageable) }

    fun save(entity: E): Mono<E> =
            executeInTransaction { it.save(entity) }
                    .doOnNext { emitUpdate(EventType.UPSERT, it) }

    fun deleteById(id: ID): Mono<E> = findById(id).flatMap { delete(it) }

    fun delete(entity: E): Mono<E> =
            executeInTransaction { it.delete(entity) }
                    .map { entity }
                    .doOnNext { emitUpdate(EventType.DELETE, it) }

    fun deleteAll(): Mono<Unit> = executeInTransaction { it.deleteAll() }

    fun update(id: ID, update: (E) -> E): Mono<E> = findById(id).flatMap { save(update(it)) }

    fun subscribeToAll(): Flux<ReactiveEvent<E>> = updatesProcessor

    fun subscribe(filter: (E) -> Boolean): Flux<ReactiveEvent<E>> = updatesProcessor
            .filter { filter(it.entity) }

    fun subscribeFirst(timeout: Duration, filter: (E) -> Boolean): Mono<E> =
            subscribe(filter).map { it.entity }.toMono().timeout(timeout)

    fun getRepository(): T = repository

    protected fun <R> from(f: (T) -> R?): Mono<R> =
            deferTo(scheduler) { f(repository) }

    protected fun <R> fromOptional(f: (T) -> Optional<R>): Mono<R> =
            deferOptionalTo(scheduler) { f(repository) }

    protected fun <R> fromIterator(f: (T) -> Iterable<R>): Flux<R> =
            deferIterableTo(scheduler) { f(repository) }

    private fun <R> executeInTransaction(f: (T) -> R): Mono<R> =
            deferTo(scheduler) { transactionTemplate.execute { f(repository) } }

    private fun emitUpdate(type: EventType, entity: E) = updatesProcessor
            .onNext(ReactiveEvent(type, entity))
}

data class ReactiveEvent<T : Any>(
        val type: EventType,
        val entity: T,
)

enum class EventType {
    UPSERT, DELETE
}

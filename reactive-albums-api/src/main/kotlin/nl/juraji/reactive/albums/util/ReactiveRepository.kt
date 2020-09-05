package nl.juraji.reactive.albums.util

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

    fun save(instance: E): Mono<E> =
            executeInTransaction { it.save(instance) }
                    .doOnNext { updatesProcessor.onNext(ReactiveEvent.updated(it)) }

    fun deleteById(id: ID): Mono<E> = findById(id).flatMap { delete(it) }

    fun delete(entity: E): Mono<E> =
            executeInTransaction { it.delete(entity) }
                    .map { entity }
                    .doOnNext { updatesProcessor.onNext(ReactiveEvent.deleted(it)) }

    fun update(id: ID, update: (E) -> E): Mono<E> = findById(id).flatMap { save(update(it)) }

    fun subscribeToAll(): Flux<ReactiveEvent<E>> = updatesProcessor

    fun subscribe(filter: (E) -> Boolean): Flux<ReactiveEvent<E>> = updatesProcessor.filter { filter(it.entity) }

    fun subscribeFirst(filter: (E) -> Boolean): Mono<E> = updatesProcessor
            .filter { filter(it.entity) }
            .map { it.entity }
            .toMono()

    protected fun <R> from(f: (T) -> R?): Mono<R> =
            deferFrom(scheduler) { f(repository) }

    protected fun <R> fromOptional(f: (T) -> Optional<R>): Mono<R> =
            deferFromOptional(scheduler) { f(repository) }

    protected fun <R> fromIterator(f: (T) -> Iterable<R>): Flux<R> =
            deferFromIterable(scheduler) { f(repository) }

    protected fun <R> executeInTransaction(f: (T) -> R): Mono<R> =
            deferFrom(scheduler) { transactionTemplate.execute { f(repository) } }

}

data class ReactiveEvent<T>(
        val type: ReactiveEventType,
        val entity: T,
) {
    companion object {
        fun <T> updated(ent: T) = ReactiveEvent(type = ReactiveEventType.UPDATE, entity = ent)
        fun <T> deleted(ent: T) = ReactiveEvent(type = ReactiveEventType.DELETE, entity = ent)
    }
}

enum class ReactiveEventType {
    UPDATE, DELETE
}

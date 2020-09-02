package nl.juraji.reactive.albums.util

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.util.*

abstract class ReactiveRepository<T : JpaRepository<E, ID>, E, ID>(
        private val repository: T,
        private val scheduler: Scheduler,
        private val transactionTemplate: TransactionTemplate,
) {
    fun findById(id: ID): Mono<E> = fromOptional { it.findById(id) }

    fun findAll(): Flux<E> = fromIterator { it.findAll() }

    fun save(instance: E): Mono<E> = executeInTransaction { it.save(instance) }

    protected fun <R> from(f: (T) -> R?): Mono<R> =
            Mono.defer {
                Mono.justOrEmpty(f(repository))
            }.subscribeOn(scheduler)

    protected fun <R> fromOptional(f: (T) -> Optional<R>): Mono<R> =
            Mono.defer {
                Mono.justOrEmpty(f(repository))
            }.subscribeOn(scheduler)

    protected fun <R> fromIterator(f: (T) -> Iterable<R>): Flux<R> =
            Flux.defer {
                Flux.fromIterable(f(repository))
            }.subscribeOn(scheduler)

    protected fun <R> executeInTransaction(f: (T) -> R): Mono<R> =
            from { rep -> transactionTemplate.execute { f(rep) } }
}

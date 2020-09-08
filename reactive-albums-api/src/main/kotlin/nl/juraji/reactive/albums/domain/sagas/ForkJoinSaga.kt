package nl.juraji.reactive.albums.domain.sagas

import org.axonframework.modelling.saga.SagaLifecycle
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

abstract class ForkJoinSaga<K : Serializable>(
        private val endEventAssociationKey: String,
) {
    private val endEventCounter = AtomicInteger(0)

    protected fun forkedEventKey(key: K) {
        SagaLifecycle.associateWith(endEventAssociationKey, key.toString())
        endEventCounter.incrementAndGet()
    }

    protected fun onForkedEventHandled(key: K) {
        SagaLifecycle.removeAssociationWith(endEventAssociationKey, key.toString())
        val endEventsLeft: Int = endEventCounter.decrementAndGet()
        if (endEventsLeft == 0) {
            SagaLifecycle.end()
        }
    }
}

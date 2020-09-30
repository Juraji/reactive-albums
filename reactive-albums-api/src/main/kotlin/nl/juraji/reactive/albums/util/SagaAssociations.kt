package nl.juraji.reactive.albums.util

import org.axonframework.modelling.saga.AnnotatedSaga
import org.axonframework.modelling.saga.AssociationValues
import org.axonframework.modelling.saga.SagaLifecycle

object SagaAssociations {
    fun hasAssociation(associationKey: String, associationValue: String): Boolean =
            getAssociations().any { it.key == associationKey && it.value == associationValue }

    fun hasAssociationKey(associationKey: String): Boolean =
            getAssociations().any { it.key == associationKey }

    fun getAssociatedValues(associationKey: String): List<String> =
            getAssociations().filter { it.key == associationKey }.map { it.value }

    fun <T> getAssociatedValues(associationKey: String, mapper: (String) -> T): List<T> =
            getAssociatedValues(associationKey).map(mapper)

    fun getAssociatedValue(associationKey: String): String? =
            getAssociations().firstOrNull { it.key == associationKey }?.value

    fun <T> getAssociatedValue(associationKey: String, mapper: (String) -> T): T? =
            getAssociatedValue(associationKey)?.let(mapper)

    fun associateWith(associationKey: String, associationValue: String) =
            SagaLifecycle.associateWith(associationKey, associationValue)

    fun removeAssociationWith(associationKey: String, associationValue: String) =
            SagaLifecycle.removeAssociationWith(associationKey, associationValue)

    private fun getAssociations(): AssociationValues =
            SagaLifecycle.getCurrentScope<AnnotatedSaga<Any>>().associationValues
}



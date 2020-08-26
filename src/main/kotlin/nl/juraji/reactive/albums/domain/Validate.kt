package nl.juraji.reactive.albums.domain

object Validate {

    fun isTrue(assertion: Boolean, message: () -> String) {
        if (!assertion) throw ValidationException(message())
    }

    fun isFalse(assertion: Boolean, message: () -> String) {
        if (assertion) throw ValidationException(message())
    }

    fun isNotNullOrBlank(value: String?, message: () -> String) {
        if (value.isNullOrBlank()) throw ValidationException(message())
    }

    fun isNotNull(value: Any?, message: () -> String) {
        if (value == null) throw ValidationException(message())
    }

    fun isNotEqual(a: Any, b: Any, message: () -> String) {
        if (a == b) throw ValidationException(message())
    }

    fun isNotEmpty(collection: Collection<Any>, message: () -> String) {
        if (collection.isEmpty()) throw ValidationException(message())
    }

    fun <T> anyMatch(collection: Collection<T>, predicate: (T) -> Boolean, message: () -> String) {
        if (collection.none(predicate)) throw ValidationException(message())
    }

    fun <T> noneMatch(collection: Collection<T>, predicate: (T) -> Boolean, message: () -> String) {
        if (collection.any(predicate)) throw ValidationException(message())
    }
}

data class ValidationException(
        override val message: String,
) : IllegalArgumentException(message)

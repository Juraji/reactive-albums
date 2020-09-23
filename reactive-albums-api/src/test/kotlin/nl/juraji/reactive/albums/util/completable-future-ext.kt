package nl.juraji.reactive.albums.util

import java.util.concurrent.CompletableFuture

fun <T> T.toCompletableFuture(): CompletableFuture<T> = CompletableFuture.completedFuture(this)

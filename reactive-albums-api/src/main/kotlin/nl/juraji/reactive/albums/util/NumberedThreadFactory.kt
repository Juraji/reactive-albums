package nl.juraji.reactive.albums.util

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class NumberedThreadFactory(
        private val baseName: String,
) : ThreadFactory {
    private val serial = AtomicInteger(0)

    override fun newThread(r: Runnable): Thread = Thread(r, "$baseName-${serial.getAndIncrement()}")
}

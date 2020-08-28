package nl.juraji.reactive.albums

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("nl.juraji.reactive.albums.configuration")
class ReactiveAlbumsApplication

fun main(args: Array<String>) {
    runApplication<ReactiveAlbumsApplication>(*args)
}

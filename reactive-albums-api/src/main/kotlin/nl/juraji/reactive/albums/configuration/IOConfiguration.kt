package nl.juraji.reactive.albums.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class IOConfiguration {

    @Bean(name = ["IOScheduler"])
    fun jdbcScheduler(): Scheduler {
        val pool: ExecutorService = Executors.newCachedThreadPool() { Thread(it, "io-scheduler") }
        return Schedulers.fromExecutor(pool)
    }
}

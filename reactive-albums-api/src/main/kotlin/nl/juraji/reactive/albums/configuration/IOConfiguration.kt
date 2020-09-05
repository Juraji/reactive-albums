package nl.juraji.reactive.albums.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors

@Configuration
class IOConfiguration {

    @Bean(name=["IOScheduler"])
    fun jdbcScheduler(): Scheduler =
            Schedulers.fromExecutor(Executors.newCachedThreadPool())
}

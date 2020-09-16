package nl.juraji.reactive.albums.configuration

import nl.juraji.reactive.albums.util.NumberedThreadFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Configuration
class IOConfiguration {

    @Bean(name = ["fileIoExecutor"])
    fun fileIoExecutor(): ExecutorService = Executors.newCachedThreadPool(NumberedThreadFactory("io-scheduler"))

    @Bean(name = ["fileIoScheduler"])
    fun fileIoScheduler(
            @Qualifier("fileIoExecutor") executor: ExecutorService,
    ): Scheduler = Schedulers.fromExecutor(executor)

    @Bean(name = ["fileWatchExecutor"])
    fun fileWatchExecutor(): ExecutorService = Executors.newCachedThreadPool(NumberedThreadFactory("file-watch"))

    @Bean(name = ["fileWatchScheduler"])
    fun fileWatchScheduler(
            @Qualifier("fileWatchExecutor") fileWatchExecutor: ExecutorService,
    ): Scheduler = Schedulers.fromExecutor(fileWatchExecutor)
}

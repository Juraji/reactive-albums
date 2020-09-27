package nl.juraji.reactive.albums.configuration.db.tenants

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import nl.juraji.reactive.albums.configuration.db.MultiTenancyConfiguration
import nl.juraji.reactive.albums.configuration.db.Tenant
import nl.juraji.reactive.albums.util.NumberedThreadFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "thumbnailsEntityManagerFactory",
        transactionManagerRef = "thumbnailsTransactionManager",
        basePackages = ["nl.juraji.reactive.albums.query.thumbnails.repositories"]
)
class ThumbnailsJpaConfiguration(
        multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tenant: Tenant = multiTenancyConfiguration.findTenant("thumbnails")

    @Bean(name = ["thumbnailsScheduler"])
    fun jdbcScheduler(): Scheduler {
        val pool: ExecutorService = Executors.newFixedThreadPool(
                tenant.connectionCount,
                NumberedThreadFactory("thumbnails-scheduler")
        )
        return Schedulers.fromExecutor(pool)
    }

    @Bean(name = ["thumbnailsDataSource"])
    fun dataSource(): DataSource {
        val config = HikariConfig()

        config.jdbcUrl = tenant.url
        config.username = tenant.username
        config.password = tenant.password
        config.poolName = "hikari-thumbnails"
        config.maximumPoolSize = tenant.connectionCount

        return HikariDataSource(config)
    }

    @Bean(name = ["thumbnailsEntityManagerFactory"])
    fun thumbnailsEntityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("thumbnailsDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
                .dataSource(dataSource)
                .packages("nl.juraji.reactive.albums.query.thumbnails")
                .persistenceUnit("thumbnails")
                .build()
    }

    @Bean(name = ["thumbnailsTransactionManager"])
    fun thumbnailsTransactionManager(
            @Qualifier("thumbnailsEntityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}

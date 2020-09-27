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
        entityManagerFactoryRef = "projectionsEntityManagerFactory",
        transactionManagerRef = "projectionsTransactionManager",
        basePackages = ["nl.juraji.reactive.albums.query.projections.repositories"]
)
class ProjectionsJpaConfiguration(
        multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tenant: Tenant = multiTenancyConfiguration.findTenant("projections")

    @Bean(name = ["projectionsScheduler"])
    fun jdbcScheduler(): Scheduler {
        val pool: ExecutorService = Executors.newFixedThreadPool(
                tenant.connectionCount,
                NumberedThreadFactory("projections-scheduler")
        )
        return Schedulers.fromExecutor(pool)
    }

    @Bean(name = ["projectionsDataSource"])
    fun dataSource(): DataSource {
        val config = HikariConfig()

        config.jdbcUrl = tenant.url
        config.username = tenant.username
        config.password = tenant.password
        config.poolName = "hikari-projections"
        config.maximumPoolSize = tenant.connectionCount

        return HikariDataSource(config)
    }

    @Bean(name = ["projectionsEntityManagerFactory"])
    fun projectionsEntityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("projectionsDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
                .dataSource(dataSource)
                .packages("nl.juraji.reactive.albums.query.projections")
                .persistenceUnit("projections")
                .build()
    }

    @Bean(name = ["projectionsTransactionManager"])
    fun projectionsTransactionManager(
            @Qualifier("projectionsEntityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}

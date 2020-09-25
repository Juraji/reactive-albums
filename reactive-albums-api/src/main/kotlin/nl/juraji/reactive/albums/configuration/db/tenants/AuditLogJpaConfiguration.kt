package nl.juraji.reactive.albums.configuration.db.tenants

import nl.juraji.reactive.albums.configuration.db.MultiTenancyConfiguration
import nl.juraji.reactive.albums.configuration.db.Tenant
import nl.juraji.reactive.albums.util.NumberedThreadFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
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
        entityManagerFactoryRef = "auditLogEntityManagerFactory",
        transactionManagerRef = "auditLogTransactionManager",
        basePackages = ["nl.juraji.reactive.albums.query.audit.repositories"]
)
class AuditLogJpaConfiguration(
        multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tennant: Tenant = multiTenancyConfiguration.findTenant("auditLog")

    @Bean(name = ["auditLogScheduler"])
    fun jdbcScheduler(): Scheduler {
        val pool: ExecutorService = Executors.newFixedThreadPool(tennant.connectionCount, NumberedThreadFactory("auditLog-scheduler"))
        return Schedulers.fromExecutor(pool)
    }

    @Bean(name = ["auditLogDataSource"])
    fun dataSource(): DataSource {
        val (url, username, password) = tennant
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build()
    }

    @Bean(name = ["auditLogEntityManagerFactory"])
    fun auditLogEntityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("auditLogDataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
                .dataSource(dataSource)
                .packages("nl.juraji.reactive.albums.query.audit")
                .persistenceUnit("auditLog")
                .build()
    }

    @Bean(name = ["auditLogTransactionManager"])
    fun auditLogTransactionManager(
            @Qualifier("auditLogEntityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}

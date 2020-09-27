package nl.juraji.reactive.albums.configuration.db.tenants

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import nl.juraji.reactive.albums.configuration.db.MultiTenancyConfiguration
import nl.juraji.reactive.albums.configuration.db.Tenant
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class EventSourcingJpaConfiguration(
        private val multiTenancyConfiguration: MultiTenancyConfiguration,
) {
    private val tenant: Tenant = multiTenancyConfiguration.findTenant("eventsourcing")

    @Primary
    @Bean(name = ["dataSource"])
    fun dataSource(): DataSource {
        val config = HikariConfig()

        config.jdbcUrl = tenant.url
        config.username = tenant.username
        config.password = tenant.password
        config.poolName = "hikari-eventsourcing"
        config.maximumPoolSize = tenant.connectionCount

        return HikariDataSource(config)
    }

    @Primary
    @Bean(name = ["entityManagerFactory"])
    fun projectionsEntityManagerFactory(
            builder: EntityManagerFactoryBuilder,
            @Qualifier("dataSource") dataSource: DataSource,
    ): LocalContainerEntityManagerFactoryBean {
        return builder
                .dataSource(dataSource)
                .packages(
                        "org.axonframework.eventhandling.tokenstore",
                        "org.axonframework.modelling.saga.repository.jpa",
                        "org.axonframework.eventsourcing.eventstore.jpa"
                )
                .persistenceUnit("eventsourcing")
                .build()
    }

    @Primary
    @Bean("transactionManager")
    fun projectionsTransactionManager(
            @Qualifier("entityManagerFactory") entityManagerFactory: EntityManagerFactory,
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}

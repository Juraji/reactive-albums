package nl.juraji.reactive.albums.configuration.db.tenants

import nl.juraji.reactive.albums.configuration.db.MultiTenancyConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
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

    @Primary
    @Bean(name = ["dataSource"])
    fun dataSource(): DataSource {
        val (url, username, password) = multiTenancyConfiguration.findTenant("eventsourcing")
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build()
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

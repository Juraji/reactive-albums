package nl.juraji.reactive.albums.configuration.db.tenants

import nl.juraji.reactive.albums.configuration.db.MultiTenancyConfiguration
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
        private val multiTenancyConfiguration: MultiTenancyConfiguration,
) {

    @Bean(name = ["projectionsDataSource"])
    fun dataSource(): DataSource {
        val (url, username, password) = multiTenancyConfiguration.findTenant("projections")
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build()
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

package nl.juraji.reactive.albums.configuration.db

import nl.juraji.reactive.albums.util.LoggerCompanion
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
class MultiTenantFlywayMigrations(
        @Qualifier("taskExecutor") private val taskExecutor: TaskExecutor?,
        private val multiTenancyConfiguration: MultiTenancyConfiguration,
) : InitializingBean {

    override fun afterPropertiesSet() {
        runOnAllDataSources()
    }

    private fun runOnAllDataSources() {
        val tenants = multiTenancyConfiguration.tenants
        logger.info("Running Flyway migrations for ${tenants.size} tenants...")

        tenants
                .filter { it.flyway != null && it.flyway.enabled }
                .forEach { tenant ->
                    val flyway = createFlywayMigrator(tenant)

                    if (taskExecutor != null) {
                        taskExecutor.execute {
                            logger.warn("Running Flyway for [${tenant.tenantId}] asynchronously, your database might not be ready at startup!")
                            flyway.migrate()
                        }
                    } else {
                        logger.info("Running flyway for [${tenant.tenantId}] synchronously")
                        flyway.migrate()
                    }
                }
    }

    private fun createFlywayMigrator(tenant: Tenant): Flyway {
        val flywayConfiguration = tenant.flyway
                ?: throw IllegalArgumentException("Flyway configuration can not null during Flyway initialization")

        return Flyway.configure()
                .baselineOnMigrate(flywayConfiguration.baselineOnMigrate)
                .dataSource(tenant.url, tenant.username, tenant.password)
                .locations(*flywayConfiguration.locations.toTypedArray())
                .load()
    }

    companion object : LoggerCompanion(MultiTenantFlywayMigrations::class)
}

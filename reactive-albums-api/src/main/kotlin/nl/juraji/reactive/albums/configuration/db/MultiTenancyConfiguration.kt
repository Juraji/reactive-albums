package nl.juraji.reactive.albums.configuration.db

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "multi-tenancy")
data class MultiTenancyConfiguration(
        val tenants: List<Tenant> = emptyList(),
) {

    fun findTenant(tenantId: String): Tenant {
        return tenants.find { it.tenantId == tenantId }
                ?: throw IllegalArgumentException("Tenant with id $tenantId does not exist!")
    }
}

@ConstructorBinding
data class Tenant(
        val url: String,
        val username: String,
        val password: String,
        val flyway: TenantFlywayProperties?,
        val tenantId: String,
        val connectionCount: Int = 32,
)

@ConstructorBinding
data class TenantFlywayProperties(
        val enabled: Boolean,
        val baselineOnMigrate: Boolean,
        val locations: List<String>,
)

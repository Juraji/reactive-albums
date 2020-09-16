package nl.juraji.reactive.albums.configuration.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@ConstructorBinding
@ConfigurationProperties(prefix = "cors")
data class CorsConfiguration(
        val maxAge: Long,
        val origins: List<String>,
)

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class WebFluxCorsConfigurer(
        private val corsConfiguration: CorsConfiguration,
) : WebFluxConfigurer {

    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(*corsConfiguration.origins.toTypedArray())
                .allowedMethods("GET", "PUT", "POST", "OPTIONS", "DELETE")
                .maxAge(corsConfiguration.maxAge)
    }
}

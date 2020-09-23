package nl.juraji.reactive.albums.api

import com.fasterxml.jackson.databind.ObjectMapper
import nl.juraji.reactive.albums.configuration.security.CorsConfiguration
import nl.juraji.reactive.albums.configuration.security.WebFluxCorsConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
@Import(SortParameterConverter::class)
@Profile("test")
class ApiTestConfiguration {

    @Bean("webFluxCorsConfigurer")
    fun webFluxCorsConfigurer(): WebFluxCorsConfigurer =
            WebFluxCorsConfigurer(CorsConfiguration(Long.MAX_VALUE, listOf("*")))

    @Bean("objectMapper")
    fun objectMapper(
            objectMapperBuilder: Jackson2ObjectMapperBuilder,
    ): ObjectMapper = objectMapperBuilder.build()
}

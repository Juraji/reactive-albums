package nl.juraji.reactive.albums.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import nl.juraji.reactive.albums.util.serialization.BitsetDeSerializer
import nl.juraji.reactive.albums.util.serialization.BitsetSerializer
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.*
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@ComponentScan
@Configuration
class SerializationConfiguration {

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder =
            Jackson2ObjectMapperBuilder()
                    .propertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
                    .serializationInclusion(JsonInclude.Include.NON_NULL)
                    .modules(
                            JavaTimeModule(),
                            KotlinModule()
                    )
                    .serializers(
                            BitsetSerializer()
                    )
                    .deserializers(
                            BitsetDeSerializer()
                    )
                    .featuresToEnable(
                            MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS,
                            SerializationFeature.WRITE_DATES_WITH_ZONE_ID,
                            DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE
                    )
                    .featuresToDisable(
                            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                    )

    @Bean
    @Primary
    fun serializer(objectMapperBuilder: Jackson2ObjectMapperBuilder): Serializer =
            JacksonSerializer.builder()
                    .objectMapper(objectMapperBuilder.build())
                    .build()
}

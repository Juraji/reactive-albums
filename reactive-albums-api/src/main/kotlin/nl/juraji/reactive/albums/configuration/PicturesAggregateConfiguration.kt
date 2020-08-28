package nl.juraji.reactive.albums.configuration

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType

@ConstructorBinding
@ConfigurationProperties(prefix = "picture-aggregate")
data class PicturesAggregateConfiguration(
        val colorPaletteSize: Int,
        val snapshotTriggerThreshold: Int,
        val thumbnailSize: Int,
        val thumbnailLocation: String,
        val thumbnailType: String,
        val hashingSampleSize: Int,
        val autoCropTolerance: Float,
        val duplicateSimilarity: Float
) {
    val thumbnailMimeType: MediaType = MediaType.parseMediaType(thumbnailType)

    @Bean("pictureSnapshotTriggerDefinition")
    fun pictureSnapshotTriggerDefinition(snapshotter: Snapshotter): SnapshotTriggerDefinition =
            EventCountSnapshotTriggerDefinition(snapshotter, snapshotTriggerThreshold)
}

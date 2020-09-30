package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.util.RgbColor
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.beans.factory.config.YamlMapFactoryBean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class InstallService(
        private val commandGateway: CommandGateway,
        private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    @EventListener(ContextRefreshedEvent::class)
    fun init() {
        if (!isTaskCompleted("runInstallPictureColorTags")) {
            runInstallPictureColorTags()
            setTaskCompleted("runInstallPictureColorTags")
        }
    }

    private fun isTaskCompleted(taskName: String): Boolean =
            jdbcTemplate.query(
                    "select 1 from InstallServiceCompletedTasks where taskName = :taskName;",
                    mapOf("taskName" to taskName),
                    ResultSetExtractor { resultSet -> resultSet.next() }
            ) == true

    private fun setTaskCompleted(taskName: String) {
        jdbcTemplate.execute(
                "insert into InstallServiceCompletedTasks (taskName) values (:taskName)",
                mapOf("taskName" to taskName)
        ) { it.execute() }
    }

    private fun runInstallPictureColorTags() {
        val yaml = YamlMapFactoryBean()
        yaml.setResources(ClassPathResource("predefined-tag-colors.yaml"))
        val map: Map<String, String> = (yaml.`object`
                ?: throw IllegalStateException("Could not load predefined-tag-colors.yaml")) as Map<String, String>

        map.forEach { (label, color) ->
            commandGateway.send<Unit>(CreateTagCommand(
                    tagId = TagId(),
                    label = label,
                    tagColor = RgbColor.of(color),
                    tagType = TagType.COLOR
            ))
        }
    }
}
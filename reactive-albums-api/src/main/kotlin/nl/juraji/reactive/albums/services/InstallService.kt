package nl.juraji.reactive.albums.services

import nl.juraji.reactive.albums.domain.tags.TagId
import nl.juraji.reactive.albums.domain.tags.TagType
import nl.juraji.reactive.albums.domain.tags.commands.CreateTagCommand
import nl.juraji.reactive.albums.util.LoggerCompanion
import nl.juraji.reactive.albums.util.RgbColor
import org.springframework.beans.factory.config.YamlMapFactoryBean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class InstallService(
        private val commandDispatch: CommandDispatch,
        private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    @EventListener(ContextRefreshedEvent::class)
    fun init() {
        runInstallTask("runInstallPictureColorTags", this::runInstallPictureColorTags)
    }

    private fun runInstallTask(taskName: String, taskRunner: () -> Unit) {
        val isTaskCompleted = jdbcTemplate.query(
                "select 1 from InstallServiceCompletedTasks where taskName = :taskName;",
                mapOf("taskName" to taskName),
                ResultSetExtractor { resultSet -> resultSet.next() }
        ) == true

        if (!isTaskCompleted) {
            taskRunner
                    .runCatching { invoke() }
                    .onSuccess {
                        jdbcTemplate.execute(
                                "insert into InstallServiceCompletedTasks (taskName) values (:taskName)",
                                mapOf("taskName" to taskName)
                        ) { stmt -> stmt.execute() }
                    }
                    .onFailure { ex ->
                        logger.error("Install task \"$taskName\" failed", ex)
                    }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun runInstallPictureColorTags() {
        val yaml = YamlMapFactoryBean()
        yaml.setResources(ClassPathResource("predefined-tag-colors.yaml"))
        val map: Map<String, String> = yaml.`object` as Map<String, String>?
                ?: throw IllegalStateException("Could not load predefined-tag-colors.yaml")

        map.forEach { (label, color) ->
            commandDispatch.dispatchAndForget(CreateTagCommand(
                    tagId = TagId(),
                    label = label,
                    tagColor = RgbColor.of(color),
                    tagType = TagType.COLOR
            ))
        }
    }

    companion object : LoggerCompanion(InstallService::class)
}

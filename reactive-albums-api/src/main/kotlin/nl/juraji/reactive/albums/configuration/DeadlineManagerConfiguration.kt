package nl.juraji.reactive.albums.configuration

import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.ConfigurationScopeAwareProvider
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.SimpleDeadlineManager
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DeadlineManagerConfiguration {

    @Bean
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun deadlineManager(
            axonConfig: AxonConfiguration,
            transactionManager: TransactionManager
    ): DeadlineManager = SimpleDeadlineManager.builder()
            .transactionManager(transactionManager)
            .scopeAwareProvider(ConfigurationScopeAwareProvider(axonConfig))
            .build()
}

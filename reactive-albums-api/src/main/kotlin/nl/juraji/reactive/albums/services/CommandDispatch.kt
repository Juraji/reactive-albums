package nl.juraji.reactive.albums.services

import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.GenericCommandMessage
import org.axonframework.commandhandling.callbacks.NoOpCallback
import org.axonframework.messaging.MetaData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommandDispatch(
        private val commandBus: CommandBus,
) {

    fun <R> dispatch(
            command: Any,
            metaData: MetaData = MetaData.emptyInstance(),
    ): Mono<R> = Mono.create { sink ->
        commandBus.dispatch<Any, R>(
                GenericCommandMessage(command, metaData)
        ) { _, result ->
            if (result.isExceptional) sink.error(result.exceptionResult())
            else sink.success(result.payload)
        }
    }

    fun dispatchAndForget(
            command: Any,
            metaData: MetaData = MetaData.emptyInstance(),
    ) = commandBus.dispatch(
            GenericCommandMessage(command, metaData),
            NoOpCallback.INSTANCE
    )

    fun <R> dispatchBlocking(
            command: Any,
            metaData: MetaData = MetaData.emptyInstance(),
    ) = dispatch<R>(command, metaData).block()
}

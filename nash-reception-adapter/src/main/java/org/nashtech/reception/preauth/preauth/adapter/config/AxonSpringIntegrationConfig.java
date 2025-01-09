package org.nashtech.reception.preauth.preauth.adapter.config;

import org.nashtech.reception.preauth.preauth.adapter.service.AxonCommandService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.handler.ServiceActivatingHandler;

/**
 * Configuration class for integrating Spring Integration with Axon Framework.
 *
 * <p>This class sets up the necessary Spring Integration channels and handlers
 * to facilitate the dispatching of message payloads as Axon commands. It provides
 * a seamless integration between Spring Integration's messaging capabilities and
 * Axon's command handling mechanism.</p>
 *
 * <h2>Components:</h2>
 * <ul>
 *     <li><b>DirectChannel:</b> A synchronous channel used for dispatching messages.</li>
 *     <li><b>ServiceActivatingHandler:</b> A handler that delegates the processing of
 *         messages to the AxonCommandService, which is responsible for handling commands.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>The `axonCommandDispatchChannel` bean defined in this class can be used
 * to send messages that are converted into Axon commands and dispatched synchronously
 * to the appropriate handlers.</p>
 *
 */
@Configuration
public class AxonSpringIntegrationConfig {

    /**
     * Channel which synchronously dispatches message payloads as Axon commands.
     */
    @Bean
    public DirectChannel axonCommandDispatchChannel(AxonCommandService axonCommandService) {
        var channel = new DirectChannel();
        channel.subscribe(new ServiceActivatingHandler(axonCommandService));
        return channel;
    }
}

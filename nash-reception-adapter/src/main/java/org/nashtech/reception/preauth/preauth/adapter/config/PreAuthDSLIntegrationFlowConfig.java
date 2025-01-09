package org.nashtech.reception.preauth.preauth.adapter.config;

import org.nashtech.reception.preauth.preauth.adapter.parser.PreAuthHeaders;
import org.nashtech.reception.preauth.preauth.adapter.parser.PreAuthMessageParser;
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckInMessage;
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckOutMessage;
import org.nashtech.reception.preauth.preauth.adapter.service.AxonCommandService;
import org.nashtech.reception.preauth.preauth.commands.CheckInCommand;
import org.nashtech.reception.preauth.preauth.commands.CheckOutCommand;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageChannel;

/**
 * Configuration class for defining Spring Integration flows for pre-authorization messages.
 *
 * <p>This class sets up channels and flows to process, route, and dispatch pre-authorization
 * messages such as check-in and check-out commands. It leverages Spring Integration's DSL
 * to create declarative and expressive message flows.</p>
 *
 * <h2>Flows and Channels:</h2>
 * <ul>
 *     <li><b>rawReceptionPreAuthMessages:</b> A {@link DirectChannel} for raw pre-authorization messages.</li>
 *     <li><b>receptionPreAuthMessages:</b> A {@link PublishSubscribeChannel} for processed  messages.</li>
 *     <li><b>checkInPreAuthorizationMessages:</b> A {@link PublishSubscribeChannel} for check-in messages.</li>
 *     <li><b>checkOutPreAuthorizationMessages:</b> A {@link PublishSubscribeChannel} for check-out messages.</li>
 *     <li><b>Integration Flows:</b> Handles message parsing, routing, and Axon command dispatching.</li>
 * </ul>
 */
@Configuration
public class PreAuthDSLIntegrationFlowConfig {

    @Bean
    public DirectChannel rawReceptionPreAuthMessages() {return new DirectChannel();}

    @Bean(name = "receptionPreAuthMessages")
    public PublishSubscribeChannel receptionPreAuthMessages() {
        return new PublishSubscribeChannel();
    }

    @Bean(name = "checkInPreAuthorizationMessages")
    public PublishSubscribeChannel checkInPreAuthorizationMessages() {
        return new PublishSubscribeChannel();
    }

    @Bean(name = "checkOutPreAuthorizationMessages")
    public PublishSubscribeChannel checkOutPreAuthorizationMessages() {
        return new PublishSubscribeChannel();
    }

    /**
     * Defines an integration flow for processing raw pre-authorization messages.
     *
     * <p>The flow enriches headers, handles message parsing, and wire-taps messages
     * for routing to the {@code receptionPreAuthMessages} channel.</p>
     *
     * @param rawReceptionPreAuthMessages the channel for raw messages
     * @param receptionPreAuthMessages the channel for processed messages
     * @param preAuthMessageParser the parser for pre-authorization messages
     * @return the integration flow
     */
    @Bean
    public IntegrationFlow preAuthMessagesFlow(
            MessageChannel rawReceptionPreAuthMessages,
            MessageChannel receptionPreAuthMessages,
            PreAuthMessageParser preAuthMessageParser) {
        return IntegrationFlow.from(rawReceptionPreAuthMessages)
                .enrichHeaders(enricher -> enricher.headerExpression("raw_preauth_message", "payload"))
                .handle(preAuthMessageParser)
                .wireTap(flow ->
                        flow.headerFilter("raw_preauth_message").channel(receptionPreAuthMessages))
                .nullChannel();
    }

    /**
     * Defines an integration flow for routing pre-authorization messages by type.
     *
     * <p>The flow routes messages to specific channels based on their class type.
     * Unrecognized message types are logged with a warning.</p>
     *
     * @param receptionPreAuthMessages the channel for processed messages
     * @param checkInPreAuthorizationMessages the channel for check-in messages
     * @param checkOutPreAuthorizationMessages the channel for check-out messages
     * @return the integration flow
     */
    @Bean
    public IntegrationFlow preAuthMessagesRouterFlow(
            MessageChannel receptionPreAuthMessages,
            MessageChannel checkInPreAuthorizationMessages,
            MessageChannel checkOutPreAuthorizationMessages) {
        return IntegrationFlow.from(receptionPreAuthMessages)
                .<Object, Class<?>>route(Object::getClass, router -> router.defaultOutputToParentFlow()
                        .channelMapping(CheckInMessage.class, checkInPreAuthorizationMessages)
                        .channelMapping(CheckOutMessage.class, checkOutPreAuthorizationMessages))
                .log(LoggingHandler.Level.WARN,
                        m -> "Unrecognized preAuth Message type "
                                + m.getHeaders().get(PreAuthHeaders.MESSAGE_TYPE))
                .nullChannel();
    }

    /**
     * Defines an integration flow for dispatching check-in pre-authorization messages as Axon commands.
     *
     * @param checkInPreAuthorizationMessages the channel for check-in messages
     * @param axonCommandService the service for handling Axon commands
     * @return the integration flow
     */
    @Bean
    public IntegrationFlow preAuthCheckInCommandDispatchFlow(
            @Qualifier(value = "checkInPreAuthorizationMessages") MessageChannel checkInPreAuthorizationMessages,
            AxonCommandService axonCommandService) {
        return IntegrationFlow.from(checkInPreAuthorizationMessages)
                .convert(CheckInCommand.class)
                .handle(axonCommandService)
                .log(LoggingHandler.Level.DEBUG, PreAuthDSLIntegrationFlowConfig.class.getName(), "payload")
                .nullChannel();
    }

    /**
     * Defines an integration flow for dispatching check-out pre-authorization messages as Axon commands.
     *
     * @param checkOutPreAuthorizationMessages the channel for check-out messages
     * @param axonCommandService the service for handling Axon commands
     * @return the integration flow
     */
    @Bean
    public IntegrationFlow preAuthCheckOutCommandDispatchFlow(
            @Qualifier(value = "checkOutPreAuthorizationMessages") MessageChannel checkOutPreAuthorizationMessages,
            AxonCommandService axonCommandService) {
        return IntegrationFlow.from(checkOutPreAuthorizationMessages)
                .convert(CheckOutCommand.class)
                .handle(axonCommandService)
                .log(LoggingHandler.Level.DEBUG, PreAuthDSLIntegrationFlowConfig.class.getName(), "payload")
                .nullChannel();
    }

}

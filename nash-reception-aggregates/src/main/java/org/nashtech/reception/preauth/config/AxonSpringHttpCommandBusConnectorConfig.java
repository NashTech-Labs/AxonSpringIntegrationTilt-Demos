/*
 * Copyright 2024 H-E-B.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of H-E-B.
 */
package org.nashtech.reception.preauth.config;

import lombok.Generated;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.distributed.CommandBusConnector;
import org.axonframework.extensions.springcloud.commandhandling.SpringHttpCommandBusConnector;
import org.axonframework.serialization.Serializer;
import org.axonframework.tracing.SpanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for setting up the Axon Framework's HTTP Command Bus Connector.
 * <p>
 * The {@link CommandBusConnector} bean created in this configuration facilitates
 * to receive Command Messages from remote nodes, It requires
 * a local command bus, a REST template for making HTTP requests, a serializer
 * for message conversion, and a span factory for tracing and monitoring.
 */
@Configuration
@ConditionalOnProperty(name = "axon.distributed.enabled", havingValue = "true")
@Generated
public class AxonSpringHttpCommandBusConnectorConfig {

    /**
     * Creates a spring http  {@link CommandBusConnector} bean to receive commands from remote nodes .
     *
     * @param localSegment the local command bus to which commands will be dispatched
     * @param restTemplate the {@link RestTemplate} used for making HTTP requests to other nodes
     * @param serializer the {@link Serializer} used for serializing command messages
     * @param spanFactory the {@link SpanFactory} for creating tracing spans
     * @return a configured {@link CommandBusConnector} instance
     */
    @Bean
    public CommandBusConnector springHttpCommandBusConnector(
            @Qualifier("localSegment") CommandBus localSegment,
            RestTemplate restTemplate,
            @Qualifier("messageSerializer") Serializer serializer,
            SpanFactory spanFactory) {
        return SpringHttpCommandBusConnector.builder()
                .localCommandBus(localSegment)
                .restOperations(restTemplate)
                .serializer(serializer)
                .spanFactory(spanFactory)
                .build();
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

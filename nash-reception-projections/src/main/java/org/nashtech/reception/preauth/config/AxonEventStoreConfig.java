/*
 * Copyright 2024 H-E-B.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of H-E-B.
 */
package org.nashtech.reception.preauth.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Generated;
import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventhandling.tokenstore.jdbc.PostgresTokenTableFactory;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.SQLErrorCodesResolver;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.jdbc.SpringDataSourceConnectionProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Configuration class for setting up the Axon {@link EmbeddedEventStore} with {@link JdbcEventStorageEngine}
 * for PostgreSQL. This class configures the necessary beans for event storage, token management,
 * and connection handling for the Axon Framework.
 * <p>
 * Axon event store configuration is only for reading the events from event store and
 * handling the events by event processors of this application.
 * <p>
 * {@link JdbcTokenStore} save and load TrackingToken instances in Postgres. It keeps and maintain the
 * event processing progress by the event processors of this application.
 */
@Configuration(proxyBeanMethods = false)
@Generated
public class AxonEventStoreConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.write-side-event-store")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    HikariDataSource eventStoreDataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    /**
     * Creates a SpringDataSourceConnectionProvider bean using the configured event store data source.
     *
     * @param eventStoreDataSource the configured HikariDataSource for the event store.
     * @return SpringDataSourceConnectionProvider instance.
     */
    @Bean
    public SpringDataSourceConnectionProvider eventStoreConnectionProvider(HikariDataSource eventStoreDataSource) {
        return new SpringDataSourceConnectionProvider(eventStoreDataSource);
    }

    /**
     * Configures the EventStorageEngine for reading events stored in event
     * store by command applications.
     *
     * @param serializer the serializer to use for serializing events.
     * @param eventStoreConnectionProvider the connection provider for the event store.
     * @param transactionManager the transaction manager to use for event storage operations.
     * @param persistenceExceptionResolver the exception resolver for handling persistence errors.
     * @return EventStorageEngine instance configured for event storage.
     */
    @Bean
    public EventStorageEngine eventStorageEngine(
            Serializer serializer,
            SpringDataSourceConnectionProvider eventStoreConnectionProvider,
            TransactionManager transactionManager,
            PersistenceExceptionResolver persistenceExceptionResolver) {
        return JdbcEventStorageEngine.builder()
                .snapshotSerializer(JacksonSerializer.defaultSerializer())
                .connectionProvider(eventStoreConnectionProvider)
                .eventSerializer(JacksonSerializer.defaultSerializer())
                .transactionManager(transactionManager)
                .persistenceExceptionResolver(persistenceExceptionResolver)
                .build();
    }

    /**
     * Configures a JdbcTokenStore for managing {@link TrackingToken}, using a
     * PostgreSQL-specific token table factory.
     *
     * @param processingConfigurer the processing configurer to register the token store.
     * @param eventStoreConnectionProvider the connection provider for the event store.
     * @return JdbcTokenStore instance configured for token management.
     */
    @Bean
    public JdbcTokenStore registerTokenStore(
            final EventProcessingConfigurer processingConfigurer,
            final SpringDataSourceConnectionProvider eventStoreConnectionProvider) {

        JdbcTokenStore tokenStore = JdbcTokenStore.builder()
                .connectionProvider(eventStoreConnectionProvider)
                .serializer(JacksonSerializer.defaultSerializer())
                .build();

        tokenStore.createSchema(PostgresTokenTableFactory.INSTANCE);
        processingConfigurer.registerTokenStore(config -> tokenStore);
        return tokenStore;
    }

    /**
     * Creates a PersistenceExceptionResolver bean if none exists and if a DataSource bean is present.
     *
     * @param eventStoreDataSource the data source to resolve SQL errors.
     * @return PersistenceExceptionResolver instance for handling persistence exceptions.
     * @throws SQLException if a database access error occurs.
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({DataSource.class})
    public PersistenceExceptionResolver persistenceExceptionResolver(HikariDataSource eventStoreDataSource)
            throws SQLException {
        return new SQLErrorCodesResolver(eventStoreDataSource);
    }
}

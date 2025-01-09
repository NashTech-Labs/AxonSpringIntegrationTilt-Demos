package org.nashtech.reception.preauth.config;

import lombok.Generated;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.GenericDomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.AbstractEventTableFactory;
import org.axonframework.eventsourcing.eventstore.jdbc.EventSchema;
import org.axonframework.eventsourcing.eventstore.jdbc.EventTableFactory;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.statements.TimestampWriter;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.jdbc.SpringDataSourceConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * {@link EmbeddedEventStore} configuration with {@link JdbcEventStorageEngine}.
 */
@Configuration
@Generated
@Component
public class AxonEventStoreConfig {

    /**
     * The JdbcEventStorageEngine stores events in a JDBC-compatible data source.
     * Here overriding the prepared statement to append events and snapshot.
     * Cast byte array to jsonb type for payload and metadata.
     *
     * @param transactionManager Manage Transaction {@link TransactionManager}.
     * @param springDataSourceConnectionProvider Spring Datasource connection
     *                                           provider {@link SpringDataSourceConnectionProvider}.
     * @return {@link EventStorageEngine}.
     */
    @Bean
    public EventStorageEngine storageEngine(
            final TransactionManager transactionManager,
            final SpringDataSourceConnectionProvider springDataSourceConnectionProvider) {

        JdbcEventStorageEngine storageEngine = JdbcEventStorageEngine.builder()
                .connectionProvider(springDataSourceConnectionProvider)
                .eventSerializer(JacksonSerializer.defaultSerializer())
                .snapshotSerializer(JacksonSerializer.defaultSerializer())
                .transactionManager(transactionManager)
                .appendEvents(AxonEventStoreConfig::getDomainPreparedStatement)
                .appendSnapshot(AxonEventStoreConfig::getSnapshotPreparedStatement)
                .build();


        return storageEngine;
    }

    @SuppressWarnings("squid:S2095")
    private static PreparedStatement getSnapshotPreparedStatement(
            final Connection connection,
            final EventSchema schema,
            Class<?> dataType,
            final DomainEventMessage<?> snapshot,
            final Serializer serializer,
            final TimestampWriter timestampWriter)
            throws SQLException {

        final String sql = getSql(schema, schema.snapshotTable());
        PreparedStatement statement = connection.prepareStatement(sql);
        SerializedObject<?> payload = snapshot.serializePayload(serializer, dataType);
        SerializedObject<?> metaData = snapshot.serializeMetaData(serializer, dataType);
        statement.setString(1, snapshot.getIdentifier());
        statement.setString(2, snapshot.getAggregateIdentifier());
        statement.setLong(3, snapshot.getSequenceNumber());
        statement.setString(4, snapshot.getType());
        timestampWriter.writeTimestamp(statement, 5, snapshot.getTimestamp());
        statement.setString(6, payload.getType().getName());
        statement.setString(7, payload.getType().getRevision());
        statement.setObject(8, new String((byte[]) payload.getData(), StandardCharsets.UTF_8));
        statement.setObject(9, new String((byte[]) metaData.getData(), StandardCharsets.UTF_8));
        return statement;
    }

    @SuppressWarnings("squid:S2095")
    private static PreparedStatement getDomainPreparedStatement(
            final Connection connection,
            final EventSchema schema,
            final Class<?> dataType,
            final List<? extends EventMessage<?>> events,
            final Serializer serializer,
            final TimestampWriter timestampWriter)
            throws SQLException {

        final String sql = getSql(schema, schema.domainEventTable());
        PreparedStatement statement = connection.prepareStatement(sql);
        for (EventMessage<?> eventMessage : events) {
            DomainEventMessage<?> event = asDomainEventMessage(eventMessage);
            SerializedObject<?> payload = event.serializePayload(serializer, dataType);
            SerializedObject<?> metaData = event.serializeMetaData(serializer, dataType);
            statement.setString(1, event.getIdentifier());
            statement.setString(2, event.getAggregateIdentifier());
            statement.setLong(3, event.getSequenceNumber());
            statement.setString(4, event.getType());
            timestampWriter.writeTimestamp(statement, 5, event.getTimestamp());
            statement.setString(6, payload.getType().getName());
            statement.setString(7, payload.getType().getRevision());
            statement.setObject(8, new String((byte[]) payload.getData(), StandardCharsets.UTF_8));
            statement.setObject(9, new String((byte[]) metaData.getData(), StandardCharsets.UTF_8));
            statement.addBatch();
        }
        return statement;
    }

    private static String getSql(EventSchema schema, String table) {

        return "INSERT INTO " + table
                + " ("
                + schema.domainEventFields()
                + ") "
                + "VALUES (?,?,?,?,?,?,?,?::JSONB,?::JSONB)";
    }

    /**
     * Creates a {@link SpringDataSourceConnectionProvider} for the event store data source.
     *
     * <p>This method initializes the connection provider using the specified data source,
     * which allows for connections to be managed and utilized for event store database operations.</p>
     *
     * @param dataSource The {@link DataSource} instance used to create the connection provider.
     * @return A new instance of {@link SpringDataSourceConnectionProvider}
     * configured with the provided data source.
     */
    @Bean
    public SpringDataSourceConnectionProvider getEventStoreDBDataSource(final DataSource dataSource) {
        return new SpringDataSourceConnectionProvider(dataSource);
    }

    /**
     * Converts an {@link EventMessage} to a {@link DomainEventMessage}.
     *
     * <p>This method checks if the provided {@link EventMessage} is already an instance of
     * {@link DomainEventMessage}. If so, it casts and returns it. If not, it creates a new
     * {@link GenericDomainEventMessage} using the properties of the provided event.</p>
     *
     * @param event The {@link EventMessage} to be converted to a {@link DomainEventMessage}.
     * @param <T> The type of the event payload.
     * @return The corresponding {@link DomainEventMessage} if the input event is already a domain event,
     *         otherwise a new {@link GenericDomainEventMessage} based on the input event.
     */
    protected static <T> DomainEventMessage<T> asDomainEventMessage(final EventMessage<T> event) {
        return event instanceof DomainEventMessage<?>
                ? (DomainEventMessage<T>) event
                : new GenericDomainEventMessage<>(null, event.getIdentifier(), 0L, event, event::getTimestamp);
    }

    /**
     * Creates a new instance of {@link EventTableFactory} for configuring event tables.
     * <p>
     * This method returns an implementation of {@link AbstractEventTableFactory} that specifies
     * the types of columns used in the event storage table. The ID column is defined as
     * "BIGSERIAL" for auto-incrementing values, and the payload column is defined as "JSONB"
     * for storing event payloads in a binary JSON format.
     * </p>
     *
     * @return a configured instance of {@link EventTableFactory} with specific column types.
     */
    private EventTableFactory tableFactory() {

        return new AbstractEventTableFactory() {
            @Override
            protected String idColumnType() {
                return "BIGSERIAL";
            }

            @Override
            protected String payloadType() {
                return "JSONB";
            }
        };
    }
}

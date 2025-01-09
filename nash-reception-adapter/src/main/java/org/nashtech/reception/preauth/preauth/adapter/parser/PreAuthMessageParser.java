package org.nashtech.reception.preauth.preauth.adapter.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckInMessage;
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckOutMessage;
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.PreAuthMetadata;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * A parser for handling pre-authorization messages in JSON format.
 *
 * <p>This class uses Jackson's {@link ObjectMapper} to parse JSON messages and extract their metadata and payload.
 * Depending on the message type, it maps the payload to a specific message class (e.g., {@link CheckInMessage} or {@link CheckOutMessage}).
 * Unrecognized message types are returned as generic JSON nodes.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Parses JSON messages and extracts metadata into headers.</li>
 *     <li>Supports multiple message types with a switch-based mapping.</li>
 *     <li>Handles unrecognized message types gracefully by returning a {@link GenericMessage}.</li>
 * </ul>
 *
 * <p>Used in a Spring Integration flow with the {@link ServiceActivator} annotation for message processing.</p>
 *
 * @see ObjectMapper
 * @see PreAuthMetadata
 * @see CheckInMessage
 * @see CheckOutMessage
 *
 */
@Slf4j
@Service
public class PreAuthMessageParser {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @ServiceActivator
    public Message<?> parse(String preAuthJsonMessage) {
        log.debug("*** PARSING MESSAGE ***");
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readValue(preAuthJsonMessage, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse pre authorization JSON message", e);
        }

        PreAuthMetadata preAuthMetadata;
        try {
            var metadataNode = jsonNode.get("metadata");
            preAuthMetadata = Objects.requireNonNull(objectMapper.treeToValue(
                    metadataNode, PreAuthMetadata.class));
        } catch (NullPointerException | JsonProcessingException e) {
            throw new RuntimeException("Unable to extract metadata from pre auth JSON message", e);
        }

        String messageType = preAuthMetadata.getMessageType();

        Class<?> messageTypeClass =
                switch (messageType) {
                    case "CheckInPreAuthorization" -> CheckInMessage.class;
                    case "CheckOutPreAuthorization" -> CheckOutMessage.class;
                    default -> null;
                };

        HashMap<String, Object> headers = new HashMap<>();
        headers.put(PreAuthHeaders.UUID, UUID.fromString(preAuthMetadata.getUuid()));
        headers.put(PreAuthHeaders.MESSAGE_TYPE, messageType);
        headers.put(PreAuthHeaders.MESSAGE_TYPE_VERSION, preAuthMetadata.getMessageTypeVersion());
        headers.put(PreAuthHeaders.SOURCE, preAuthMetadata.getSource());
        headers.put(PreAuthHeaders.EMITTED_AT_DATE_TIME, ZonedDateTime.parse(preAuthMetadata.getEmittedAtDateTime()));

        if (messageTypeClass == null) {

            return new GenericMessage<>(jsonNode, headers);
        }

        try {
            return new GenericMessage<>(objectMapper.treeToValue(jsonNode, messageTypeClass), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to parse recognized preAuth message type %s".formatted(messageType), e);
        }
    }
}

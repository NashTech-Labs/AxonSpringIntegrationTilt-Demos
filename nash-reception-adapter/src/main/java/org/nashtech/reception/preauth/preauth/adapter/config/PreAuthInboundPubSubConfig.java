package org.nashtech.reception.preauth.preauth.adapter.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

/**
 * Configuration class for setting up inbound Pub/Sub message adapters in the pre-authorization flow.
 *
 * <p>This class defines a {@link PubSubInboundChannelAdapter} bean that listens to a Pub/Sub subscription
 * and routes messages to the appropriate channels for processing and error handling.</p>
 *
 * <h2>Components:</h2>
 * <ul>
 *     <li><b>PubSubInboundChannelAdapter:</b> Adapts Pub/Sub messages into Spring Integration messages.</li>
 *     <li><b>rawReceptionPreAuthMessages:</b> The channel for raw incoming messages.</li>
 *     <li><b>errorChannel:</b> The channel for handling errors in message processing.</li>
 * </ul>
 *
 * <p>Properties for the Pub/Sub subscription are sourced from the application's configuration.</p>
 */
@Configuration
public class PreAuthInboundPubSubConfig {

    @Bean
    public PubSubInboundChannelAdapter receptionPreAuthPubSubAdapter(
            PubSubTemplate pubSubTemplate,
            MessageChannel rawReceptionPreAuthMessages,
            MessageChannel errorChannel,
            @Value("${reception-inbound-pre-auth-pubsub-topic-subscription}") String inboundPreAuthSubscription) {
        var adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, inboundPreAuthSubscription);
        adapter.setOutputChannel(rawReceptionPreAuthMessages);
        adapter.setErrorChannel(errorChannel);
        adapter.setPayloadType(String.class);
        adapter.setAckMode(AckMode.AUTO);
        return adapter;
    }
}

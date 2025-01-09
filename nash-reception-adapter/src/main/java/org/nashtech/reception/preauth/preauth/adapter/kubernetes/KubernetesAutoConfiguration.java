package org.nashtech.reception.preauth.preauth.adapter.kubernetes;

import org.axonframework.lifecycle.Lifecycle;
import org.axonframework.lifecycle.Phase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.kubernetes.client.discovery.KubernetesInformerDiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.UnknownHostException;
import java.util.Optional;

@Configuration
@EnableScheduling
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
@ConditionalOnClass(KubernetesInformerDiscoveryClient.class)
public class KubernetesAutoConfiguration {
    /**
     * Creates a basic, mocked {@link Registration} bean. The Kubernetes discovery client (without a server) does not
     * register with anything, and Axon requires a registration bean to function. Since the app is alive within
     * Kubernetes, it is safe to assume the configuration as provided by this bean.
     *
     * @param applicationName Name of the application.
     * @param port            Port the web server is listening on.
     * @return A mocked registration bean.
     * @throws UnknownHostException When a URI exception occurs.
     */
    @Bean
    @ConditionalOnMissingBean
    public KubernetesClientRegistration k8sClientRegistration(
            @Value("${spring.application.name}") String applicationName, @Value("${server.port:8080}") String port)
            throws UnknownHostException {
        return KubernetesClientRegistration.of(applicationName, port);
    }

    /**
     * When the Kubernetes discovery client is in use, there is no central server to register to and so no
     * registration event is fired. This Axon lifecycle bean ties into the startup process and manually fires the
     * required event so that Axon discovery can function correctly.
     *
     * @param eventPublisher Application event publisher.
     * @return An Axon lifecycle configuration object.
     */
    @Bean
    @ConditionalOnBean(KubernetesClientRegistration.class)
    public Lifecycle foo(ApplicationEventPublisher eventPublisher) {
        return lifecycle -> lifecycle.onStart(
                Phase.EXTERNAL_CONNECTIONS,
                () -> eventPublisher.publishEvent(new InstanceRegisteredEvent<>(this, Optional.empty())));
    }
}

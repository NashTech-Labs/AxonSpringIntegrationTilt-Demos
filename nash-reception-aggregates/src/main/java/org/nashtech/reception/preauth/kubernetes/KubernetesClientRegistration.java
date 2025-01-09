package org.nashtech.reception.preauth.kubernetes;

import org.springframework.cloud.client.serviceregistry.Registration;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * A basic, mocked registration object suitable for use with the serverless Kubernetes discover client.
 *
 * @param applicationName Application  name.
 * @param host            Application runtime hostname.
 * @param port            Web server port.
 * @param secure          Whether the URI is using a secure protocol (https).
 * @param uri             Complete URI of the registration.
 * @param metadata        Any metadata associated with the registration.
 */
public record KubernetesClientRegistration(
        String applicationName, String host, int port, boolean secure, URI uri, Map<String, String> metadata)
        implements Registration {
    /**
     * Builds a Kubernetes registration based on the application name and web server port. The host component of the
     * URI is queried from the host OS' hostname.
     *
     * @param name Application name.
     * @param port Web server port.
     * @return A Kubernetes registration object.
     * @throws UnknownHostException When an error querying the hostname occurs.
     */
    public static KubernetesClientRegistration of(String name, String port) throws UnknownHostException {
        int p = Integer.parseInt(port);
        var address = InetAddress.getLocalHost();

        var uri = URI.create("http://" + address.getHostAddress() + ":" + port);
        return new KubernetesClientRegistration(name, address.getHostAddress(), p, false, uri, Map.of());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceId() {
        return applicationName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHost() {
        return host();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return port();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSecure() {
        return secure();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUri() {
        return uri();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getMetadata() {
        return metadata();
    }
}

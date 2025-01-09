package org.nashtech.reception.preauth.preauth.adapter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

/**
 * A service for dispatching commands to the Axon Command Gateway.
 *
 * <p>This class is responsible for handling command objects and sending them synchronously
 * to the Axon framework using the {@link CommandGateway}. It integrates seamlessly with
 * Spring Integration flows through the {@link ServiceActivator} annotation.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Logs dispatched commands for traceability.</li>
 *     <li>Uses Axon's {@link CommandGateway} for reliable command delivery.</li>
 *     <li>Supports synchronous command dispatch with immediate response handling.</li>
 * </ul>
 *
 * <p>Designed for enterprise integration scenarios where commands need to be processed
 * reliably within a distributed system.</p>
 *
 * @see CommandGateway
 * @see ServiceActivator
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AxonCommandService {
    private final CommandGateway commandGateway;

    @ServiceActivator
    public Object dispatch(Object commandPayload) {

        log.info("dispatching command {}", commandPayload);
        return commandGateway.sendAndWait(commandPayload);
    }
}

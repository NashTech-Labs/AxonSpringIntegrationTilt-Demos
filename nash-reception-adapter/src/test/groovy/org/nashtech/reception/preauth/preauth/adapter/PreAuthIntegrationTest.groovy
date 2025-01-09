package org.nashtech.reception.preauth.preauth.adapter

import org.nashtech.reception.preauth.PreAuthAdapterApplication
import org.nashtech.reception.preauth.preauth.adapter.service.AxonCommandService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import spock.lang.Specification
import spock.lang.Tag

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PreAuthAdapterApplication)
@ActiveProfiles(["test"])
@Tag("integration")
class PreAuthIntegrationTest extends Specification {

    @Autowired
    MessageChannel rawReceptionPreAuthMessages

    @SpringBean
    AxonCommandService axonCommandService = Mock(AxonCommandService)

    @ServiceConnection
    public static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:15-alpine")

    static {
        postgresContainer.start()
    }

    def "should convert and send CheckInCommand to the axon command gateway"() {
        given:
        var subscriber = Mock(MessageHandler)
        def checkInJson = PreAuthIntegrationTest.getResource("/check-in.json").text

        when:
        rawReceptionPreAuthMessages.send(new GenericMessage(checkInJson))

        then:
        1 * axonCommandService.dispatch(_)
    }
}

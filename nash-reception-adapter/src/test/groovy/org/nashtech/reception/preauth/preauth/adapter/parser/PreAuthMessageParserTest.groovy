package org.nashtech.reception.preauth.preauth.adapter.parser

import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckInMessage
import spock.lang.Specification
import java.time.ZonedDateTime;

class PreAuthMessageParserTest extends Specification {

    def "should parse pre-auth message correctly and return a message with headers"() {
        given: "A PreAuthMessageParser instance"
        def preAuthMessageParser = new PreAuthMessageParser()

        and: "A sample JSON string for CheckInPreAuthorization read from file"
        def jsonFile = new File(getClass().getResource("/check-in.json").toURI())
        def json = jsonFile.text

        when: "Parsing the message using PreAuthMessageParser"
        def message = preAuthMessageParser.parse(json)

        then: "The headers should be correctly set"
        message.headers[PreAuthHeaders.MESSAGE_TYPE] == "CheckInPreAuthorization"
        message.headers[PreAuthHeaders.MESSAGE_TYPE_VERSION] == "1.0"
        message.headers[PreAuthHeaders.SOURCE] == "nash-reception-pro"
        message.headers[PreAuthHeaders.EMITTED_AT_DATE_TIME] == ZonedDateTime.parse("2025-01-10T12:00:00Z")

        and: "The payload should be correctly parsed as CheckInMessage"
        message.payload instanceof CheckInMessage
        def checkInMessage = (CheckInMessage) message.payload
        checkInMessage.data.reservationId == "12345"
        checkInMessage.data.checkInDate == "2025-01-10"
        checkInMessage.data.roomNumber == "101"
        checkInMessage.data.roomType == "Standard"
        checkInMessage.data.amountHold.compareTo(150.00) == 0
        checkInMessage.data.currency == "USD"
        checkInMessage.data.hotelId == "IND:DEL:003"
    }

    def "Throws exception for unrecognized preauth message type"() {
        given:
        def messageJson = """{
            "metadata": {
                "messageType": "not-my-type"
            }
        }
        """
        def parser = new PreAuthMessageParser()

        when:
        parser.parse(messageJson)

        then:
        thrown RuntimeException
    }

}

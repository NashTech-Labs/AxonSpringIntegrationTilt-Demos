package org.nashtech.reception.preauth.preauth.adapter.mapper

import org.nashtech.reception.preauth.preauth.adapter.service.CreditCheckResponse
import org.nashtech.reception.preauth.preauth.adapter.service.MockCreditCheckService
import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckInMessage
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import org.mapstruct.factory.Mappers;
import spock.lang.Specification

class CheckInMapperTest extends Specification {

    def "should map CheckInMessage to CheckInCommand correctly"() {
        given: "A CheckInMessage JSON read from a file"
        def jsonFile = CheckInMapperTest.getResource("/check-in.json")
        def json = jsonFile.text
        def objectMapper = new ObjectMapper()
        def checkInMessage = objectMapper.readValue(json, CheckInMessage)

        and: "A MockCreditCheckService for the canAmountBeHold logic"
        def mockCreditCheckService = Mock(MockCreditCheckService)
        mockCreditCheckService.checkCredit(_ as CheckInMessage) >> new CreditCheckResponse(100.00, "SUCCESS", "Mocked response")

        and: "The CheckInMapper instance"
        def checkInMapper = Mappers.getMapper(CheckInMapper)

        when: "Mapping the CheckInMessage to CheckInCommand"
        def checkInCommand = checkInMapper.convert(checkInMessage)

        then: "The mapping is correct"
        checkInCommand.reservationId == "12345"
        checkInCommand.checkInDate == "2025-01-10"
        checkInCommand.roomNumber == "101"
        checkInCommand.roomType == "Standard"
        checkInCommand.hotelId == "IND:DEL:003"
        checkInCommand.amountHold.compareTo(150.00) == 0
        checkInCommand.currency == "USD"

        and: "The canAmountBeHold logic is invoked and returns the correct value"
        checkInCommand.canAmountBeHold
    }
}

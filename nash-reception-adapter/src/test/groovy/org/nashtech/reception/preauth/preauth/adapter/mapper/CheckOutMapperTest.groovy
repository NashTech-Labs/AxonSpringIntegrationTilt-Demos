package org.nashtech.reception.preauth.preauth.adapter.mapper

import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckOutMessage
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import org.mapstruct.factory.Mappers;
import spock.lang.Specification

class CheckOutMapperTest extends Specification {

    def "should map CheckOutMessage to CheckOutCommand correctly"() {
        given: "A CheckOutMessage JSON read from a file"
        def jsonFile = getClass().getResource("/check-out.json")
        def json = jsonFile.text
        def objectMapper = new ObjectMapper()
        def checkOutMessage = objectMapper.readValue(json, CheckOutMessage)

        and: "The CheckOutMapper instance"
        def checkOutMapper = Mappers.getMapper(CheckOutMapper)

        when: "Mapping the CheckOutMessage to CheckOutCommand"
        def checkOutCommand = checkOutMapper.convert(checkOutMessage)

        then: "The mapping is correct"
        checkOutCommand.reservationId == "12345"
        checkOutCommand.checkOutDate == "2025-01-15"
        checkOutCommand.roomNumber == "101"
        checkOutCommand.hotelId == "IND:DEL:003"
        checkOutCommand.finalBillAmount.compareTo(145.00) == 0
        checkOutCommand.additionalCharges.compareTo(5.00) == 0
    }
}

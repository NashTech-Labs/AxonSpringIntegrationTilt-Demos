package org.nashtech.reception.preauth.aggregate

import org.axonframework.eventhandling.GenericDomainEventMessage
import org.nashtech.reception.preauth.preauth.commands.CheckInCommand
import org.nashtech.reception.preauth.preauth.commands.CheckOutCommand
import org.nashtech.reception.preauth.preauth.events.AuthorizationApprove
import org.nashtech.reception.preauth.preauth.events.AuthorizationDecline
import org.nashtech.reception.preauth.preauth.events.CheckInCharge
import org.nashtech.reception.preauth.preauth.events.PreAuthorizationApprove
import org.nashtech.reception.preauth.preauth.events.PreAuthorizationDeclined
import spock.lang.Specification
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.matchers.Matchers

class PreAuthorizationTest extends Specification {

    AggregateTestFixture<PreAuthorization> fixture = new AggregateTestFixture<>(PreAuthorization)

    def "should apply PreAuthorizationApprove event when CheckInCommand can amount be held"() {
        given: "A valid CheckInCommand where the amount can be held"
        def checkInCommand = new CheckInCommand(
                "12345", "2025-01-10", "101",
                "Standard", "IND:DEL:003", 150.00,
                 true, "USD")

        expect: "CheckInCommand is handled"
        fixture.given()
                .when(checkInCommand)
                .expectEventsMatching(Matchers.matches(a -> {

                         GenericDomainEventMessage object =
                                (GenericDomainEventMessage) a.findResult()
                        def preAuthorizationApprove = (PreAuthorizationApprove) object.getPayload()
                        def checkInCharge = preAuthorizationApprove.checkInCharge()
                        def assertAmount = checkInCharge.amountHold.compareTo(150.00) == 0
                        def assertReservationId = checkInCharge.reservationId == "12345"
                        def assertPaymentState = checkInCharge.paymentState == "PRE_AUTH_APPROVED"

                       return assertAmount && assertPaymentState && assertReservationId
                    }))
    }

    def "should apply PreAuthorizationDeclined event when CheckInCommand cannot hold amount"() {
        given: "A valid CheckInCommand where the amount cannot be held"
        def checkInCommand = new CheckInCommand(
                "12345", "2025-01-10",
                "101", "Standard", "IND:DEL:003",
                150.00, false, "USD")

        expect: "CheckInCommand is handled"
        fixture.given()
                .when(checkInCommand)
                .expectEventsMatching(Matchers.matches(a -> {

                    GenericDomainEventMessage object =
                            (GenericDomainEventMessage) a.findResult()
                    def preAuthorizationDeclined = (PreAuthorizationDeclined) object.getPayload()
                    def checkInCharge = preAuthorizationDeclined.checkInCharge()
                    def assertAmount = checkInCharge.amountHold.compareTo(150.00) == 0
                    def assertReservationId = checkInCharge.reservationId == "12345"
                    def assertPaymentState = checkInCharge.paymentState == "PRE_AUTH_ERROR"

                    return assertAmount && assertPaymentState && assertReservationId
                }))
    }

    def "should apply AuthorizationApprove event when CheckOutCommand is handled with pre-authorization approved"() {
        given: "Pre-authorization was approved"
        def preAuthorizationApprove = new PreAuthorizationApprove(
                "12345",
                new CheckInCharge(
                        reservationId: "12345",
                        checkInDate: "2025-01-10",
                        hotelId: "IND:DEL:003",
                        roomNumber: "101",
                        roomType: "Standard",
                        amountHold: 150.00,
                        paymentState: PaymentState.PRE_AUTH_APPROVED.name()
                )
        )

        and: "A CheckOutCommand"
        def checkOutCommand = new CheckOutCommand(
                "12345", "2025-01-15", "101",
                "IND:DEL:003", 145.00, 5.00
        )

        expect: "CheckOutCommand is handled after PreAuthorizationApprove"
        fixture.given(preAuthorizationApprove)
                .when(checkOutCommand)
                .expectEventsMatching(Matchers.matches(a -> {

                    GenericDomainEventMessage object =
                            (GenericDomainEventMessage) a.findResult()
                    def authorizationApprove = (AuthorizationApprove) object.getPayload()
                    def checkOutCharge = authorizationApprove.checkOutCharge()
                    def assertAmount = checkOutCharge.finalBillAmount.compareTo(145.00) == 0
                    def assertReservationId = checkOutCharge.reservationId == "12345"
                    def assertPaymentState = checkOutCharge.paymentState == PaymentState.AUTH_APPROVED.name()

                    return assertAmount && assertPaymentState && assertReservationId
                }))
    }

    def "should apply AuthorizationDecline event when CheckOutCommand is handled with pre-authorization declined"() {
        given: "Pre-authorization was declined"
        def preAuthorizationDeclined = new PreAuthorizationDeclined(
                "12345",
                new CheckInCharge(
                        reservationId: "12345",
                        checkInDate: "2025-01-10",
                        hotelId: "IND:DEL:003",
                        roomNumber: "101",
                        roomType: "Standard",
                        amountHold: 150.00,
                        paymentState: PaymentState.PRE_AUTH_ERROR.name()
                )
        )

        and: "A CheckOutCommand"
        def checkOutCommand = new CheckOutCommand(
                "12345", "2025-01-15", "101",
                "IND:DEL:003", 145.00, 5.00
        )

        expect: "CheckOutCommand is handled after PreAuthorizationApprove"
        fixture.given(preAuthorizationDeclined)
                .when(checkOutCommand)
                .expectEventsMatching(Matchers.matches(a -> {

                    GenericDomainEventMessage object =
                            (GenericDomainEventMessage) a.findResult()
                    def authorizationDecline = (AuthorizationDecline) object.getPayload()
                    def checkOutCharge = authorizationDecline.checkOutCharge()
                    def assertAmount = checkOutCharge.finalBillAmount.compareTo(145.00) == 0
                    def assertReservationId = checkOutCharge.reservationId == "12345"
                    def assertPaymentState = checkOutCharge.paymentState == PaymentState.AUTH_ERROR.name()

                    return assertAmount && assertPaymentState && assertReservationId
                }))
    }

}

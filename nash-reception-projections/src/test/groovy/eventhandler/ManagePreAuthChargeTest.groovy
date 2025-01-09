package eventhandler

import org.nashtech.reception.preauth.entity.PreAuthCharge
import org.nashtech.reception.preauth.eventhandler.ManagePreAuthCharge
import org.nashtech.reception.preauth.preauth.events.AuthorizationApprove
import org.nashtech.reception.preauth.preauth.events.AuthorizationDecline
import org.nashtech.reception.preauth.preauth.events.CheckInCharge
import org.nashtech.reception.preauth.preauth.events.CheckOutCharge
import org.nashtech.reception.preauth.preauth.events.PreAuthorizationApprove
import org.nashtech.reception.preauth.preauth.events.PreAuthorizationDeclined
import org.nashtech.reception.preauth.repository.PreAuthRepository

import spock.lang.Specification

class ManagePreAuthChargeTest extends Specification {

    def preAuthRepository = Mock(PreAuthRepository)
    def managePreAuthCharge = new ManagePreAuthCharge(preAuthRepository)

    def "should handle PreAuthorizationApprove event and save PreAuthCharge"() {
        given: "A PreAuthorizationApprove event"
        def preAuthorizationApprove = new PreAuthorizationApprove(
                "12345",
                new CheckInCharge(
                        reservationId: "12345",
                        checkInDate: "2025-01-10",
                        hotelId: "IND:DEL:003",
                        roomNumber: "101",
                        roomType: "Standard",
                        amountHold: 150.00,
                        paymentState: "PRE_AUTH_APPROVED"
                ))

        when: "The event is handled"
        managePreAuthCharge.on(preAuthorizationApprove)

        then: "A PreAuthCharge is saved in the repository with the expected values"
        1 * preAuthRepository.save(_) >> { PreAuthCharge preAuthCharge ->
            assert preAuthCharge.reservationId == "12345"
            assert preAuthCharge.checkInDate == "2025-01-10"
            assert preAuthCharge.roomNumber == "101"
            assert preAuthCharge.roomType == "Standard"
            assert preAuthCharge.hotelId == "IND:DEL:003"
            assert preAuthCharge.amountHold == BigDecimal.valueOf(150.00)
            assert preAuthCharge.paymentState == "PRE_AUTH_APPROVED"
            assert preAuthCharge.checkOutDate == null
            assert preAuthCharge.finalBillAmount == BigDecimal.ZERO
            assert preAuthCharge.additionalCharges == BigDecimal.ZERO
            assert preAuthCharge.createdAt != null
        }
    }

    def "should handle PreAuthorizationDeclined event and save PreAuthCharge"() {
        given: "A PreAuthorizationDeclined event"
        def checkInCharge = new CheckInCharge(
                reservationId: "12345",
                checkInDate: "2025-01-10",
                roomNumber: "101",
                roomType: "Standard",
                hotelId: "IND:DEL:003",
                amountHold: 150.00,
                paymentState: "PRE_AUTH_ERROR"
        )
        def preAuthorizationDeclined = new PreAuthorizationDeclined(
                "12345",
                 checkInCharge)

        when: "The event is handled"
        managePreAuthCharge.on(preAuthorizationDeclined)

        then: "A PreAuthCharge is saved in the repository with the expected values"
        1 * preAuthRepository.save(_) >> { PreAuthCharge preAuthCharge ->
            assert preAuthCharge.reservationId == "12345"
            assert preAuthCharge.checkInDate == "2025-01-10"
            assert preAuthCharge.roomNumber == "101"
            assert preAuthCharge.roomType == "Standard"
            assert preAuthCharge.hotelId == "IND:DEL:003"
            assert preAuthCharge.amountHold == BigDecimal.valueOf(150.00)
            assert preAuthCharge.paymentState == "PRE_AUTH_ERROR"
            assert preAuthCharge.checkOutDate == null
            assert preAuthCharge.finalBillAmount == BigDecimal.ZERO
            assert preAuthCharge.additionalCharges == BigDecimal.ZERO
            assert preAuthCharge.createdAt != null
        }
    }

    def "should handle AuthorizationApprove event and save PreAuthCharge"() {
        given: "An AuthorizationApprove event"
        def checkOutCharge = new CheckOutCharge(
                reservationId: "12345",
                checkOutDate: "2025-01-15",
                roomNumber: "101",
                hotelId: "IND:DEL:003",
                finalBillAmount: 200.00,
                additionalCharges: 50.00,
                paymentState: "AUTH_APPROVED"
        )
        def authorizationApprove = new AuthorizationApprove(
                "12345",
                checkOutCharge
        )

        when: "The event is handled"
        managePreAuthCharge.on(authorizationApprove)

        then: "A PreAuthCharge is saved in the repository with the expected values"
        1 * preAuthRepository.save(_) >> { PreAuthCharge preAuthCharge ->
            assert preAuthCharge.reservationId == "12345"
            assert preAuthCharge.checkOutDate == "2025-01-15"
            assert preAuthCharge.paymentState == "AUTH_APPROVED"
            assert preAuthCharge.finalBillAmount == BigDecimal.valueOf(200.00)
            assert preAuthCharge.additionalCharges == BigDecimal.valueOf(50.00)
            assert preAuthCharge.createdAt != null
        }
    }

    def "should handle AuthorizationDecline event and save PreAuthCharge"() {
        given: "An AuthorizationDecline event"
        def checkOutCharge = new CheckOutCharge(
                reservationId: "12345",
                checkOutDate: "2025-01-15",
                roomNumber: "101",
                hotelId: "IND:DEL:003",
                finalBillAmount: 200.00,
                additionalCharges: 50.00,
                paymentState: "AUTH_ERROR"
        )
        def authorizationDecline = new AuthorizationDecline(
                "12345",
                checkOutCharge
        )

        when: "The event is handled"
        managePreAuthCharge.on(authorizationDecline)

        then: "A PreAuthCharge is saved in the repository with the expected values"
        1 * preAuthRepository.save(_) >> { PreAuthCharge preAuthCharge ->
            assert preAuthCharge.reservationId == "12345"
            assert preAuthCharge.checkOutDate == "2025-01-15"
            assert preAuthCharge.paymentState == "AUTH_ERROR"
            assert preAuthCharge.finalBillAmount == BigDecimal.valueOf(200.00)
            assert preAuthCharge.additionalCharges == BigDecimal.valueOf(50.00)
            assert preAuthCharge.createdAt != null
        }
    }
}


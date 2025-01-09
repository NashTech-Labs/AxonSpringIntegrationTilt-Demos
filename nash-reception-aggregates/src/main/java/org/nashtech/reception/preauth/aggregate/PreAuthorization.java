package org.nashtech.reception.preauth.aggregate;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.nashtech.reception.preauth.preauth.commands.CheckInCommand;
import org.nashtech.reception.preauth.preauth.commands.CheckOutCommand;
import org.nashtech.reception.preauth.preauth.events.*;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * The PreAuthorization aggregate represents a reservation's payment process, handling both
 * pre-authorization and authorization of funds for a hotel reservation.
 *
 * <p>This class listens for commands like {@link CheckInCommand} and {@link CheckOutCommand}
 * and applies events such as {@link PreAuthorizationApprove}, {@link PreAuthorizationDeclined},
 * {@link AuthorizationApprove}, and {@link AuthorizationDecline} to track the payment state
 * of the reservation.</p>
 *
 * <p>The aggregate ensures that sufficient funds are available before a reservation can proceed
 * with hotel services and that the authorization is appropriately handled based on the pre-authorization
 * outcome.</p>
 *
 * <p>The lifecycle of the aggregate is managed using the {@link EventSourcingHandler} annotations,
 * which listen to the applied events and modify the state accordingly.</p>
 */
@Aggregate
@Slf4j
public class PreAuthorization {

    @AggregateIdentifier
    private String reservationId;

    private PaymentState paymentState;

    PreAuthorization() {}

    @CommandHandler
    public PreAuthorization(CheckInCommand checkIn) {

        log.info("Pre Authorization event for id - {}.", checkIn.reservationId());
        var charge = CheckInCharge.builder()
                .reservationId(checkIn.reservationId())
                .checkInDate(checkIn.checkInDate())
                .hotelId(checkIn.hotelId())
                .roomNumber(checkIn.roomNumber())
                .roomType(checkIn.roomType())
                .amountHold(checkIn.amountHold())
                .build();
        if (checkIn.canAmountBeHold()) {

            log.info("Sufficient Amount available for hotel services for id {}",
                    checkIn.reservationId());
            var preAuthApproved = charge.toBuilder().paymentState(
                    PaymentState.PRE_AUTH_APPROVED.name()).build();

            apply(new PreAuthorizationApprove(checkIn.reservationId(), preAuthApproved));
        } else {

            var preAuthDecline = charge.toBuilder().paymentState(
                    PaymentState.PRE_AUTH_ERROR.name()).build();
            log.info("Sufficient amount not available for hotel services for id {}",
                   checkIn.reservationId());
            apply(new PreAuthorizationDeclined(charge.getReservationId(),
                    preAuthDecline));
        }

    }

    @CommandHandler
    public void on(CheckOutCommand checkOut) {

        log.info("Authorization event for id - {}.", checkOut.reservationId());

        var charge = CheckOutCharge.builder()
                .reservationId(reservationId)
                .checkOutDate(checkOut.checkOutDate())
                .hotelId(checkOut.checkOutDate())
                .roomNumber(checkOut.roomNumber())
                .finalBillAmount(checkOut.finalBillAmount())
                .additionalCharges(checkOut.additionalCharges())
                .build();
        if (this.paymentState == PaymentState.PRE_AUTH_APPROVED) {

            log.info("Pre-authorization done and authorization check " +
                    "met for id {}", checkOut.reservationId());
            var authApproved = charge.toBuilder().paymentState(
                    PaymentState.AUTH_APPROVED.name()).build();
            apply(new AuthorizationApprove(checkOut.reservationId(),
                    authApproved));
        } else {

            log.info("either Pre-authorization not done or authorization " +
                    "check failed for id {}", checkOut.reservationId());
            var authError = charge.toBuilder().paymentState(
                    PaymentState.AUTH_ERROR.name()).build();
            apply(new AuthorizationDecline(checkOut.reservationId(),
                    authError));
        }
    }

    @EventSourcingHandler
    public void on(PreAuthorizationApprove preAuthorizationApprove) {

        this.reservationId = preAuthorizationApprove.reservationId();
        this.paymentState = PaymentState.PRE_AUTH_APPROVED;
    }

    @EventSourcingHandler
    public void on(PreAuthorizationDeclined preAuthorizationDeclined) {

        this.reservationId = preAuthorizationDeclined.reservationId();
        this.paymentState = PaymentState.PRE_AUTH_ERROR;
    }


    @EventSourcingHandler
    public void on(AuthorizationApprove authorizationApprove) {

        this.reservationId = authorizationApprove.reservationId();
        this.paymentState = PaymentState.AUTH_APPROVED;
    }

    @EventSourcingHandler
    public void on(AuthorizationDecline authorizationDecline) {

        this.reservationId = authorizationDecline.reservationId();
        this.paymentState = PaymentState.AUTH_ERROR;
    }

}

package org.nashtech.reception.preauth.eventhandler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.nashtech.reception.preauth.entity.PreAuthCharge;
import org.nashtech.reception.preauth.preauth.events.AuthorizationApprove;
import org.nashtech.reception.preauth.preauth.events.AuthorizationDecline;
import org.nashtech.reception.preauth.preauth.events.PreAuthorizationApprove;
import org.nashtech.reception.preauth.preauth.events.PreAuthorizationDeclined;
import org.nashtech.reception.preauth.repository.PreAuthRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Manages the handling and storage of pre-authorization and authorization charge data
 * based on incoming events. The class listens for events related to pre-authorization and
 * authorization processing of reservations and stores the charge details in a repository.
 * <p>
 * The {@code ManagePreAuthCharge} class processes four types of events:
 * <ul>
 *     <li>{@link PreAuthorizationApprove} - Event fired when a pre-authorization is approved.</li>
 *     <li>{@link PreAuthorizationDeclined} - Event fired when a pre-authorization is declined.</li>
 *     <li>{@link AuthorizationApprove} - Event fired when an authorization is approved during checkout.</li>
 *     <li>{@link AuthorizationDecline} - Event fired when an authorization is declined during checkout.</li>
 * </ul>
 * Each of these events is handled by the corresponding {@code on} method, where charge data
 * is extracted, transformed, and saved in the {@link PreAuthRepository}.
 *
 * <p>The class is part of the {@code preauth-projection} processing group and uses
 * the {@link EventHandler} annotation to mark methods that handle the events. It leverages
 * {@link PreAuthCharge} to store the charge data after each event is processed.
 *
 * <p>In addition to processing the events, the class also logs relevant information about
 * each event processing step.
 */
@ProcessingGroup("preauth-projection")
@Component
@Slf4j
@AllArgsConstructor
public class ManagePreAuthCharge {

    private final PreAuthRepository preAuthRepository;

    @EventHandler
    public void on(PreAuthorizationApprove preAuthorizationApprove) {

        final var checkInCharge = preAuthorizationApprove.checkInCharge();
        log.info("Handling preAuthorizationApprove event for id {}",
                checkInCharge.getReservationId());
        final var preAuthCharge=  PreAuthCharge.builder()
                .reservationId(checkInCharge.getReservationId())
                .checkInDate(checkInCharge.getCheckInDate())
                .roomNumber(checkInCharge.getRoomNumber())
                .roomType(checkInCharge.getRoomType())
                .hotelId(checkInCharge.getHotelId())
                .amountHold(BigDecimal.valueOf(checkInCharge.getAmountHold()))
                .paymentState(checkInCharge.getPaymentState())
                .checkOutDate(null)
                .finalBillAmount(BigDecimal.ZERO)
                .additionalCharges(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        preAuthRepository.save(preAuthCharge);
    }

    @EventHandler
    public void on(PreAuthorizationDeclined preAuthorizationDeclined) {

        final var checkInCharge = preAuthorizationDeclined.checkInCharge();
        log.info("Handling preAuthorizationDecline event for id {}",
                checkInCharge.getReservationId());
        final var preAuthCharge=  PreAuthCharge.builder()
                .reservationId(checkInCharge.getReservationId())
                .checkInDate(checkInCharge.getCheckInDate())
                .roomNumber(checkInCharge.getRoomNumber())
                .roomType(checkInCharge.getRoomType())
                .hotelId(checkInCharge.getHotelId())
                .amountHold(BigDecimal.valueOf(checkInCharge.getAmountHold()))
                .paymentState(checkInCharge.getPaymentState())
                .checkOutDate(null)
                .finalBillAmount(BigDecimal.ZERO)
                .additionalCharges(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        preAuthRepository.save(preAuthCharge);
    }

    @EventHandler
    public void on(AuthorizationApprove authorizationApprove) {

        final var checkOutCharge = authorizationApprove.checkOutCharge();
        log.info("Handling authorizationApprove event for id {}",
                checkOutCharge.getReservationId());
        final var preAuthCharge=  PreAuthCharge.builder()
                .reservationId(checkOutCharge.getReservationId())
                .checkOutDate(checkOutCharge.getCheckOutDate())
                .paymentState(checkOutCharge.getPaymentState())
                .finalBillAmount(BigDecimal.valueOf(checkOutCharge.getFinalBillAmount()))
                .additionalCharges(BigDecimal.valueOf(checkOutCharge.getAdditionalCharges()))
                .createdAt(LocalDateTime.now())
                .build();

        preAuthRepository.save(preAuthCharge);
    }

    @EventHandler
    public void on(AuthorizationDecline authorizationDeclined) {

        final var checkOutCharge = authorizationDeclined.checkOutCharge();
        log.info("Handling authorizationDeclined event for id {}",
                checkOutCharge.getReservationId());
        final var preAuthCharge=  PreAuthCharge.builder()
                .reservationId(checkOutCharge.getReservationId())
                .checkOutDate(checkOutCharge.getCheckOutDate())
                .paymentState(checkOutCharge.getPaymentState())
                .finalBillAmount(BigDecimal.valueOf(checkOutCharge.getFinalBillAmount()))
                .additionalCharges(BigDecimal.valueOf(checkOutCharge.getAdditionalCharges()))
                .createdAt(LocalDateTime.now())
                .build();

        preAuthRepository.save(preAuthCharge);
    }

}

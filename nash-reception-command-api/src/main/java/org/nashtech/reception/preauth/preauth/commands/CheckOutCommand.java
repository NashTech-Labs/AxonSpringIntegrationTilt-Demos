package org.nashtech.reception.preauth.preauth.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CheckOutCommand(
        @TargetAggregateIdentifier String reservationId,
        String checkOutDate,
        String roomNumber,
        String hotelId,
        double finalBillAmount,
        double additionalCharges
) { }

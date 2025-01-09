package org.nashtech.reception.preauth.preauth.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CheckInCommand(
        @TargetAggregateIdentifier String reservationId,
        String checkInDate,
        String roomNumber,
        String roomType,
        String hotelId,
        double amountHold,
        boolean canAmountBeHold,
        String currency
) { }
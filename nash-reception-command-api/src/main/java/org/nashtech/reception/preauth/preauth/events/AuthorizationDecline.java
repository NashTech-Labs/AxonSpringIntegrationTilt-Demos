package org.nashtech.reception.preauth.preauth.events;

import org.nashtech.reception.preauth.preauth.commands.CheckOutCommand;

public record AuthorizationDecline(String reservationId, CheckOutCharge checkOutCharge) {
}

package org.nashtech.reception.preauth.preauth.events;


public record AuthorizationApprove(String reservationId, CheckOutCharge checkOutCharge) {
}

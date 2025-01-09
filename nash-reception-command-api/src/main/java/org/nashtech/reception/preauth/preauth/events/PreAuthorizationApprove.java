package org.nashtech.reception.preauth.preauth.events;

public record PreAuthorizationApprove(String reservationId, CheckInCharge checkInCharge) { }

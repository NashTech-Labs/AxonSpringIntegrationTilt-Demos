package org.nashtech.reception.preauth.preauth.events;

public record PreAuthorizationDeclined(String reservationId, CheckInCharge checkInCharge) { }

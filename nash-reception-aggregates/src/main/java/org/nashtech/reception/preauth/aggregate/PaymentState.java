package org.nashtech.reception.preauth.aggregate;

/**
 * States.
 */
public enum PaymentState {
    NEW,
    PRE_AUTH_APPROVED,
    PRE_AUTH_ERROR,
    AUTH_APPROVED,
    AUTH_ERROR
}

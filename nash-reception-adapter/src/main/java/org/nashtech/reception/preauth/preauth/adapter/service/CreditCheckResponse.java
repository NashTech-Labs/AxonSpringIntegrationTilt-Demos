package org.nashtech.reception.preauth.preauth.adapter.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCheckResponse {

    private double availableAmount;
    private String status;
    private String message;

    public CreditCheckResponse(double availableAmount, String status, String message) {
        this.availableAmount = availableAmount;
        this.status = status;
        this.message = message;
    }

}

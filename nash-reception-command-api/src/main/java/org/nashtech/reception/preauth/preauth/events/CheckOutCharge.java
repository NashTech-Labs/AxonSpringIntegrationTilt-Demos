package org.nashtech.reception.preauth.preauth.events;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class CheckOutCharge {

    private String reservationId;
    private String checkOutDate;
    private String roomNumber;
    private String hotelId;
    private double finalBillAmount;
    private double additionalCharges;
    private String paymentState;
}

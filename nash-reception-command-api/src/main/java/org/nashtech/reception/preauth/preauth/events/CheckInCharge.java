package org.nashtech.reception.preauth.preauth.events;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class CheckInCharge {

    private String reservationId;
    private String checkInDate;
    private String roomNumber;
    private String roomType;
    private String hotelId;
    private double amountHold;
    private String paymentState;
}

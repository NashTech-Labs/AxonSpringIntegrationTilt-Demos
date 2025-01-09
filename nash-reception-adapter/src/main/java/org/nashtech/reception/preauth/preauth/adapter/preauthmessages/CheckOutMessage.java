package org.nashtech.reception.preauth.preauth.adapter.preauthmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckOutMessage {

    @JsonProperty("metadata")
    private PreAuthMetadata metadata;

    @JsonProperty("data")
    private CheckOutData data;

    @Data
    public static class CheckOutData {
        @JsonProperty("reservationId")
        private String reservationId;

        @JsonProperty("guestName")
        private String guestName;

        @JsonProperty("guestEmail")
        private String guestEmail;

        @JsonProperty("checkOutDate")
        private String checkOutDate;

        @JsonProperty("roomNumber")
        private String roomNumber;

        @JsonProperty("hotelId")
        private String hotelId;

        @JsonProperty("roomType")
        private String roomType;

        @JsonProperty("amount")
        private double amount;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("paymentMethod")
        private String paymentMethod;

        @JsonProperty("preAuthorizationStatus")
        private String preAuthorizationStatus;

        @JsonProperty("checkOutTime")
        private String checkOutTime;

        @JsonProperty("finalBillAmount")
        private double finalBillAmount;

        @JsonProperty("additionalCharges")
        private double additionalCharges;
    }


}

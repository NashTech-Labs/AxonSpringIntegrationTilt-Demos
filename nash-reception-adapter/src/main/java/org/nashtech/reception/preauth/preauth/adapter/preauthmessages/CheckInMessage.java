package org.nashtech.reception.preauth.preauth.adapter.preauthmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckInMessage {

    @JsonProperty("metadata")
    private PreAuthMetadata metadata;

    @JsonProperty("data")
    private CheckInData data;

    @Data
    public static class CheckInData {
        @JsonProperty("reservationId")
        private String reservationId;

        @JsonProperty("guestName")
        private String guestName;

        @JsonProperty("guestEmail")
        private String guestEmail;

        @JsonProperty("checkInDate")
        private String checkInDate;

        @JsonProperty("roomNumber")
        private String roomNumber;

        @JsonProperty("hotelId")
        private String hotelId;

        @JsonProperty("roomType")
        private String roomType;

        @JsonProperty("amount")
        private double amountHold;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("paymentMethod")
        private String paymentMethod;

        @JsonProperty("preAuthorizationStatus")
        private String preAuthorizationStatus;

        @JsonProperty("arrivalTime")
        private String arrivalTime;

        @JsonProperty("specialRequests")
        private String specialRequests;
    }

}

package org.nashtech.reception.preauth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pre_auth_charge")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreAuthCharge {

    @Id
    @Column(name = "reservation_id", nullable = false, length = 50)
    private String reservationId;

    @Column(name = "check_in_date", length = 20)
    private String checkInDate;

    @Column(name = "check_out_date", length = 20)
    private String checkOutDate;

    @Column(name = "room_number", length = 20)
    private String roomNumber;

    @Column(name = "room_type", length = 50)
    private String roomType;

    @Column(name = "hotel_id", nullable = false, length = 50)
    private String hotelId;

    @Column(name = "amount_hold", precision = 10, scale = 2)
    private BigDecimal amountHold;

    @Column(name = "final_bill_amount", precision = 10, scale = 2)
    private BigDecimal finalBillAmount;

    @Column(name = "additional_charges", precision = 10, scale = 2)
    private BigDecimal additionalCharges;

    @Column(name = "payment_state", length = 50)
    private String paymentState;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}

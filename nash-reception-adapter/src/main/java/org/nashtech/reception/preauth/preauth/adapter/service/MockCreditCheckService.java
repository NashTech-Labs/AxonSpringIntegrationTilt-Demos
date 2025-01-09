package org.nashtech.reception.preauth.preauth.adapter.service;

import org.nashtech.reception.preauth.preauth.adapter.preauthmessages.CheckInMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock service for performing credit checks on a reservation.
 *
 * <p>This class is a placeholder for a real credit check service, simulating
 * a credit check operation that would typically involve a call to a payment
 * gateway or external financial system.</p>
 *
 * <h2>Features:</h2>
 * <ul>
 *     <li>Mocks credit check logic with random behavior for demonstration purposes.</li>
 *     <li>Exposes a REST API endpoint for testing the credit check functionality.</li>
 * </ul>
 *
 * <p>Intended for use in development or testing environments to validate integration
 * workflows.</p>
 *
 * @see CreditCheckResponse
 * @see CheckInMessage
 */
@RestController
@RequestMapping("/api/mock")
public class MockCreditCheckService {

    @PostMapping("/check-credit")
    public CreditCheckResponse checkCredit(@RequestBody CheckInMessage checkInMessage) {

        String reservationId = checkInMessage.getData().getReservationId();
        String guestName = checkInMessage.getData().getGuestName();

        // Mocking the credit check logic. it should be API call to payment gateway.
        double availableAmount = mockCreditCheck(checkInMessage.getData());

        // Return a mocked response
        return new CreditCheckResponse(availableAmount, "SUCCESS",
                "Mocked credit check completed for " + guestName);
    }

    private double mockCreditCheck(CheckInMessage.CheckInData data) {

        // Simulating available credit based on room amount for demonstration purposes
        double freezeAmount = data.getAmountHold();

        // Randomly decide whether to add or subtract 1.5 times the freeze amount
        boolean isMore = Math.random() < 0.5; // 50% chance
        double multiplier = isMore ? 1.5 : -1.5;

        // Calculate and return the result
        return Math.max(0, freezeAmount + (freezeAmount * multiplier));
    }
}

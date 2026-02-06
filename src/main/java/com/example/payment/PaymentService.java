package com.example.payment;

/**
 * Interface for payment gateway operations.
 * Provides an abstraction layer for processing payment transactions
 * with external payment service providers.
 */
public interface PaymentService {

    /**
     * Charges the specified amount using the payment gateway.
     *
     * @param amount the amount to charge, must be positive
     * @return a PaymentApiResponse containing the result of the charge operation
     * @throws RuntimeException if the payment gateway is unavailable or returns an error
     */
    PaymentApiResponse charge(double amount);
}

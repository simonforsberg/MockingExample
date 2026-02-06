package com.example.payment;

/**
 * Repository interface for persisting payment records.
 * Follows the Repository pattern to abstract database operations.
 */
public interface PaymentRepository {

    /**
     * Saves a payment record with the specified amount and status.
     *
     * @param amount the payment amount to save
     * @param status the status of the payment (e.g., "SUCCESS", "FAILURE")
     */
    void savePayment(double amount, String status);
}

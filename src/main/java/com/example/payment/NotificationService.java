package com.example.payment;

/**
 * Interface for sending payment-related notifications.
 * Abstracts the notification mechanism to allow different implementations
 */
public interface NotificationService {

    /**
     * Sends a payment confirmation notification to the specified email address.
     *
     * @param email  the recipient's email address
     * @param amount the payment amount to include in the confirmation
     */
    void sendPaymentConfirmation(String email, double amount);
}

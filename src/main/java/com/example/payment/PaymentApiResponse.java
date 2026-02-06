package com.example.payment;

/**
 * Represents the response from a payment processing operation.
 * This immutable record encapsulates the result of a payment transaction.
 *
 * @param success indicates whether the payment was processed successfully
 * @param message a descriptive message about the payment result
 */
public record PaymentApiResponse(boolean success, String message) {
}

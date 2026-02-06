package com.example.payment;

/**
 * Processes payment transactions by coordinating payment gateway operations,
 * data persistence, and customer notifications.
 * <p>
 * This class follows the Dependency Injection pattern, accepting all dependencies
 * through its constructor to ensure testability.
 */
public class PaymentProcessor {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    /**
     * Constructs a PaymentProcessor with the specified dependencies.
     *
     * @param paymentService      the service for processing payment transactions
     * @param paymentRepository   the repository for persisting payment records
     * @param notificationService the service for sending notifications
     */
    public PaymentProcessor(
            PaymentService paymentService,
            PaymentRepository paymentRepository,
            NotificationService notificationService) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    /**
     * Processes a payment transaction for the specified amount.
     * <p>
     * If the payment is successful, this method will:
     * <ul>
     *   <li>Save the payment record to the repository</li>
     *   <li>Send a confirmation notification to the customer</li>
     * </ul>
     *
     * If the payment fails, no repository or notification operations are performed.
     *
     * @param amount the amount to charge
     * @param email the customer's email address for confirmation
     * @return true if the payment was successful, false otherwise
     * @throws RuntimeException if the payment service is unavailable
     */
    public boolean processPayment(double amount, String email) {
        PaymentApiResponse response = paymentService.charge(amount);

        if (response.success()) {
            paymentRepository.savePayment(amount, "SUCCESS");
            notificationService.sendPaymentConfirmation(email, amount);
        }

        return response.success();
    }
}
package com.example.payment;

public class PaymentProcessor {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public PaymentProcessor(
            PaymentService paymentService,
            PaymentRepository paymentRepository,
            NotificationService notificationService) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    public boolean processPayment(double amount, String email) {
        PaymentApiResponse response = paymentService.charge(amount);

        if (response.success()) {
            paymentRepository.savePayment(amount, "SUCCESS");
            notificationService.sendPaymentConfirmation(email, amount);
        }

        return response.success();
    }
}
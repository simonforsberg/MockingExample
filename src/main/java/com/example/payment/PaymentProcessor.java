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

    public boolean processPayment(String email, double amount) {
        PaymentApiResponse response = paymentService.charge(amount);

        if (response.isSuccess()) {
            paymentRepository.savePayment(amount, "SUCCESS");
        }

        if (response.isSuccess()) {
            notificationService.sendPaymentConfirmation(email, amount);
        }

        return response.isSuccess();
    }
}
//    private static final String API_KEY = "sk_test_123456";
//
//    public boolean processPayment(double amount) {
//        // Anropar extern betaltjänst direkt med statisk API-nyckel
//        PaymentApiResponse response = PaymentApi.charge(API_KEY, amount);
//
//        // Skriver till databas direkt
//        if (response.isSuccess()) {
//            DatabaseConnection.getInstance()
//                    .executeUpdate("INSERT INTO payments (amount, status) VALUES (" + amount + ", 'SUCCESS')");
//        }
//
//        // Skickar e-post direkt
//        if (response.isSuccess()) {
//            EmailService.sendPaymentConfirmation("user@example.com", amount);
//        }
//
//        return response.isSuccess();
//    }
//}

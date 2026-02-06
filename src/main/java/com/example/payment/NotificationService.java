package com.example.payment;

public interface NotificationService {
    void sendPaymentConfirmation(String email, double amount);
}

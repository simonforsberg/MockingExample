package com.example.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentProcessor paymentProcessor;

    @Test
    void processPayment_shouldSaveAndNotify_whenSuccessful() {
        // Arrange
        double amount = 100.0;
        String email = "simon.forsberg@iths.se";
        PaymentApiResponse successResponse = new PaymentApiResponse(true, "SUCCESS");
        when(paymentService.charge(amount)).thenReturn(successResponse);
        // Act
        boolean result = paymentProcessor.processPayment(amount, email);
        // Assert
        assertTrue(result);

        verify(paymentRepository).savePayment(amount, "SUCCESS");
        verify(notificationService).sendPaymentConfirmation(email, amount);
    }

    @Test
    void processPayment_shouldNotSaveAndNotify_whenUnsuccessful() {
        // Arrange
        double amount = 100.0;
        String email = "simon.forsberg@iths.se";
        PaymentApiResponse failureResponse = new PaymentApiResponse(false, "FAILURE");
        when(paymentService.charge(amount)).thenReturn(failureResponse);
        // Act
        boolean result = paymentProcessor.processPayment(amount, email);
        // Assert
        assertFalse(result);

        verifyNoInteractions(paymentRepository, notificationService);
    }
}
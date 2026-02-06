package com.example.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PaymentProcessor}.
 * <p>
 * Uses Mockito for dependency mocking.
 * Tests verify the behavior of payment processing.
 */
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

    /**
     * Verifies that a successful payment results in:
     * - Payment record being saved
     * - Confirmation notification being sent
     * - Method returning true
     */
    @Test
    void processPayment_shouldSaveAndNotify_whenSuccessful() {
        // Arrange
        double amount = 100.0;
        String email = "customer@example.com";
        PaymentApiResponse successResponse = new PaymentApiResponse(true, "SUCCESS");
        when(paymentService.charge(amount)).thenReturn(successResponse);
        // Act
        boolean result = paymentProcessor.processPayment(amount, email);
        // Assert
        assertTrue(result);

        verify(paymentService).charge(amount);
        verify(paymentRepository).savePayment(amount, "SUCCESS");
        verify(notificationService).sendPaymentConfirmation(email, amount);
    }

    /**
     * Verifies that a failed payment results in:
     * - No payment record being saved
     * - No confirmation notification being sent
     * - Method returning false
     */
    @Test
    void processPayment_shouldNotSaveAndNotify_whenUnsuccessful() {
        // Arrange
        double amount = 100.0;
        String email = "customer@example.com";
        PaymentApiResponse failureResponse = new PaymentApiResponse(false, "FAILURE");
        when(paymentService.charge(amount)).thenReturn(failureResponse);
        // Act
        boolean result = paymentProcessor.processPayment(amount, email);
        // Assert
        assertFalse(result);

        verifyNoInteractions(paymentRepository, notificationService);
    }
}
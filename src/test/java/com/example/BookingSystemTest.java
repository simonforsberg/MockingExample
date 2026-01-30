package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingSystem bookingSystem;

    private static final LocalDateTime NOW =
            LocalDateTime.of(2026, 1, 29, 9, 0);

    @BeforeEach
    void setUp() {
        when(timeProvider.getCurrentTime()).thenReturn(NOW);
    }

    @Test
    @DisplayName("bookRoom returnerar true när rummet är ledigt")
    void bookRoom_shouldReturnTrue_whenRoomIsAvailable() throws NotificationException {
        // Arrange
        String roomId = "room01";
        LocalDateTime startTime = NOW.plusDays(1);
        LocalDateTime endTime = NOW.plusDays(2);
        Room room = new Room(roomId, "Dubbelrum");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);

        // Assert
        assertThat(result).isTrue();

        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

}
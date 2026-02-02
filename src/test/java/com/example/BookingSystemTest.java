package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("bookRoom returnerar true när rummet är ledigt")
    void bookRoom_shouldReturnTrue_whenRoomIsAvailable() throws NotificationException {
        // Arrange
        String roomId = "room01";
        LocalDateTime startTime = NOW.plusDays(1);
        LocalDateTime endTime = NOW.plusDays(2);
        Room room = new Room(roomId, "Dubbelrum");

        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);

        // Assert
        assertThat(result).isTrue();

        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    @DisplayName("bookRoom returnerar false när rummet inte är ledigt")
    void bookRoom_shouldReturnFalse_whenRoomIsNotAvailable() throws NotificationException {
        // Arrange
        String roomId = "room01";
        LocalDateTime startTime = NOW.plusDays(1);
        LocalDateTime endTime = NOW.plusDays(2);
        Room room = new Room(roomId, "Dubbelrum");

        room.addBooking(new Booking("existing-booking", roomId, startTime.minusHours(1), endTime.plusHours(1)));

        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        // Act
        boolean result = bookingSystem.bookRoom(roomId, startTime, endTime);

        // Assert
        assertThat(result).isFalse();

        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }

    @Test
    @DisplayName("bookRoom kastar exception om starttid är före nutid")
    void bookRoom_shouldThrowException_whenStartTimeIsBeforeNow() {
        // Arrange
        String roomId = "room01";
        LocalDateTime beforeNow = NOW.minusDays(1);
        LocalDateTime endTime = NOW.plusDays(2);

        when(timeProvider.getCurrentTime()).thenReturn(NOW);

        // Act + Assert
        assertThatThrownBy(() ->
                bookingSystem.bookRoom(roomId, beforeNow, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kan inte boka tid i dåtid");

        verifyNoInteractions(roomRepository, notificationService);
    }

    @ParameterizedTest(name = "roomId={0}, startTime={1}, endTime={2}")
    @MethodSource("invalidBookingInputs")
    @DisplayName("bookRoom kastar exception vid ogiltiga null-värden")
    void bookRoom_shouldThrowException_whenInputIsNull(String roomId, LocalDateTime startTime, LocalDateTime endTime) {
        // Act + Assert
        assertThatThrownBy(() ->
                bookingSystem.bookRoom(roomId, startTime, endTime)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga");
    }
    static Stream<Arguments> invalidBookingInputs() {
        return Stream.of(
                Arguments.of(null, NOW.plusDays(1), NOW.plusDays(2)),
                Arguments.of("room01", null, NOW.plusDays(2)),
                Arguments.of("room01", NOW.plusDays(1), null)
        );
    }

    @ParameterizedTest(name = "start={0}, end={1}")
    @MethodSource("invalidTimeRanges")
    @DisplayName("bookRoom kastar exception när sluttid är före starttid")
    void bookRoom_shouldThrowException_whenEndTimeIsBeforeStartTime(LocalDateTime startTime, LocalDateTime endTime) {
        // Arrange
        when(timeProvider.getCurrentTime()).thenReturn(NOW);

        // Act + Assert
        assertThatThrownBy(() ->
                bookingSystem.bookRoom("room01", startTime, endTime)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sluttid måste vara efter starttid");
    }
    static Stream<Arguments> invalidTimeRanges() {
        return Stream.of(
                Arguments.of(NOW.plusDays(2), NOW.plusDays(1)),
                Arguments.of(NOW.plusHours(2), NOW.plusHours(1))
        );
    }

    @Test
    @DisplayName("bookRoom kastar exception när rummet inte finns")
    void bookRoom_shouldThrowException_whenRoomDoesNotExist() {
        // Arrange
        String roomId = "non-existent";

        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() ->
                bookingSystem.bookRoom(roomId, NOW.plusDays(1), NOW.plusDays(2))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rummet existerar inte");

        verifyNoInteractions(notificationService);
    }

}
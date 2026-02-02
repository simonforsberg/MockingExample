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
import java.util.List;
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

    // -------------------
    // tester för bookRoom
    // -------------------

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

        verifyNoInteractions(roomRepository, notificationService);
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

        verifyNoInteractions(roomRepository, notificationService);
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

    @Test
    @DisplayName("bookRoom lyckas även när notifiering misslyckas")
    void bookRoom_shouldSucceed_whenNotificationFails() throws NotificationException {
        // Arrange
        String roomId = "room01";
        Room room = new Room(roomId, "Dubbelrum");

        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        doThrow(new NotificationException("Notifiering misslyckades"))
                .when(notificationService)
                .sendBookingConfirmation(any(Booking.class));

        // Act
        boolean result = bookingSystem.bookRoom(roomId, NOW.plusDays(1), NOW.plusDays(2));

        // Assert
        assertThat(result).isTrue();

        verify(roomRepository).save(room);
    }

    // ----------------------------
    // tester för getAvailableRooms
    // ----------------------------

    @Test
    @DisplayName("getAvailableRooms returnerar alla rum när inga är bokade")
    void getAvailableRooms_shouldReturnAllRooms_whenNoRoomsAreBooked() {
        // Arrange
        Room room1 = new Room("room01", "Dubbelrum");
        Room room2 = new Room("room02", "Dubbelrum");
        Room room3 = new Room("room03", "Enkelrum");

        LocalDateTime startTime = NOW.plusDays(1);
        LocalDateTime endTime = NOW.plusDays(2);

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2, room3));

        // Act
        List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);

        // Assert
        assertThat(availableRooms)
                .hasSize(3)
                .contains(room1, room2, room3);
    }

    // TODO: några rum är bokade
    @Test
    void getAvailableRooms_shouldReturnAvailableRooms_whenSomeRoomsAreBooked() {
        // Arrange

        // Act

        // Assert
    }

    @Test
    @DisplayName("getAvailableRooms returnerar inga rum när alla är bokade")
    void getAvailableRooms_shouldReturnNoRooms_whenAllRoomsAreBooked() {
        // Arrange
        LocalDateTime startTime = NOW.plusDays(1);
        LocalDateTime endTime = NOW.plusDays(2);

        Room bookedRoom1 = new Room("room01", "Dubbelrum");
        bookedRoom1.addBooking(new Booking("booking01", "room01", startTime, endTime));

        Room bookedRoom2 = new Room("room02", "Dubbelrum");
        bookedRoom2.addBooking(new Booking("booking02", "room02", startTime, endTime));

        Room bookedRoom3 = new Room("room03", "Enkelrum");
        bookedRoom3.addBooking(new Booking("booking03", "room03", startTime, endTime));

        when(roomRepository.findAll()).thenReturn(List.of(bookedRoom1, bookedRoom2, bookedRoom3));

        // Act
        List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);

        // Assert
        assertThat(availableRooms).isEmpty();
    }

    // ------------------------
    // tester för cancelBooking
    // ------------------------

    @Test
    @DisplayName("cancelBooking returnerar true när framtida bokning finns")
    void cancelBooking_shouldReturnTrue_whenBookingExists() throws NotificationException {
        // Arrange
        String roomId = "room01";
        String bookingId = "booking01";

        Room room = new Room(roomId, "Dubbelrum");
        room.addBooking(new Booking(bookingId, roomId, NOW.plusDays(1), NOW.plusDays(2)));

        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        // Act
        boolean result = bookingSystem.cancelBooking(bookingId);

        // Assert
        assertThat(result).isTrue();

        verify(roomRepository).save(room);
        verify(notificationService).sendCancellationConfirmation(any(Booking.class));
    }

    @Test
    @DisplayName("cancelBooking returnerar false när framtida bokning inte finns")
    void cancelBooking_shouldReturnFalse_whenBookingDoesNotExist() throws NotificationException {
        // Arrange
        Room room = new Room("room01", "Dubbelrum");

        when(roomRepository.findAll()).thenReturn(List.of(room));

        // Act
        boolean result = bookingSystem.cancelBooking("non-existent booking");

        // Assert
        assertThat(result).isFalse();

        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendCancellationConfirmation(any());
    }

    @Test
    @DisplayName("cancelBooking returnerar false när en bokning pågår eller passerat")
    void cancelBooking_shouldReturnFalse_whenBookingHasStarted() {
        // Arrange
        String bookingId = "booking01";
        String roomId = "room01";
        Room room = new Room(roomId, "Dubbelrum");
        room.addBooking(new Booking(bookingId, roomId, NOW.minusHours(1), NOW.plusDays(1)));

        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        // Act + Assert
        assertThatThrownBy(() -> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Kan inte avboka påbörjad eller avslutad bokning");

        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancelBooking kastar exception när boknings-ID är null")
    void cancelBooking_shouldThrowException_whenBookingIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Boknings-id kan inte vara null");

        verifyNoInteractions(roomRepository, notificationService);
    }

    @Test
    @DisplayName("cancelBooking lyckas även när notifiering misslyckas")
    void cancelBooking_shouldSucceed_whenNotificationFails() throws NotificationException {
        // Arrange
        String roomId = "room01";
        String bookingId = "booking01";
        Room room = new Room(roomId, "Dubbelrum");
        room.addBooking(new Booking(bookingId, roomId, NOW.plusDays(1), NOW.plusDays(2)));

        when(timeProvider.getCurrentTime()).thenReturn(NOW);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        doThrow(new NotificationException("Notifiering misslyckades"))
                .when(notificationService)
                .sendCancellationConfirmation(any(Booking.class));

        // Act
        boolean result = bookingSystem.cancelBooking(bookingId);

        // Assert
        assertThat(result).isTrue();
        assertThat(room.hasBooking(bookingId)).isFalse();

        verify(roomRepository).save(room);
    }

}
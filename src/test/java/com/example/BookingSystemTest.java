package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BookingSystem}.
 * <p>
 * Uses Mockito to create test doubles for dependencies.
 * Tests are organized by method using nested classes.
 */
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

    /**
     * Tests for {@link BookingSystem#bookRoom(String, LocalDateTime, LocalDateTime)}.
     * <p>
     * Covers validation, successful bookings, unavailable rooms, and error handling.
     */
    @Nested
    @DisplayName("bookRoom() test suite")
    class BookRoomTests {

        /**
         * Verifies that booking an available room returns true
         * and triggers both repository save and notification.
         */
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

        /**
         * Verifies that booking an unavailable room returns false
         * without saving or sending notifications.
         */
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

        /**
         * Verifies that attempting to book a time in the past throws {@link IllegalArgumentException}.
         */
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

        /**
         * Verifies that null input values throw {@link IllegalArgumentException}.
         * <p>
         * Tests three scenarios: null roomId, null startTime, and null endTime.
         */
        @ParameterizedTest(name = "roomId={0}, startTime={1}, endTime={2}")
        @MethodSource("invalidBookingInputs")
        @DisplayName("bookRoom kastar exception vid null-värden")
        void bookRoom_shouldThrowException_whenInputIsNull(String roomId, LocalDateTime startTime, LocalDateTime endTime) {
            // Act + Assert
            assertThatThrownBy(() ->
                    bookingSystem.bookRoom(roomId, startTime, endTime)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bokning kräver giltiga");

            verifyNoInteractions(roomRepository, notificationService);
        }

        /**
         * Provides test data for null input validation.
         */
        static Stream<Arguments> invalidBookingInputs() {
            return Stream.of(
                    Arguments.of(null, NOW.plusDays(1), NOW.plusDays(2)),
                    Arguments.of("room01", null, NOW.plusDays(2)),
                    Arguments.of("room01", NOW.plusDays(1), null)
            );
        }

        /**
         * Verifies that an end time before start time throws {@link IllegalArgumentException}.
         * <p>
         * Tests multiple invalid time ranges.
         */
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

        /**
         * Verifies that attempting to book a non-existent room throw {@link IllegalArgumentException}.
         */
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

        /**
         * Verifies that booking succeeds even when notification fails.
         * <p>
         * Tests the resilience of the system - booking is saved despite notification errors.
         */
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
    }

    /**
     * Tests for {@link BookingSystem#getAvailableRooms(LocalDateTime, LocalDateTime)}.
     * <p>
     * Covers filtering logic, empty results, and validation.
     */
    @Nested
    @DisplayName("getAvailableRooms() test suite")
    class GetAvailableRoomsTests {

        /**
         * Verifies that all rooms are returned when none have bookings in the requested time range.
         */
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
                    .containsExactlyInAnyOrder(room1, room2, room3);
        }

        /**
         * Verifies that only available rooms are returned when some rooms are booked.
         * <p>
         * Tests filtering logic with rooms that have no bookings, non-overlapping bookings,
         * and overlapping bookings.
         */
        @Test
        @DisplayName("getAvailableRooms returnerar lediga rum")
        void getAvailableRooms_shouldReturnAvailableRooms_whenSomeRoomsAreBooked() {
            // Arrange
            LocalDateTime startTime = NOW.plusDays(1).withHour(15);
            LocalDateTime endTime = NOW.plusDays(2).withHour(11);

            Room availableRoom1 = new Room("room01", "Dubbelrum"); // Ledigt

            Room availableRoom2 = new Room("room02", "Dubbelrum"); // Ledigt efter önskad tid
            availableRoom2.addBooking(new Booking("booking02", "room02", NOW.plusDays(2).withHour(12), NOW.plusDays(3).withHour(11)));

            Room bookedRoom = new Room("room03", "Enkelrum"); // Bokat under önskad tid
            bookedRoom.addBooking(new Booking("booking03", "room03", NOW.plusDays(1).withHour(15), NOW.plusDays(3).withHour(11)));

            when(roomRepository.findAll()).thenReturn(
                    List.of(availableRoom1, availableRoom2, bookedRoom)
            );

            // Act
            List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);

            // Assert
            assertThat(availableRooms)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(availableRoom1, availableRoom2)
                    .doesNotContain(bookedRoom);
        }

        /**
         * Verifies that an empty list is returned when all rooms are booked.
         */
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

        /**
         * Verifies that null time values throw {@link IllegalArgumentException}.
         * <p>
         * Tests both null startTime and null endTime.
         */
        @ParameterizedTest(name = "startTime={0}, endTime={1}")
        @MethodSource("nullTimeInputs")
        @DisplayName("getAvailableRooms kastar exception vid null-värden")
        void getAvailableRooms_shouldThrowException_whenTimeIsNull(LocalDateTime startTime, LocalDateTime endTime) {
            // Act & Assert
            assertThatThrownBy(() ->
                    bookingSystem.getAvailableRooms(startTime, endTime)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Måste ange både start- och sluttid");

            verifyNoInteractions(roomRepository);
        }

        /**
         * Provides test data for null time validation.
         */
        static Stream<Arguments> nullTimeInputs() {
            return Stream.of(
                    Arguments.of(null, NOW.plusDays(1)),
                    Arguments.of(NOW.plusDays(1), null)
            );
        }

        /**
         * Verifies that an end time before start time throws {@link IllegalArgumentException}.
         */
        @Test
        @DisplayName("getAvailableRooms kastar exception när endTime är före startTime")
        void getAvailableRooms_shouldThrowException_whenEndTimeIsBeforeStartTime() {
            // Arrange
            LocalDateTime start = NOW.plusDays(2);
            LocalDateTime end = NOW.plusDays(1);

            // Act & Assert
            assertThatThrownBy(() ->
                    bookingSystem.getAvailableRooms(start, end)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Sluttid måste vara efter starttid");

            verifyNoInteractions(roomRepository);
        }
    }

    /**
     * Tests for {@link BookingSystem#cancelBooking(String)}.
     * <p>
     * Covers successful cancellation, non-existent bookings, validation, and error handling.
     */
    @Nested
    @DisplayName("cancelBooking() test suite")
    class CancelBookingTests {

        /**
         * Verifies that cancelling an existing future booking returns true
         * and triggers both repository save and notification.
         */
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

        /**
         * Verifies that attempting to cancel a non-existent booking returns false
         * without triggering save or notification.
         */
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

        /**
         * Verifies that attempting to cancel a started or completed booking throws {@link IllegalStateException}.
         */
        @Test
        @DisplayName("cancelBooking kastar exception när en bokning pågår eller passerat")
        void cancelBooking_shouldThrowException_whenBookingHasStarted() {
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

        /**
         * Verifies that a null booking ID throws {@link IllegalArgumentException}.
         */
        @Test
        @DisplayName("cancelBooking kastar exception när boknings-ID är null")
        void cancelBooking_shouldThrowException_whenBookingIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Boknings-id kan inte vara null");

            verifyNoInteractions(roomRepository, notificationService);
        }

        /**
         * Verifies that cancellation succeeds even when notification fails.
         * <p>
         * Tests the resilience of the system - cancellation is saved despite notification errors.
         */
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
}
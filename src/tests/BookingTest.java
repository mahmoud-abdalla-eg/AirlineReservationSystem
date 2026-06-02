package tests;

import models.Booking;
import models.Seat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test for Booking and Seat models
 */
class BookingTest {

    @Test
    void testBookingCreation() {
        Booking booking = new Booking("BK1", "AI101", 1, "12A", 350.00);

        assertEquals("BK1", booking.getBookingId());
        assertEquals("AI101", booking.getFlightNumber());
        assertEquals(1, booking.getPassengerId());
        assertEquals("12A", booking.getSeatId());
        assertEquals(350.00, booking.getTotalPrice());
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        assertNotNull(booking.getBookingReference());
        assertEquals(6, booking.getBookingReference().length());
    }

    @Test
    void testBookingReferenceIsUnique() {
        Booking b1 = new Booking("BK1", "AI101", 1, "12A", 350.00);
        Booking b2 = new Booking("BK2", "AI101", 2, "12B", 350.00);
        assertNotEquals(b1.getBookingReference(), b2.getBookingReference());
    }

    @Test
    void testCancelBooking() {
        Booking booking = new Booking("BK1", "AI101", 1, "12A", 350.00);
        assertTrue(booking.cancel());
        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
        assertFalse(booking.cancel()); // Already cancelled
    }

    @Test
    void testCheckIn() {
        Booking booking = new Booking("BK1", "AI101", 1, "12A", 350.00);
        assertTrue(booking.checkIn());
        assertEquals(Booking.BookingStatus.CHECKED_IN, booking.getStatus());
        assertFalse(booking.checkIn()); // Already checked in
    }

    @Test
    void testBoard() {
        Booking booking = new Booking("BK1", "AI101", 1, "12A", 350.00);
        booking.checkIn();
        assertTrue(booking.board());
        assertEquals(Booking.BookingStatus.BOARDED, booking.getStatus());
    }

    @Test
    void testSeatCreation() {
        Seat seat = new Seat("12A", "AI101", 12, "A", Seat.SeatClass.ECONOMY);
        assertEquals("12A", seat.getSeatId());
        assertEquals("AI101", seat.getFlightNumber());
        assertEquals(12, seat.getRow());
        assertEquals("A", seat.getColumn());
        assertEquals(Seat.SeatClass.ECONOMY, seat.getSeatClass());
        assertTrue(seat.isAvailable());
    }

    @Test
    void testSeatPricing() {
        Seat economySeat = new Seat("15A", "AI101", 15, "A", Seat.SeatClass.ECONOMY);
        Seat firstClassSeat = new Seat("2A", "AI101", 2, "A", Seat.SeatClass.FIRST_CLASS);

        assertEquals(50.0, economySeat.getPrice(), 0.01);
        assertEquals(500.0, firstClassSeat.getPrice(), 0.01);
    }

    @Test
    void testGetFinalPrice() {
        Seat seat = new Seat("2A", "AI101", 2, "A", Seat.SeatClass.FIRST_CLASS);
        double finalPrice = seat.getFinalPrice(300.0);
        // Front row gets 20% premium + first class base
        assertTrue(finalPrice > 300.0);
    }
}
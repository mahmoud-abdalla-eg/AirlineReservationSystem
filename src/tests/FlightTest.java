package tests;

import models.Flight;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test for Flight model
 */
class FlightTest {

    @Test
    void testFlightCreation() {
        Flight flight = new Flight(
                "AI101", "New York (JFK)", "Los Angeles (LAX)",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                180, 299.99, "Boeing 737"
        );

        assertEquals("AI101", flight.getFlightNumber());
        assertEquals("New York (JFK)", flight.getOrigin());
        assertEquals("Los Angeles (LAX)", flight.getDestination());
        assertEquals(180, flight.getTotalSeats());
        assertEquals(180, flight.getAvailableSeats());
        assertEquals(299.99, flight.getBasePrice());
        assertTrue(flight.isActive());
    }

    @Test
    void testCalculateCurrentPrice_NoSurge() {
        Flight flight = new Flight(
                "AI101", "A", "B",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                100, 200.0, "Boeing 737"
        );
        // Low occupancy (<50%) - no surge
        assertEquals(200.0, flight.calculateCurrentPrice(), 0.01);
    }

    @Test
    void testCalculateCurrentPrice_WithSurge() {
        Flight flight = new Flight(
                "AI101", "A", "B",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                100, 200.0, "Boeing 737"
        );
        flight.setAvailableSeats(10); // 90% occupied - 30% surge
        assertEquals(260.0, flight.calculateCurrentPrice(), 0.01);
    }

    @Test
    void testGetDurationHours() {
        LocalDateTime departure = LocalDateTime.of(2026, 5, 1, 8, 0);
        LocalDateTime arrival = LocalDateTime.of(2026, 5, 1, 11, 30);

        Flight flight = new Flight("AI101", "A", "B", departure, arrival, 100, 200.0, "Test");
        assertEquals(3.5, flight.getDurationHours(), 0.01);
    }

    @Test
    void testFlightIsAvailable() {
        Flight flight = new Flight(
                "AI101", "A", "B",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                100, 200.0, "Boeing 737"
        );

        assertTrue(flight.isAvailable());
        flight.setAvailableSeats(0);
        assertFalse(flight.isAvailable());
    }
}
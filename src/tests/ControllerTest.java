package tests;

import controllers.FlightController;
import controllers.PassengerController;
import controllers.BookingController;
import models.Flight;
import models.Passenger;
import models.Booking;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test for controllers
 */
class ControllerTest {

    @Test
    void testAddAndFindFlight() {
        FlightController controller = new FlightController();
        int initialSize = controller.getAllFlights().size();

        Flight flight = new Flight(
                "TEST1", "Test Origin", "Test Destination",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(3),
                100, 199.99, "Test Aircraft"
        );

        assertTrue(controller.addFlight(flight));
        assertEquals(initialSize + 1, controller.getAllFlights().size());

        Flight found = controller.findByFlightNumber("TEST1");
        assertNotNull(found);
        assertEquals("Test Origin", found.getOrigin());
    }

    @Test
    void testDuplicateFlightNumber() {
        FlightController controller = new FlightController();

        Flight flight1 = new Flight(
                "DUP1", "A", "B",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                100, 100.0, "Plane"
        );
        controller.addFlight(flight1);

        Flight flight2 = new Flight(
                "DUP1", "C", "D",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(2),
                100, 200.0, "Plane"
        );
        assertFalse(controller.addFlight(flight2));
    }

    @Test
    void testSearchFlights() {
        FlightController controller = new FlightController();
        List<Flight> results = controller.searchFlights("New York", "London");
        assertFalse(results.isEmpty());
    }

    @Test
    void testAddPassenger() {
        PassengerController controller = new PassengerController();
        int initialSize = controller.getAllPassengers().size();

        Passenger passenger = new Passenger(
                0, "Test", "User", "test@email.com", "555-9999",
                "ZZ999999", LocalDate.of(1995, 1, 1), "TestNationality"
        );

        assertTrue(controller.addPassenger(passenger));
        assertEquals(initialSize + 1, controller.getAllPassengers().size());
    }

    @Test
    void testDuplicatePassengerEmail() {
        PassengerController controller = new PassengerController();

        Passenger p1 = new Passenger(0, "John", "Doe", "dup@email.com", "555-0100", "AA111111", LocalDate.of(1990, 1, 1), "American");
        controller.addPassenger(p1);

        Passenger p2 = new Passenger(0, "Jane", "Doe", "dup@email.com", "555-0200", "BB222222", LocalDate.of(1991, 1, 1), "British");
        assertFalse(controller.addPassenger(p2));
    }

    @Test
    void testFindPassengerByPassport() {
        PassengerController controller = new PassengerController();
        controller.addPassenger(new Passenger(0, "John", "Doe", "john@email.com", "555-0100", "XX123456", LocalDate.of(1990, 1, 1), "American"));

        Passenger found = controller.findByPassport("XX123456");
        assertNotNull(found);
        assertEquals("John", found.getFirstName());
    }

    @Test
    void testCreateBooking() {
        FlightController flightController = new FlightController();
        BookingController bookingController = new BookingController(flightController);

        // Initialize seats for test flight
        bookingController.initializeSeatsForFlight("TESTFL", 30);

        Booking booking = bookingController.createBooking("AI101", 1, "15A");
        assertNotNull(booking);
        assertEquals("AI101", booking.getFlightNumber());
        assertEquals(1, booking.getPassengerId());
    }

    @Test
    void testCancelBooking() {
        FlightController flightController = new FlightController();
        BookingController bookingController = new BookingController(flightController);

        bookingController.initializeSeatsForFlight("TESTFL2", 30);

        Booking booking = bookingController.createBooking("AI101", 1, "20A");
        assertNotNull(booking);

        assertTrue(bookingController.cancelBooking(booking.getBookingId()));
    }
}
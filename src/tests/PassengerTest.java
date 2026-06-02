package tests;

import models.Passenger;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test for Passenger model
 */
class PassengerTest {

    @Test
    void testPassengerCreation() {
        Passenger passenger = new Passenger(
                1, "John", "Doe", "john@email.com", "555-0100",
                "AB123456", LocalDate.of(1990, 5, 15), "American"
        );

        assertEquals(1, passenger.getPassengerId());
        assertEquals("John", passenger.getFirstName());
        assertEquals("Doe", passenger.getLastName());
        assertEquals("john@email.com", passenger.getEmail());
        assertEquals("AB123456", passenger.getPassportNumber());
        assertEquals("American", passenger.getNationality());
        assertTrue(passenger.isActive());
    }

    @Test
    void testGetFullName() {
        Passenger passenger = new Passenger(
                1, "John", "Doe", "john@email.com", "555-0100",
                "AB123456", LocalDate.of(1990, 5, 15), "American"
        );
        assertEquals("John Doe", passenger.getFullName());
    }

    @Test
    void testGetAge() {
        Passenger passenger = new Passenger(
                1, "John", "Doe", "john@email.com", "555-0100",
                "AB123456", LocalDate.of(1990, 1, 1), "American"
        );
        int age = passenger.getAge();
        assertTrue(age >= 35 && age <= 37); // Age depends on current date
    }

    @Test
    void testIsValidPassport() {
        Passenger passenger = new Passenger();
        passenger.setPassportNumber("AB123456");
        assertTrue(passenger.isValidPassport());

        passenger.setPassportNumber("ABC");
        assertFalse(passenger.isValidPassport());
    }

    @Test
    void testPassengerEquals() {
        Passenger p1 = new Passenger(1, "John", "Doe", "john@email.com", "555-0100", "AB123456", LocalDate.of(1990, 5, 15), "American");
        Passenger p2 = new Passenger(1, "John", "Doe", "john@email.com", "555-0100", "AB123456", LocalDate.of(1990, 5, 15), "American");
        assertEquals(p1, p2);
    }
}
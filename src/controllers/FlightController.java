package controllers;

import models.Flight;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for Flight operations
 */
public class FlightController {
    private List<Flight> flights;

    public FlightController() {
        this.flights = new ArrayList<>();
        initializeSampleFlights();
    }

    public FlightController(List<Flight> flights) {
        this.flights = flights;
    }

    /**
     * Initialize with sample flights
     */
    private void initializeSampleFlights() {
        flights.add(new Flight("AI101", "New York (JFK)", "Los Angeles (LAX)",
                LocalDateTime.now().plusDays(1).withHour(8).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(11).withMinute(30),
                180, 299.99, "Boeing 737"));

        flights.add(new Flight("AI202", "New York (JFK)", "London (LHR)",
                LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
                LocalDateTime.now().plusDays(2).withHour(22).withMinute(30),
                250, 599.99, "Boeing 777"));

        flights.add(new Flight("AI303", "Los Angeles (LAX)", "Tokyo (NRT)",
                LocalDateTime.now().plusDays(3).withHour(10).withMinute(0),
                LocalDateTime.now().plusDays(4).withHour(14).withMinute(0),
                300, 899.99, "Airbus A380"));

        flights.add(new Flight("AI404", "London (LHR)", "Dubai (DXB)",
                LocalDateTime.now().plusDays(1).withHour(18).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(23).withMinute(30),
                220, 449.99, "Boeing 787"));

        flights.add(new Flight("AI505", "Paris (CDG)", "Singapore (SIN)",
                LocalDateTime.now().plusDays(2).withHour(20).withMinute(0),
                LocalDateTime.now().plusDays(3).withHour(6).withMinute(0),
                280, 749.99, "Airbus A350"));
    }

    public boolean addFlight(Flight flight) {
        if (flight == null || flight.getFlightNumber() == null) {
            return false;
        }
        for (Flight f : flights) {
            if (f.getFlightNumber().equals(flight.getFlightNumber())) {
                return false;
            }
        }
        return flights.add(flight);
    }

    public boolean updateFlight(String flightNumber, Flight updatedFlight) {
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getFlightNumber().equals(flightNumber)) {
                flights.set(i, updatedFlight);
                return true;
            }
        }
        return false;
    }

    public boolean deleteFlight(String flightNumber) {
        return flights.removeIf(f -> f.getFlightNumber().equals(flightNumber));
    }

    public Flight findByFlightNumber(String flightNumber) {
        return flights.stream()
                .filter(f -> f.getFlightNumber().equals(flightNumber))
                .findFirst()
                .orElse(null);
    }

    public List<Flight> searchFlights(String origin, String destination) {
        return flights.stream()
                .filter(f -> f.getOrigin().toLowerCase().contains(origin.toLowerCase()) ||
                        f.getDestination().toLowerCase().contains(destination.toLowerCase()))
                .filter(Flight::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    public List<Flight> getAvailableFlights() {
        return flights.stream()
                .filter(Flight::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean updateAvailableSeats(String flightNumber, int seats) {
        Flight flight = findByFlightNumber(flightNumber);
        if (flight == null || flight.getAvailableSeats() < seats) {
            return false;
        }
        flight.setAvailableSeats(flight.getAvailableSeats() - seats);
        return true;
    }

    public boolean restoreSeats(String flightNumber, int seats) {
        Flight flight = findByFlightNumber(flightNumber);
        if (flight == null) {
            return false;
        }
        int newSeats = Math.min(flight.getAvailableSeats() + seats, flight.getTotalSeats());
        flight.setAvailableSeats(newSeats);
        return true;
    }
}
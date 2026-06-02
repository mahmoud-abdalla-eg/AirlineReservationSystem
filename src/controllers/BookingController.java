package controllers;

import models.Booking;
import models.Flight;
import models.Seat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for Booking operations
 */
public class BookingController {
    private List<Booking> bookings;
    private List<Seat> seats;
    private FlightController flightController;
    private int nextBookingId = 1;

    public BookingController(FlightController flightController) {
        this.bookings = new ArrayList<>();
        this.seats = new ArrayList<>();
        this.flightController = flightController;
    }

    public BookingController(List<Booking> bookings, List<Seat> seats, FlightController flightController) {
        this.bookings = bookings;
        this.seats = seats;
        this.flightController = flightController;
        this.nextBookingId = bookings.stream()
                .map(b -> Integer.parseInt(b.getBookingId().replace("BK", "")))
                .max(Integer::compare)
                .orElse(0) + 1;
    }

    /**
     * Initialize seats for a flight
     */
    public void initializeSeatsForFlight(String flightNumber, int totalSeats) {
        // Remove existing seats for this flight
        seats.removeIf(s -> s.getFlightNumber().equals(flightNumber));

        String[] columns = {"A", "B", "C", "D", "E", "F"};
        int seatsPerRow = 6;
        int rows = totalSeats / seatsPerRow;

        for (int row = 1; row <= rows; row++) {
            for (String col : columns) {
                Seat.SeatClass seatClass;
                if (row <= 3) {
                    seatClass = Seat.SeatClass.FIRST_CLASS;
                } else if (row <= 7) {
                    seatClass = Seat.SeatClass.BUSINESS;
                } else if (row <= 12) {
                    seatClass = Seat.SeatClass.PREMIUM_ECONOMY;
                } else {
                    seatClass = Seat.SeatClass.ECONOMY;
                }

                Seat seat = new Seat(row + col, flightNumber, row, col, seatClass);
                seats.add(seat);
            }
        }
    }

    /**
     * Book a seat on a flight
     */
    public Booking createBooking(String flightNumber, int passengerId, String seatId) {
        Flight flight = flightController.findByFlightNumber(flightNumber);
        if (flight == null || !flight.isAvailable()) {
            return null;
        }

        Seat seat = findSeat(flightNumber, seatId);
        if (seat == null || !seat.isAvailable()) {
            return null;
        }

        // Calculate price
        double flightPrice = flight.calculateCurrentPrice();
        double finalPrice = flightPrice + seat.getFinalPrice(flightPrice);

        // Mark seat as unavailable
        seat.setAvailable(false);

        // Create booking
        Booking booking = new Booking(
                "BK" + nextBookingId++,
                flightNumber,
                passengerId,
                seatId,
                finalPrice
        );

        // Update flight available seats
        flightController.updateAvailableSeats(flightNumber, 1);

        bookings.add(booking);
        return booking;
    }

    /**
     * Cancel a booking
     */
    public boolean cancelBooking(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId) && b.cancel()) {
                // Restore seat availability
                Seat seat = findSeat(b.getFlightNumber(), b.getSeatId());
                if (seat != null) {
                    seat.setAvailable(true);
                }
                // Restore flight seats
                flightController.restoreSeats(b.getFlightNumber(), 1);
                return true;
            }
        }
        return false;
    }

    /**
     * Check in for a booking
     */
    public boolean checkIn(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)) {
                return b.checkIn();
            }
        }
        return false;
    }

    /**
     * Board the flight
     */
    public boolean board(String bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId)) {
                return b.board();
            }
        }
        return false;
    }

    public Seat findSeat(String flightNumber, String seatId) {
        return seats.stream()
                .filter(s -> s.getFlightNumber().equals(flightNumber) && s.getSeatId().equals(seatId))
                .findFirst()
                .orElse(null);
    }

    public List<Seat> getAvailableSeats(String flightNumber) {
        return seats.stream()
                .filter(s -> s.getFlightNumber().equals(flightNumber) && s.isAvailable())
                .collect(Collectors.toList());
    }

    public List<Seat> getAllSeatsForFlight(String flightNumber) {
        return seats.stream()
                .filter(s -> s.getFlightNumber().equals(flightNumber))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByPassenger(int passengerId) {
        return bookings.stream()
                .filter(b -> b.getPassengerId() == passengerId)
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByFlight(String flightNumber) {
        return bookings.stream()
                .filter(b -> b.getFlightNumber().equals(flightNumber))
                .collect(Collectors.toList());
    }

    public List<Booking> getActiveBookings() {
        return bookings.stream()
                .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED ||
                        b.getStatus() == Booking.BookingStatus.CHECKED_IN)
                .collect(Collectors.toList());
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}
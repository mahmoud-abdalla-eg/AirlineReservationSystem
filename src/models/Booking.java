package models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a booking/reservation in the system
 */
public class Booking {
    private String bookingId;
    private String flightNumber;
    private int passengerId;
    private String seatId;
    private LocalDateTime bookingTime;
    private BookingStatus status;
    private double totalPrice;
    private String bookingReference;

    public Booking() {
    }

    public Booking(String bookingId, String flightNumber, int passengerId,
                   String seatId, double totalPrice) {
        this.bookingId = bookingId;
        this.flightNumber = flightNumber;
        this.passengerId = passengerId;
        this.seatId = seatId;
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
        this.totalPrice = totalPrice;
        this.bookingReference = generateReference();
    }

    public enum BookingStatus {
        CONFIRMED,
        CANCELLED,
        CHECKED_IN,
        BOARDED,
        COMPLETED
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    /**
     * Generate a unique booking reference (6 alphanumeric characters)
     */
    private String generateReference() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder ref = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            ref.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return ref.toString();
    }

    /**
     * Cancel this booking
     */
    public boolean cancel() {
        if (status == BookingStatus.CANCELLED) {
            return false;
        }
        this.status = BookingStatus.CANCELLED;
        return true;
    }

    /**
     * Check in for the flight
     */
    public boolean checkIn() {
        if (status != BookingStatus.CONFIRMED) {
            return false;
        }
        this.status = BookingStatus.CHECKED_IN;
        return true;
    }

    /**
     * Board the flight
     */
    public boolean board() {
        if (status != BookingStatus.CHECKED_IN) {
            return false;
        }
        this.status = BookingStatus.BOARDED;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingReference='" + bookingReference + '\'' +
                ", flight='" + flightNumber + '\'' +
                ", seat='" + seatId + '\'' +
                ", status=" + status +
                ", price=" + totalPrice +
                '}';
    }
}
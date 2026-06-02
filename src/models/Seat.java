package models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a seat on a flight
 */
public class Seat {
    private String seatId;
    private String flightNumber;
    private int row;
    private String column;
    private SeatClass seatClass;
    private boolean available;
    private double price;

    public Seat() {
    }

    public Seat(String seatId, String flightNumber, int row, String column, SeatClass seatClass) {
        this.seatId = seatId;
        this.flightNumber = flightNumber;
        this.row = row;
        this.column = column;
        this.seatClass = seatClass;
        this.available = true;
        this.price = calculateSeatPrice();
    }

    public enum SeatClass {
        ECONOMY,
        PREMIUM_ECONOMY,
        BUSINESS,
        FIRST_CLASS
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Calculate seat price based on class
     */
    private double calculateSeatPrice() {
        return switch (seatClass) {
            case FIRST_CLASS -> 500.0;
            case BUSINESS -> 300.0;
            case PREMIUM_ECONOMY -> 150.0;
            case ECONOMY -> 50.0;
        };
    }

    /**
     * Apply multiplier for premium rows (exit rows, front rows)
     */
    public double getFinalPrice(double baseFlightPrice) {
        double multiplier = 1.0;
        if (row <= 5) {
            multiplier += 0.2; // Premium front rows
        } else if (row >= 20 && row <= 25) {
            multiplier += 0.15; // Exit rows
        }
        return baseFlightPrice + (price * multiplier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return Objects.equals(seatId, seat.seatId) && Objects.equals(flightNumber, seat.flightNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatId, flightNumber);
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId='" + seatId + '\'' +
                ", flight='" + flightNumber + '\'' +
                ", class=" + seatClass +
                ", available=" + available +
                '}';
    }
}
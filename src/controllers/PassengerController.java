package controllers;

import models.Passenger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for Passenger operations
 */
public class PassengerController {
    private List<Passenger> passengers;
    private int nextPassengerId = 1;

    public PassengerController() {
        this.passengers = new ArrayList<>();
    }

    public PassengerController(List<Passenger> passengers) {
        this.passengers = passengers;
        this.nextPassengerId = passengers.stream()
                .mapToInt(Passenger::getPassengerId)
                .max()
                .orElse(0) + 1;
    }

    public boolean addPassenger(Passenger passenger) {
        if (passenger == null || passenger.getEmail() == null) {
            return false;
        }

        // Check for duplicate email or passport
        for (Passenger p : passengers) {
            if (p.getEmail().equalsIgnoreCase(passenger.getEmail()) ||
                    p.getPassportNumber().equals(passenger.getPassportNumber())) {
                return false;
            }
        }

        passenger.setPassengerId(nextPassengerId++);
        return passengers.add(passenger);
    }

    public boolean updatePassenger(int passengerId, Passenger updatedPassenger) {
        for (int i = 0; i < passengers.size(); i++) {
            if (passengers.get(i).getPassengerId() == passengerId) {
                updatedPassenger.setPassengerId(passengerId);
                passengers.set(i, updatedPassenger);
                return true;
            }
        }
        return false;
    }

    public boolean deletePassenger(int passengerId) {
        return passengers.removeIf(p -> p.getPassengerId() == passengerId);
    }

    public Passenger findById(int passengerId) {
        return passengers.stream()
                .filter(p -> p.getPassengerId() == passengerId)
                .findFirst()
                .orElse(null);
    }

    public Passenger findByPassport(String passportNumber) {
        return passengers.stream()
                .filter(p -> p.getPassportNumber().equalsIgnoreCase(passportNumber))
                .findFirst()
                .orElse(null);
    }

    public Passenger findByEmail(String email) {
        return passengers.stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<Passenger> searchByName(String name) {
        return passengers.stream()
                .filter(p -> p.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Passenger> getAllPassengers() {
        return new ArrayList<>(passengers);
    }

    public boolean validatePassenger(int passengerId) {
        Passenger p = findById(passengerId);
        return p != null && p.isActive() && p.isValidPassport();
    }
}
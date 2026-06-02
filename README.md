# SkyBook - Airline Reservation System

A comprehensive Airline Reservation System built with Java Swing GUI, designed as a course project for Java Programming at Jinan University.
Username: Admin
Pass: 123

## Project Overview

This project demonstrates core Java programming skills including:
- **Object-Oriented Programming** - Models, Controllers, Views (MVC) architecture
- **GUI Development** - Java Swing with modern UI design
- **Data Management** - Collections, business logic, and validation
- **Testing** - JUnit 5 test suites

## Features

### Desktop Application (Java Swing)
- **Flight Management** - Add, update, delete, and search flights with dynamic pricing
- **Passenger Management** - Register and manage passenger information with validation
- **Seat Booking** - Interactive seat selection with tiered pricing by cabin class
- **Booking History** - View, filter, and manage all reservations
- **Check-in & Boarding** - Process passenger check-ins and generate boarding passes

### Web Interface
- Modern, responsive landing page
- Flight search demonstration
- Features showcase
- Route information

## Technology Stack

### Desktop Application
- Java 8+
- Java Swing (GUI)
- JUnit 5 (Testing)
- MVC Architecture

### Web Interface
- Vite React
- Google Fonts (Inter)
- Responsive Design

## Project Structure

```
AirlineReservationSystem/
├── src/
│   ├── main/
│   │   └── Main.java
│   ├── models/
│   │   ├── Flight.java
│   │   ├── Passenger.java
│   │   ├── Booking.java
│   │   └── Seat.java
│   ├── views/
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── FlightFrame.java
│   │   ├── PassengerFrame.java
│   │   ├── BookingFrame.java
│   │   ├── BookingHistoryFrame.java
│   │   └── CheckInFrame.java
│   ├── controllers/
│   │   ├── FlightController.java
│   │   ├── PassengerController.java
│   │   └── BookingController.java
│   └── tests/
│       ├── FlightTest.java
│       ├── PassengerTest.java
│       ├── BookingTest.java
│       └── ControllerTest.java
└── web/
    └── Vite React
```

## How to Run

### Desktop Application

1. Compile the Java source files:
```bash
javac -d bin src/**/*.java
```

2. Run the application:
```bash
java -cp bin main.Main
```

3. Login credentials:
   - Username: `admin`
   - Password: `admin123`

### Web Interface

Open `web/index.html` in any modern web browser.

### Running Tests

```bash
java -cp bin org.junit.platform.console.ConsoleLauncher
```

## Pricing Structure

### Dynamic Flight Pricing (based on seat occupancy)
- **0-50% occupancy**: Base price
- **50-80% occupancy**: +15% surge pricing
- **80%+ occupancy**: +30% surge pricing

### Seat Class Pricing
| Class | Base Price |
|-------|-----------|
| Economy | $50 |
| Premium Economy | $150 |
| Business | $300 |
| First Class | $500 |

### Premium Row Charges
- Front rows (1-5): +20% premium
- Exit rows (20-25): +15% premium

## Sample Data

The system comes pre-loaded with sample flights:
- AI101: New York (JFK) → Los Angeles (LAX)
- AI202: New York (JFK) → London (LHR)
- AI303: Los Angeles (LAX) → Tokyo (NRT)
- AI404: London (LHR) → Dubai (DXB)
- AI505: Paris (CDG) → Singapore (SIN)

## Course Information

- **Course**: Java Programming
- **Institution**: Jinan University - International School

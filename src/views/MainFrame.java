package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main application frame using a single-window tabbed interface
 * This avoids multiple frame issues on different systems
 */
public class MainFrame extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    private JPanel dashboardPanel, flightsPanel, passengersPanel, bookingsPanel, checkinPanel;

    // Dashboard components
    private JLabel lblFlights, lblPassengers, lblBookings, lblSeats;
    private JTextArea dashboardArea;

    // Flight components
    private JTextArea flightsArea;
    private JComboBox<String> flightFilter;
    private JButton btnRefreshFlights;

    // Passenger components
    private JTextArea passengersArea;
    private JButton btnRefreshPassengers;

    // Booking components
    private JComboBox<String> bookingFlightCombo, bookingPassengerCombo;
    private JTextArea seatArea, ticketArea;
    private JButton btnBook, btnCancel;
    private String selectedSeat = null;

    // Checkin components
    private JTextArea checkinArea;
    private JButton btnCheckin, btnBoard;

    public MainFrame() {
        setTitle("Airline Reservation System - SkyBook");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Initialize all panels
        createDashboardPanel();
        createFlightsPanel();
        createPassengersPanel();
        createBookingsPanel();
        createCheckinPanel();

        // Add tabs
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Flights", flightsPanel);
        tabbedPane.addTab("Passengers", passengersPanel);
        tabbedPane.addTab("Booking", bookingsPanel);
        tabbedPane.addTab("Check-in", checkinPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Load initial data
        refreshAll();
    }

    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));

        lblFlights = new JLabel("5", SwingConstants.CENTER);
        lblFlights.setFont(new Font("Arial", Font.BOLD, 36));
        lblFlights.setForeground(new Color(0, 102, 204));
        JPanel p1 = createStatPanel("Total Flights", lblFlights);
        statsPanel.add(p1);

        lblPassengers = new JLabel("4", SwingConstants.CENTER);
        lblPassengers.setFont(new Font("Arial", Font.BOLD, 36));
        lblPassengers.setForeground(new Color(204, 102, 0));
        JPanel p2 = createStatPanel("Passengers", lblPassengers);
        statsPanel.add(p2);

        lblBookings = new JLabel("0", SwingConstants.CENTER);
        lblBookings.setFont(new Font("Arial", Font.BOLD, 36));
        lblBookings.setForeground(new Color(0, 153, 76));
        JPanel p3 = createStatPanel("Active Bookings", lblBookings);
        statsPanel.add(p3);

        lblSeats = new JLabel("890", SwingConstants.CENTER);
        lblSeats.setFont(new Font("Arial", Font.BOLD, 36));
        lblSeats.setForeground(new Color(153, 51, 153));
        JPanel p4 = createStatPanel("Available Seats", lblSeats);
        statsPanel.add(p4);

        dashboardPanel.add(statsPanel, BorderLayout.NORTH);

        dashboardArea = new JTextArea();
        dashboardArea.setEditable(false);
        dashboardArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(dashboardArea);
        dashboardPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatPanel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void createFlightsPanel() {
        flightsPanel = new JPanel(new BorderLayout(10, 10));
        flightsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter:"));
        flightFilter = new JComboBox<>(new String[]{"All Flights", "Available", "Full"});
        topPanel.add(flightFilter);
        btnRefreshFlights = new JButton("Refresh");
        btnRefreshFlights.addActionListener(this);
        topPanel.add(btnRefreshFlights);

        flightsPanel.add(topPanel, BorderLayout.NORTH);

        flightsArea = new JTextArea();
        flightsArea.setEditable(false);
        flightsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(flightsArea);
        flightsPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void createPassengersPanel() {
        passengersPanel = new JPanel(new BorderLayout(10, 10));
        passengersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Passenger Management"));
        btnRefreshPassengers = new JButton("Refresh");
        btnRefreshPassengers.addActionListener(this);
        topPanel.add(btnRefreshPassengers);

        passengersPanel.add(topPanel, BorderLayout.NORTH);

        passengersArea = new JTextArea();
        passengersArea.setEditable(false);
        passengersArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(passengersArea);
        passengersPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void createBookingsPanel() {
        bookingsPanel = new JPanel(new BorderLayout(10, 10));
        bookingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top selection panel
        JPanel selectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        selectPanel.add(new JLabel("Select Flight:"), gbc);
        gbc.gridx = 1;
        bookingFlightCombo = new JComboBox<>();
        bookingFlightCombo.setPreferredSize(new Dimension(300, 25));
        selectPanel.add(bookingFlightCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        selectPanel.add(new JLabel("Select Passenger:"), gbc);
        gbc.gridx = 1;
        bookingPassengerCombo = new JComboBox<>();
        bookingPassengerCombo.setPreferredSize(new Dimension(300, 25));
        selectPanel.add(bookingPassengerCombo, gbc);

        bookingsPanel.add(selectPanel, BorderLayout.NORTH);

        // Center - seat selection and ticket
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        seatArea = new JTextArea();
        seatArea.setEditable(false);
        seatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        splitPane.setLeftComponent(new JScrollPane(seatArea));

        ticketArea = new JTextArea();
        ticketArea.setEditable(false);
        ticketArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ticketArea.setBorder(BorderFactory.createTitledBorder("Boarding Pass"));
        splitPane.setRightComponent(new JScrollPane(ticketArea));

        splitPane.setDividerLocation(400);
        bookingsPanel.add(splitPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnBook = new JButton("Book Selected Seat");
        btnBook.setPreferredSize(new Dimension(180, 35));
        btnBook.setBackground(new Color(0, 153, 76));
        btnBook.setForeground(Color.WHITE);
        btnBook.addActionListener(this);

        btnCancel = new JButton("Cancel Booking");
        btnCancel.setBackground(new Color(204, 0, 0));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(this);

        buttonPanel.add(btnBook);
        buttonPanel.add(btnCancel);
        bookingsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add combo listener
        bookingFlightCombo.addActionListener(e -> updateSeatDisplay());
    }

    private void createCheckinPanel() {
        checkinPanel = new JPanel(new BorderLayout(10, 10));
        checkinPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Check-in & Boarding Management"));
        checkinPanel.add(topPanel, BorderLayout.NORTH);

        checkinArea = new JTextArea();
        checkinArea.setEditable(false);
        checkinArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(checkinArea);
        checkinPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnCheckin = new JButton("Check In Selected");
        btnCheckin.setBackground(new Color(0, 102, 204));
        btnCheckin.setForeground(Color.WHITE);
        btnCheckin.addActionListener(this);

        btnBoard = new JButton("Board Selected");
        btnBoard.setBackground(new Color(0, 153, 76));
        btnBoard.setForeground(Color.WHITE);
        btnBoard.addActionListener(this);

        buttonPanel.add(btnCheckin);
        buttonPanel.add(btnBoard);
        checkinPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefreshFlights) {
            refreshFlights();
        } else if (e.getSource() == btnRefreshPassengers) {
            refreshPassengers();
        } else if (e.getSource() == btnBook) {
            bookSeat();
        } else if (e.getSource() == btnCancel) {
            cancelBooking();
        } else if (e.getSource() == btnCheckin) {
            doCheckin();
        } else if (e.getSource() == btnBoard) {
            doBoard();
        }
    }

    private void refreshAll() {
        refreshDashboard();
        refreshFlights();
        refreshPassengers();
        refreshBookings();
        refreshCheckin();
    }

    private void refreshDashboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("              AIRLINE RESERVATION SYSTEM - SKYBOOK\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");
        sb.append("SYSTEM OVERVIEW\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-20s %s\n", "Total Flights:", "5"));
        sb.append(String.format("%-20s %s\n", "Total Passengers:", "4"));
        sb.append(String.format("%-20s %s\n", "Active Bookings:", "0"));
        sb.append(String.format("%-20s %s\n", "Available Seats:", "890"));
        sb.append("\nRECENT FLIGHTS\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-10s %-25s %-25s %-15s\n", "Flight", "Origin", "Destination", "Departure"));
        sb.append(String.format("%-10s %-25s %-25s %-15s\n", "AI101", "New York (JFK)", "Los Angeles (LAX)", "2026-05-31 08:00"));
        sb.append(String.format("%-10s %-25s %-25s %-15s\n", "AI202", "New York (JFK)", "London (LHR)", "2026-06-01 14:00"));
        sb.append(String.format("%-10s %-25s %-25s %-15s\n", "AI303", "Los Angeles (LAX)", "Tokyo (NRT)", "2026-06-02 10:00"));
        sb.append(String.format("%-10s %-25s %-25s %-15s\n", "AI404", "London (LHR)", "Dubai (DXB)", "2026-05-31 18:00"));
        sb.append(String.format("%-10s %-25s %-25s %-15s\n", "AI505", "Paris (CDG)", "Singapore (SIN)", "2026-06-03 20:00"));

        dashboardArea.setText(sb.toString());

        lblFlights.setText("5");
        lblPassengers.setText("4");
        lblBookings.setText("0");
        lblSeats.setText("890");
    }

    private void refreshFlights() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("                    FLIGHT MANAGEMENT\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");
        sb.append(String.format("%-10s %-20s %-20s %-15s %-12s %-10s\n", "Flight", "Origin", "Destination", "Departure", "Aircraft", "Seats"));
        sb.append("───────────────────────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-10s %-20s %-20s %-15s %-12s %-10s\n", "AI101", "New York (JFK)", "Los Angeles (LAX)", "2026-05-31 08:00", "Boeing 737", "180"));
        sb.append(String.format("%-10s %-20s %-20s %-15s %-12s %-10s\n", "AI202", "New York (JFK)", "London (LHR)", "2026-06-01 14:00", "Boeing 777", "250"));
        sb.append(String.format("%-10s %-20s %-20s %-15s %-12s %-10s\n", "AI303", "Los Angeles (LAX)", "Tokyo (NRT)", "2026-06-02 10:00", "Airbus A380", "300"));
        sb.append(String.format("%-10s %-20s %-20s %-15s %-12s %-10s\n", "AI404", "London (LHR)", "Dubai (DXB)", "2026-05-31 18:00", "Boeing 787", "220"));
        sb.append(String.format("%-10s %-20s %-20s %-15s %-12s %-10s\n", "AI505", "Paris (CDG)", "Singapore (SIN)", "2026-06-03 20:00", "Airbus A350", "280"));

        flightsArea.setText(sb.toString());
    }

    private void refreshPassengers() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("                  PASSENGER MANAGEMENT\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");
        sb.append(String.format("%-6s %-15s %-25s %-15s %-12s %-15s\n", "ID", "Name", "Email", "Phone", "Passport", "Nationality"));
        sb.append("───────────────────────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-6s %-15s %-25s %-15s %-12s %-15s\n", "1", "John Doe", "john.doe@email.com", "555-0100", "AB123456", "American"));
        sb.append(String.format("%-6s %-15s %-25s %-15s %-12s %-15s\n", "2", "Sarah Smith", "sarah.smith@email.com", "555-0200", "CD789012", "British"));
        sb.append(String.format("%-6s %-15s %-25s %-15s %-12s %-15s\n", "3", "Ahmed Hassan", "ahmed.hassan@email.com", "555-0300", "EF345678", "Egyptian"));
        sb.append(String.format("%-6s %-15s %-25s %-15s %-12s %-15s\n", "4", "Wei Chen", "wei.chen@email.com", "555-0400", "GH901234", "Chinese"));

        passengersArea.setText(sb.toString());
    }

    private void refreshBookings() {
        bookingFlightCombo.removeAllItems();
        bookingFlightCombo.addItem("AI101 - New York → Los Angeles ($299)");
        bookingFlightCombo.addItem("AI202 - New York → London ($599)");
        bookingFlightCombo.addItem("AI303 - Los Angeles → Tokyo ($899)");
        bookingFlightCombo.addItem("AI404 - London → Dubai ($449)");
        bookingFlightCombo.addItem("AI505 - Paris → Singapore ($749)");

        bookingPassengerCombo.removeAllItems();
        bookingPassengerCombo.addItem("1 - John Doe");
        bookingPassengerCombo.addItem("2 - Sarah Smith");
        bookingPassengerCombo.addItem("3 - Ahmed Hassan");
        bookingPassengerCombo.addItem("4 - Wei Chen");

        updateSeatDisplay();
    }

    private void updateSeatDisplay() {
        String selected = (String) bookingFlightCombo.getSelectedItem();
        String flight = selected != null ? selected.split(" - ")[0] : "AI101";

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("                    SEAT MAP - ").append(flight).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");
        sb.append("       A     B     C     D     E     F\n");
        sb.append("    ┌─────┬─────┬─────┬─────┬─────┬─────┐\n");

        String[] classes = {"FIRST", "FIRST", "FIRST", "BUSINESS", "BUSINESS", "BUSINESS", "BUSINESS", "BUSINESS",
                           "PREMIUM", "PREMIUM", "PREMIUM", "PREMIUM", "PREMIUM", "ECONOMY", "ECONOMY", "ECONOMY",
                           "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY",
                           "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY", "ECONOMY"};

        for (int row = 1; row <= 30; row++) {
            sb.append(String.format("  %2d │", row));
            for (char col : new char[]{'A', 'B', 'C', 'D', 'E', 'F'}) {
                String seatId = row + "" + col;
                sb.append(String.format(" %3s │", seatId));
            }
            sb.append("  ").append(classes[row - 1]);
            sb.append("\n");
            if (row < 30) {
                sb.append("    ├─────┼─────┼─────┼─────┼─────┼─────┤\n");
            }
        }
        sb.append("    └─────┴─────┴─────┴─────┴─────┴─────┘\n");

        seatArea.setText(sb.toString());
        selectedSeat = null;
    }

    private void bookSeat() {
        String selectedFlight = (String) bookingFlightCombo.getSelectedItem();
        String selectedPassenger = (String) bookingPassengerCombo.getSelectedItem();

        if (selectedFlight == null || selectedPassenger == null) {
            JOptionPane.showMessageDialog(this, "Please select flight and passenger!");
            return;
        }

        String flightInfo = selectedFlight.split(" - ")[0];
        String passengerInfo = selectedPassenger.split(" - ")[0];
        String passengerName = selectedPassenger.split(" - ")[1];

        // Generate booking reference
        String ref = generateRef();

        StringBuilder ticket = new StringBuilder();
        ticket.append("╔═══════════════════════════════════════════════════════════════╗\n");
        ticket.append("║                    BOARDING PASS                              ║\n");
        ticket.append("╠═══════════════════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║  Booking Reference: %-40s ║\n", ref));
        ticket.append("╠═══════════════════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║  Flight: %-47s ║\n", flightInfo));
        ticket.append(String.format("║  Route:  %-47s ║\n", "JFK → LAX"));
        ticket.append(String.format("║  Date:   %-47s ║\n", "2026-05-31 08:00"));
        ticket.append("╠═══════════════════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║  Passenger: %-44s ║\n", passengerName));
        ticket.append(String.format("║  Seat:   %-47s ║\n", "12A"));
        ticket.append(String.format("║  Class:  %-47s ║\n", "Economy"));
        ticket.append("╠═══════════════════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║  Total Paid: $%-44s ║\n", "349.00"));
        ticket.append("╚═══════════════════════════════════════════════════════════════╝\n");

        ticketArea.setText(ticket.toString());

        JOptionPane.showMessageDialog(this, "Booking Confirmed!\nReference: " + ref);
    }

    private String generateRef() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder ref = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            ref.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return ref.toString();
    }

    private void cancelBooking() {
        JOptionPane.showMessageDialog(this, "Cancel booking functionality");
    }

    private void refreshCheckin() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("                    CHECK-IN & BOARDING\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");
        sb.append(String.format("%-10s %-10s %-15s %-10s %-15s\n", "Ref", "Flight", "Passenger", "Seat", "Status"));
        sb.append("───────────────────────────────────────────────────────────────────────────────\n");
        sb.append("No bookings found. Create a booking first in the Booking tab.\n");

        checkinArea.setText(sb.toString());
    }

    private void doCheckin() {
        JOptionPane.showMessageDialog(this, "Check-in processed!");
    }

    private void doBoard() {
        JOptionPane.showMessageDialog(this, "Boarding completed!");
    }
}
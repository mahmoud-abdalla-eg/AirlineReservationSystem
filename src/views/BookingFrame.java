package views;

import models.*;
import controllers.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Booking frame for seat reservations
 */
public class BookingFrame extends JFrame {
    private JComboBox<String> flightComboBox;
    private JComboBox<Integer> passengerComboBox;
    private JTable seatsTable;
    private DefaultTableModel seatTableModel;
    private JTextArea ticketArea;
    private FlightController flightController;
    private PassengerController passengerController;
    private BookingController bookingController;

    public BookingFrame() {
        setTitle("Seat Booking");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        flightController = new FlightController();
        passengerController = new PassengerController();
        bookingController = new BookingController(flightController);

        initializeData();
        initializeSeats();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Seat Booking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Flight & Passenger"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        selectionPanel.add(new JLabel("Select Flight:"), gbc);
        gbc.gridx = 1;
        flightComboBox = new JComboBox<>();
        for (Flight f : flightController.getAvailableFlights()) {
            flightComboBox.addItem(f.getFlightNumber());
        }
        flightComboBox.addActionListener(e -> updateSeatDisplay());
        selectionPanel.add(flightComboBox, gbc);

        gbc.gridx = 2;
        selectionPanel.add(new JLabel("Select Passenger:"), gbc);
        gbc.gridx = 3;
        for (Passenger p : passengerController.getAllPassengers()) {
            passengerComboBox.addItem(p.getPassengerId());
        }
        selectionPanel.add(passengerComboBox, gbc);

        mainPanel.add(selectionPanel, BorderLayout.NORTH);

        String[] seatColumns = {"Seat", "Class", "Row", "Available", "Price"};
        seatTableModel = new DefaultTableModel(seatColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        seatsTable = new JTable(seatTableModel);
        seatsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seatsTable.setRowHeight(25);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(seatsTable), BorderLayout.CENTER);

        ticketArea = new JTextArea(12, 40);
        ticketArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        ticketArea.setEditable(false);
        ticketArea.setBorder(BorderFactory.createTitledBorder("Booking Ticket"));
        centerPanel.add(new JScrollPane(ticketArea), BorderLayout.EAST);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton bookBtn = new JButton("Book Selected Seat");
        JButton refreshBtn = new JButton("Refresh");
        JButton cancelBtn = new JButton("Cancel Booking");

        bookBtn.setFont(new Font("Arial", Font.BOLD, 13));
        bookBtn.setBackground(new Color(0, 153, 76));
        bookBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(new Color(204, 0, 0));
        cancelBtn.setForeground(Color.WHITE);

        buttonPanel.add(bookBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        bookBtn.addActionListener(e -> bookSeat());
        refreshBtn.addActionListener(e -> updateSeatDisplay());

        updateSeatDisplay();
        add(mainPanel);
    }

    private void initializeData() {
        models.Passenger p1 = new models.Passenger(1, "John", "Doe", "john.doe@email.com", "555-0100", "AB123456", java.time.LocalDate.of(1985, 5, 15), "American");
        models.Passenger p2 = new models.Passenger(2, "Sarah", "Smith", "sarah.smith@email.com", "555-0200", "CD789012", java.time.LocalDate.of(1990, 8, 22), "British");
        passengerController.addPassenger(p1);
        passengerController.addPassenger(p2);
    }

    private void initializeSeats() {
        for (Flight f : flightController.getAllFlights()) {
            bookingController.initializeSeatsForFlight(f.getFlightNumber(), f.getTotalSeats());
        }
    }

    private void updateSeatDisplay() {
        String flightNumber = (String) flightComboBox.getSelectedItem();
        if (flightNumber == null) return;

        seatTableModel.setRowCount(0);
        List<Seat> seats = bookingController.getAllSeatsForFlight(flightNumber);
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Seat s : seats) {
            Flight f = flightController.findByFlightNumber(flightNumber);
            double price = s.getFinalPrice(f != null ? f.calculateCurrentPrice() : 0);

            String classDisplay = switch (s.getSeatClass()) {
                case FIRST_CLASS -> "First Class";
                case BUSINESS -> "Business";
                case PREMIUM_ECONOMY -> "Premium Eco";
                case ECONOMY -> "Economy";
            };

            Color rowColor = s.isAvailable() ? new Color(200, 255, 200) : new Color(255, 200, 200);
            seatTableModel.addRow(new Object[]{
                    s.getSeatId(),
                    classDisplay,
                    s.getRow(),
                    s.isAvailable() ? "Available" : "Occupied",
                    String.format("$%.2f", price)
            });
        }
    }

    private void bookSeat() {
        int selectedRow = seatsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a seat.");
            return;
        }

        String seatStatus = (String) seatTableModel.getValueAt(selectedRow, 3);
        if (!seatStatus.equals("Available")) {
            JOptionPane.showMessageDialog(this, "This seat is already occupied.");
            return;
        }

        String flightNumber = (String) flightComboBox.getSelectedItem();
        Integer passengerId = (Integer) passengerComboBox.getSelectedItem();

        if (flightNumber == null || passengerId == null) {
            JOptionPane.showMessageDialog(this, "Please select flight and passenger.");
            return;
        }

        String seatId = (String) seatTableModel.getValueAt(selectedRow, 0);
        Booking booking = bookingController.createBooking(flightNumber, passengerId, seatId);

        if (booking != null) {
            Flight flight = flightController.findByFlightNumber(flightNumber);
            Passenger passenger = passengerController.findById(passengerId);

            String ticket = """
                ====================================
                BOARDING PASS
                ====================================
                Booking Ref: %s
                ------------------------------------
                Flight:     %s
                Route:      %s → %s
                Departure:  %s
                ------------------------------------
                Passenger:  %s
                Seat:       %s
                Class:      %s
                ------------------------------------
                Total Paid: $%.2f
                ====================================
                """.formatted(
                    booking.getBookingReference(),
                    flight.getFlightNumber(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    passenger.getFullName(),
                    seatId,
                    seatTableModel.getValueAt(selectedRow, 1),
                    booking.getTotalPrice()
            );

            ticketArea.setText(ticket);
            updateSeatDisplay();
            JOptionPane.showMessageDialog(this, "Booking confirmed!\nReference: " + booking.getBookingReference());
        } else {
            JOptionPane.showMessageDialog(this, "Booking failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
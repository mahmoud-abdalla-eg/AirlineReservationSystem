package views;

import models.Booking;
import controllers.BookingController;
import controllers.FlightController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Check-in and boarding frame
 */
public class CheckInFrame extends JFrame {
    private JTable checkInTable;
    private DefaultTableModel tableModel;
    private JTextField bookingRefField;
    private JTextArea resultArea;
    private BookingController bookingController;
    private FlightController flightController;

    public CheckInFrame() {
        setTitle("Check-in & Boarding");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        flightController = new FlightController();
        bookingController = new BookingController(flightController);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Check-in & Boarding");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(102, 102, 102));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search by Booking Reference"));
        searchPanel.add(new JLabel("Booking Reference:"));
        bookingRefField = new JTextField(12);
        bookingRefField.setFont(new Font("Arial", Font.BOLD, 14));
        JButton searchBtn = new JButton("Search");
        JButton checkInBtn = new JButton("Check In");
        JButton boardBtn = new JButton("Board");
        JButton showAllBtn = new JButton("Show All Active");

        searchPanel.add(bookingRefField);
        searchPanel.add(searchBtn);
        searchPanel.add(checkInBtn);
        searchPanel.add(boardBtn);
        searchPanel.add(showAllBtn);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Ref", "Flight", "Passenger", "Seat", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        checkInTable = new JTable(tableModel);
        checkInTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(checkInTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        resultArea = new JTextArea(8, 40);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("Result"));
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel hintLabel = new JLabel("Select a booking from the table, then click Check In or Board");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        buttonPanel.add(hintLabel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> searchBooking());
        checkInBtn.addActionListener(e -> checkIn());
        boardBtn.addActionListener(e -> board());
        showAllBtn.addActionListener(e -> showActiveBookings());

        showActiveBookings();
        add(mainPanel);
    }

    private void showActiveBookings() {
        tableModel.setRowCount(0);
        List<Booking> activeBookings = bookingController.getActiveBookings();
        for (Booking b : activeBookings) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getBookingReference(),
                    b.getFlightNumber(),
                    "P-" + b.getPassengerId(),
                    b.getSeatId(),
                    b.getStatus().toString()
            });
        }
    }

    private void searchBooking() {
        String ref = bookingRefField.getText().trim().toUpperCase();
        if (ref.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter booking reference.");
            return;
        }

        tableModel.setRowCount(0);
        for (Booking b : bookingController.getAllBookings()) {
            if (b.getBookingReference().equals(ref)) {
                tableModel.addRow(new Object[]{
                        b.getBookingId(),
                        b.getBookingReference(),
                        b.getFlightNumber(),
                        "P-" + b.getPassengerId(),
                        b.getSeatId(),
                        b.getStatus().toString()
                });
                resultArea.setText("Found: " + b.getBookingReference() + "\nStatus: " + b.getStatus());
                return;
            }
        }
        resultArea.setText("No booking found with reference: " + ref);
    }

    private void checkIn() {
        int selectedRow = checkInTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking from the table.");
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        if (bookingController.checkIn(bookingId)) {
            resultArea.setText("Check-in successful for booking: " + bookingId);
            showActiveBookings();
        } else {
            JOptionPane.showMessageDialog(this, "Check-in failed. Booking may already be checked in or cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void board() {
        int selectedRow = checkInTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a booking from the table.");
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        if (bookingController.board(bookingId)) {
            resultArea.setText("Boarding successful for booking: " + bookingId + "\nHave a pleasant flight!");
            showActiveBookings();
        } else {
            JOptionPane.showMessageDialog(this, "Boarding failed. Passenger must check in first.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
package views;

import models.Booking;
import controllers.BookingController;
import controllers.FlightController;
import controllers.PassengerController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Booking history frame
 */
public class BookingHistoryFrame extends JFrame {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private BookingController bookingController;
    private FlightController flightController;
    private PassengerController passengerController;

    public BookingHistoryFrame() {
        setTitle("Booking History");
        setSize(950, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        flightController = new FlightController();
        passengerController = new PassengerController();
        bookingController = new BookingController(flightController);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Booking History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(153, 51, 153));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Ref", "Flight", "Passenger", "Seat", "Price", "Status", "Booked At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createEtchedBorder());

        statusFilter = new JComboBox<>(new String[]{"All", "Confirmed", "Cancelled", "Checked-in", "Boarded"});
        JButton filterBtn = new JButton("Filter");
        JButton refreshBtn = new JButton("Refresh");
        JButton cancelBtn = new JButton("Cancel Selected");

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(filterBtn);
        filterPanel.add(refreshBtn);
        filterPanel.add(cancelBtn);

        mainPanel.add(filterPanel, BorderLayout.SOUTH);

        filterBtn.addActionListener(e -> filterBookings());
        refreshBtn.addActionListener(e -> refreshTable());
        cancelBtn.addActionListener(e -> cancelBooking());

        refreshTable();
        add(mainPanel);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Booking b : bookingController.getAllBookings()) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getBookingReference(),
                    b.getFlightNumber(),
                    "P-" + b.getPassengerId(),
                    b.getSeatId(),
                    String.format("$%.2f", b.getTotalPrice()),
                    b.getStatus().toString(),
                    b.getBookingTime().format(dtFormatter)
            });
        }
    }

    private void filterBookings() {
        String selected = (String) statusFilter.getSelectedItem();
        tableModel.setRowCount(0);
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Booking b : bookingController.getAllBookings()) {
            boolean add = switch (selected) {
                case "Confirmed" -> b.getStatus() == Booking.BookingStatus.CONFIRMED;
                case "Cancelled" -> b.getStatus() == Booking.BookingStatus.CANCELLED;
                case "Checked-in" -> b.getStatus() == Booking.BookingStatus.CHECKED_IN;
                case "Boarded" -> b.getStatus() == Booking.BookingStatus.BOARDED;
                default -> true;
            };

            if (add) {
                tableModel.addRow(new Object[]{
                        b.getBookingId(),
                        b.getBookingReference(),
                        b.getFlightNumber(),
                        "P-" + b.getPassengerId(),
                        b.getSeatId(),
                        String.format("$%.2f", b.getTotalPrice()),
                        b.getStatus().toString(),
                        b.getBookingTime().format(dtFormatter)
                });
            }
        }
    }

    private void cancelBooking() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookingController.cancelBooking(bookingId)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Cannot cancel this booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
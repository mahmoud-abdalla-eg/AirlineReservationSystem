package views;

import models.Flight;
import controllers.FlightController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Flight management frame
 */
public class FlightFrame extends JFrame {
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private JTextField flightNumberField, originField, destinationField, priceField, seatsField, aircraftField;
    private JSpinner departureDateSpinner, departureTimeSpinner, arrivalTimeSpinner;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn, searchBtn;
    private FlightController flightController;

    public FlightFrame() {
        setTitle("Flight Management");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        flightController = new FlightController();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Flight Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Flight No.", "Origin", "Destination", "Departure", "Arrival", "Seats", "Available", "Price", "Aircraft"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightTable = new JTable(tableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Flight Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        flightNumberField = new JTextField(15);
        formPanel.add(flightNumberField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 3;
        originField = new JTextField(15);
        formPanel.add(originField, gbc);

        gbc.gridx = 0;
        row++;
        formPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        destinationField = new JTextField(15);
        formPanel.add(destinationField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Total Seats:"), gbc);
        gbc.gridx = 3;
        seatsField = new JTextField(15);
        formPanel.add(seatsField, gbc);

        gbc.gridx = 0;
        row++;
        formPanel.add(new JLabel("Departure Date:"), gbc);
        gbc.gridx = 1;
        departureDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(departureDateSpinner, "yyyy-MM-dd");
        departureDateSpinner.setEditor(dateEditor);
        formPanel.add(departureDateSpinner, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Departure Time:"), gbc);
        gbc.gridx = 3;
        departureTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(departureTimeSpinner, "HH:mm");
        departureTimeSpinner.setEditor(timeEditor);
        formPanel.add(departureTimeSpinner, gbc);

        gbc.gridx = 0;
        row++;
        formPanel.add(new JLabel("Arrival Time:"), gbc);
        gbc.gridx = 1;
        arrivalTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor arrivalEditor = new JSpinner.DateEditor(arrivalTimeSpinner, "HH:mm");
        arrivalTimeSpinner.setEditor(arrivalEditor);
        formPanel.add(arrivalTimeSpinner, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Base Price ($):"), gbc);
        gbc.gridx = 3;
        priceField = new JTextField(15);
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        row++;
        formPanel.add(new JLabel("Aircraft Type:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        aircraftField = new JTextField(15);
        formPanel.add(aircraftField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addBtn = new JButton("Add Flight");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        row++;
        gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addFlight());
        updateBtn.addActionListener(e -> updateFlight());
        deleteBtn.addActionListener(e -> deleteFlight());
        clearBtn.addActionListener(e -> clearForm());

        flightTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = flightTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadFlightToForm(selectedRow);
                }
            }
        });

        refreshTable();
        add(mainPanel);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Flight f : flightController.getAllFlights()) {
            tableModel.addRow(new Object[]{
                    f.getFlightNumber(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureTime().format(dtFormatter),
                    f.getArrivalTime().format(dtFormatter),
                    f.getTotalSeats(),
                    f.getAvailableSeats(),
                    String.format("$%.2f", f.getBasePrice()),
                    f.getAircraftType()
            });
        }
    }

    private void addFlight() {
        if (!validateInput()) return;

        try {
            Flight flight = createFlightFromForm();
            if (flightController.addFlight(flight)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Flight number already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to update.");
            return;
        }
        if (!validateInput()) return;

        try {
            Flight flight = createFlightFromForm();
            if (flightController.updateFlight(flight.getFlightNumber(), flight)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Flight updated successfully!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete.");
            return;
        }

        String flightNumber = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete flight " + flightNumber + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            flightController.deleteFlight(flightNumber);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Flight deleted!");
        }
    }

    private void loadFlightToForm(int row) {
        Flight f = flightController.findByFlightNumber((String) tableModel.getValueAt(row, 0));
        if (f != null) {
            flightNumberField.setText(f.getFlightNumber());
            originField.setText(f.getOrigin());
            destinationField.setText(f.getDestination());
            seatsField.setText(String.valueOf(f.getTotalSeats()));
            priceField.setText(String.valueOf(f.getBasePrice()));
            aircraftField.setText(f.getAircraftType());
        }
    }

    private void clearForm() {
        flightNumberField.setText("");
        originField.setText("");
        destinationField.setText("");
        seatsField.setText("");
        priceField.setText("");
        aircraftField.setText("");
        flightTable.clearSelection();
    }

    private boolean validateInput() {
        if (flightNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter flight number.");
            return false;
        }
        if (originField.getText().trim().isEmpty() || destinationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter origin and destination.");
            return false;
        }
        try {
            Integer.parseInt(seatsField.getText().trim());
            Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid seats or price.");
            return false;
        }
        return true;
    }

    private Flight createFlightFromForm() {
        java.util.Date depDate = (java.util.Date) departureDateSpinner.getValue();
        java.util.Date depTime = (java.util.Date) departureTimeSpinner.getValue();
        java.util.Date arrTime = (java.util.Date) arrivalTimeSpinner.getValue();

        LocalDateTime departure = LocalDateTime.of(
                depDate.getYear() + 1900, depDate.getMonth() + 1, depDate.getDate(),
                depTime.getHours(), depTime.getMinutes()
        );
        LocalDateTime arrival = LocalDateTime.of(
                depDate.getYear() + 1900, depDate.getMonth() + 1, depDate.getDate(),
                arrTime.getHours(), arrTime.getMinutes()
        );
        if (arrival.isBefore(departure)) {
            arrival = arrival.plusDays(1);
        }

        return new Flight(
                flightNumberField.getText().trim().toUpperCase(),
                originField.getText().trim(),
                destinationField.getText().trim(),
                departure, arrival,
                Integer.parseInt(seatsField.getText().trim()),
                Double.parseDouble(priceField.getText().trim()),
                aircraftField.getText().trim()
        );
    }
}
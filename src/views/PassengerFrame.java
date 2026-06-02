package views;

import models.Passenger;
import controllers.PassengerController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Passenger management frame
 */
public class PassengerFrame extends JFrame {
    private JTable passengerTable;
    private DefaultTableModel tableModel;
    private JTextField firstNameField, lastNameField, emailField, phoneField, passportField, nationalityField;
    private JSpinner dobSpinner;
    private JButton addBtn, updateBtn, deleteBtn, clearBtn, searchBtn;
    private PassengerController passengerController;

    public PassengerFrame() {
        setTitle("Passenger Management");
        setSize(950, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        passengerController = new PassengerController();
        addSamplePassengers();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Passenger Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(204, 102, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Phone", "Passport", "DOB", "Nationality", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passengerTable = new JTable(tableModel);
        passengerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(passengerTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Passenger Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(15);
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3;
        lastNameField = new JTextField(15);
        formPanel.add(lastNameField, gbc);

        gbc.gridx = 0;
        row++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 3;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        row++;
        formPanel.add(new JLabel("Passport No.:"), gbc);
        gbc.gridx = 1;
        passportField = new JTextField(15);
        formPanel.add(passportField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 3;
        dobSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dobEditor = new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd");
        dobSpinner.setEditor(dobEditor);
        dobSpinner.setValue(new Date(1990 - 1900, 0, 1));
        formPanel.add(dobSpinner, gbc);

        gbc.gridx = 0;
        row++;
        formPanel.add(new JLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        nationalityField = new JTextField(15);
        formPanel.add(nationalityField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addBtn = new JButton("Add Passenger");
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

        addBtn.addActionListener(e -> addPassenger());
        updateBtn.addActionListener(e -> updatePassenger());
        deleteBtn.addActionListener(e -> deletePassenger());
        clearBtn.addActionListener(e -> clearForm());

        passengerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = passengerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadPassengerToForm(selectedRow);
                }
            }
        });

        refreshTable();
        add(mainPanel);
    }

    private void addSamplePassengers() {
        passengerController.addPassenger(new Passenger(1, "John", "Doe", "john.doe@email.com", "555-0100", "AB123456", LocalDate.of(1985, 5, 15), "American"));
        passengerController.addPassenger(new Passenger(2, "Sarah", "Smith", "sarah.smith@email.com", "555-0200", "CD789012", LocalDate.of(1990, 8, 22), "British"));
        passengerController.addPassenger(new Passenger(3, "Ahmed", "Hassan", "ahmed.hassan@email.com", "555-0300", "EF345678", LocalDate.of(1988, 3, 10), "Egyptian"));
        passengerController.addPassenger(new Passenger(4, "Wei", "Chen", "wei.chen@email.com", "555-0400", "GH901234", LocalDate.of(1995, 12, 5), "Chinese"));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Passenger p : passengerController.getAllPassengers()) {
            tableModel.addRow(new Object[]{
                    p.getPassengerId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getEmail(),
                    p.getPhone(),
                    p.getPassportNumber(),
                    p.getDateOfBirth(),
                    p.getNationality(),
                    p.isActive() ? "Active" : "Inactive"
            });
        }
    }

    private void addPassenger() {
        if (!validateInput()) return;

        try {
            java.util.Date dob = (java.util.Date) dobSpinner.getValue();
            LocalDate dateOfBirth = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Passenger passenger = new Passenger(
                    0,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    passportField.getText().trim().toUpperCase(),
                    dateOfBirth,
                    nationalityField.getText().trim()
            );

            if (passengerController.addPassenger(passenger)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Passenger added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Email or passport already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePassenger() {
        int selectedRow = passengerTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to update.");
            return;
        }
        if (!validateInput()) return;

        try {
            int passengerId = (int) tableModel.getValueAt(selectedRow, 0);
            java.util.Date dob = (java.util.Date) dobSpinner.getValue();
            LocalDate dateOfBirth = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Passenger passenger = new Passenger(
                    passengerId,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    passportField.getText().trim().toUpperCase(),
                    dateOfBirth,
                    nationalityField.getText().trim()
            );

            if (passengerController.updatePassenger(passengerId, passenger)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Passenger updated successfully!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePassenger() {
        int selectedRow = passengerTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a passenger to delete.");
            return;
        }

        int passengerId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this passenger?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            passengerController.deletePassenger(passengerId);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Passenger deleted!");
        }
    }

    private void loadPassengerToForm(int row) {
        int passengerId = (int) tableModel.getValueAt(row, 0);
        Passenger p = passengerController.findById(passengerId);
        if (p != null) {
            firstNameField.setText(p.getFirstName());
            lastNameField.setText(p.getLastName());
            emailField.setText(p.getEmail());
            phoneField.setText(p.getPhone());
            passportField.setText(p.getPassportNumber());
            nationalityField.setText(p.getNationality());
            dobSpinner.setValue(java.sql.Date.valueOf(p.getDateOfBirth()));
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passportField.setText("");
        nationalityField.setText("");
        passengerTable.clearSelection();
    }

    private boolean validateInput() {
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter first and last name.");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter email.");
            return false;
        }
        if (passportField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter passport number.");
            return false;
        }
        return true;
    }
}
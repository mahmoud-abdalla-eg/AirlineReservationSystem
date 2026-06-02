package main;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Airline Reservation System - Fully Functional GUI
 * With Local File Persistence
 */
public class Main extends JFrame {

    // Color scheme
    private static final Color PRIMARY = new Color(0, 102, 204);
    private static final Color SECONDARY = new Color(26, 26, 46);
    private static final Color ACCENT = new Color(255, 107, 53);
    private static final Color SUCCESS = new Color(0, 200, 83);
    private static final Color LIGHT_BG = new Color(245, 247, 250);
    private static final Color CARD_BG = Color.WHITE;

    // Data
    private List<Flight> flights = new ArrayList<>();
    private List<Passenger> passengers = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private Map<String, String> bookedSeats = new HashMap<>();

    // UI Components
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPasswordField loginPassword;

    // Booking components
    private JComboBox<String> flightCombo, passengerCombo;
    private JButton changeBtn;
    private JTextArea ticketArea;
    private String selectedSeat = null;
    private JList<String> bookingList, checkinList;
    private DefaultListModel<String> bookingListModel = new DefaultListModel<>();
    private DefaultListModel<String> checkinListModel = new DefaultListModel<>();

    // Seat grid panel - instance variable so we can rebuild it
    private JPanel seatGridPanel;

    public Main() {
        setTitle("SkyBook - Airline Reservation System");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveToFile();
                System.exit(0);
            }
        });
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        initData();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createMainPanel(), "MAIN");

        add(cardPanel);
        cardLayout.show(cardPanel, "LOGIN");
    }

    private static final String DATA_FILE = "airline_data.txt";

    private void initData() {
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            flights.add(new Flight("AI101", "New York (JFK)", "Los Angeles (LAX)", "2026-05-31 08:00", "Boeing 737", 180, 299));
            flights.add(new Flight("AI202", "New York (JFK)", "London (LHR)", "2026-06-01 14:00", "Boeing 777", 250, 599));
            flights.add(new Flight("AI303", "Los Angeles (LAX)", "Tokyo (NRT)", "2026-06-02 10:00", "Airbus A380", 300, 899));
            flights.add(new Flight("AI404", "London (LHR)", "Dubai (DXB)", "2026-05-31 18:00", "Boeing 787", 220, 449));
            flights.add(new Flight("AI505", "Paris (CDG)", "Singapore (SIN)", "2026-06-03 20:00", "Airbus A350", 280, 749));

            passengers.add(new Passenger(1, "John Doe", "john.doe@email.com", "555-0100", "AB123456", "American"));
            passengers.add(new Passenger(2, "Sarah Smith", "sarah.smith@email.com", "555-0200", "CD789012", "British"));
            passengers.add(new Passenger(3, "Ahmed Hassan", "ahmed.hassan@email.com", "555-0300", "EF345678", "Egyptian"));
            passengers.add(new Passenger(4, "Wei Chen", "wei.chen@email.com", "555-0400", "GH901234", "Chinese"));
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int section = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.equals("[FLIGHTS]")) { section = 0; continue; }
                if (line.equals("[PASSENGERS]")) { section = 1; continue; }
                if (line.equals("[BOOKINGS]")) { section = 2; continue; }
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (section == 0 && line.startsWith("FLIGHT|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 8) {
                        flights.add(new Flight(parts[1], parts[2], parts[3], parts[4], parts[5],
                            Integer.parseInt(parts[6]), Double.parseDouble(parts[7])));
                    }
                } else if (section == 1 && line.startsWith("PASSENGER|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 7) {
                        passengers.add(new Passenger(Integer.parseInt(parts[1]), parts[2], parts[3],
                            parts[4], parts[5], parts[6]));
                    }
                } else if (section == 2 && line.startsWith("BOOKING|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        Booking b = new Booking(parts[1], parts[2], Integer.parseInt(parts[3]),
                            parts[4], parts[5], parts[6]);
                        bookings.add(b);
                        bookedSeats.put(parts[2] + "_" + parts[5], parts[1]);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            flights.add(new Flight("AI101", "New York (JFK)", "Los Angeles (LAX)", "2026-05-31 08:00", "Boeing 737", 180, 299));
            flights.add(new Flight("AI202", "New York (JFK)", "London (LHR)", "2026-06-01 14:00", "Boeing 777", 250, 599));
            flights.add(new Flight("AI303", "Los Angeles (LAX)", "Tokyo (NRT)", "2026-06-02 10:00", "Airbus A380", 300, 899));
            flights.add(new Flight("AI404", "London (LHR)", "Dubai (DXB)", "2026-05-31 18:00", "Boeing 787", 220, 449));
            flights.add(new Flight("AI505", "Paris (CDG)", "Singapore (SIN)", "2026-06-03 20:00", "Airbus A350", 280, 749));
            passengers.add(new Passenger(1, "John Doe", "john.doe@email.com", "555-0100", "AB123456", "American"));
            passengers.add(new Passenger(2, "Sarah Smith", "sarah.smith@email.com", "555-0200", "CD789012", "British"));
            passengers.add(new Passenger(3, "Ahmed Hassan", "ahmed.hassan@email.com", "555-0300", "EF345678", "Egyptian"));
            passengers.add(new Passenger(4, "Wei Chen", "wei.chen@email.com", "555-0400", "GH901234", "Chinese"));
        }
    }

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            writer.println("# Airline Reservation System Data");
            writer.println("# Auto-generated - Do not edit manually");
            writer.println();
            writer.println("[FLIGHTS]");
            for (Flight f : flights) {
                writer.println("FLIGHT|" + f.flightNumber + "|" + f.origin + "|" + f.destination + "|" +
                    f.departure + "|" + f.aircraft + "|" + f.totalSeats + "|" + f.basePrice);
            }
            writer.println();
            writer.println("[PASSENGERS]");
            for (Passenger p : passengers) {
                writer.println("PASSENGER|" + p.id + "|" + p.name + "|" + p.email + "|" +
                    p.phone + "|" + p.passport + "|" + p.nationality);
            }
            writer.println();
            writer.println("[BOOKINGS]");
            for (Booking b : bookings) {
                writer.println("BOOKING|" + b.ref + "|" + b.flightNumber + "|" + b.passengerId + "|" +
                    b.passengerName + "|" + b.seat + "|" + b.status);
            }
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    // ============== LOGIN ==============

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(26, 26, 46));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel logoLabel = new JLabel("✈");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        logoLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(logoLabel, gbc);

        JLabel titleLabel = new JLabel("SkyBook");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Airline Reservation System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        gbc.gridy = 2;
        panel.add(subtitleLabel, gbc);

        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(CARD_BG);
        loginCard.setBorder(new CompoundBorder(
            new LineBorder(Color.WHITE, 1, true),
            new EmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel signInLabel = new JLabel("Welcome Back");
        signInLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signInLabel.setForeground(SECONDARY);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        loginCard.add(signInLabel, gc);

        JLabel hintLabel = new JLabel("Sign in to access your account");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        gc.gridy = 1;
        loginCard.add(hintLabel, gc);

        JTextField loginUsername = new JTextField(18);
        loginUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginUsername.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(12, 15, 12, 15)));

        JPanel userPanel = new JPanel();
        userPanel.setBackground(CARD_BG);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(5));
        loginUsername.setMaximumSize(new Dimension(280, 40));
        loginUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(loginUsername);
        gc.gridy = 2;
        gc.gridwidth = 2;
        loginCard.add(userPanel, gc);

        JPanel passPanel = new JPanel();
        passPanel.setBackground(CARD_BG);
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(SECONDARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passPanel.add(passLabel);
        passPanel.add(Box.createVerticalStrut(5));
        loginPassword = new JPasswordField(18);
        loginPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginPassword.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(12, 15, 12, 15)));
        loginPassword.setMaximumSize(new Dimension(280, 40));
        loginPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPassword.addActionListener(e -> {
            String user = loginUsername.getText();
            String pass = new String(loginPassword.getPassword());
            if (user.equals("admin") && pass.equals("123")) {
                cardLayout.show(cardPanel, "MAIN");
            } else {
                JOptionPane.showMessageDialog(loginCard, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        passPanel.add(loginPassword);
        gc.gridy = 3;
        loginCard.add(passPanel, gc);

        gc.gridy = 4;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(280, 45));
        loginBtn.setBackground(PRIMARY);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> {
            String user = loginUsername.getText();
            String pass = new String(loginPassword.getPassword());
            if (user.equals("admin") && pass.equals("123")) {
                cardLayout.show(cardPanel, "MAIN");
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginCard.add(loginBtn, gc);

        gbc.gridy = 3;
        panel.add(loginCard, gbc);

        return panel;
    }

    // ============== MAIN PANEL ==============

    private JTabbedPane mainTabs;

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(LIGHT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SECONDARY);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerLeft.setBackground(SECONDARY);
        JLabel logoLabel = new JLabel("✈ SkyBook");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(Color.WHITE);
        headerLeft.add(logoLabel);
        JLabel subtitleLabel = new JLabel("Airline Reservation System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        headerLeft.add(Box.createHorizontalStrut(15));
        headerLeft.add(subtitleLabel);
        header.add(headerLeft, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        header.add(logoutBtn, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        mainTabs = new JTabbedPane();
        mainTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainTabs.addTab("📊 Dashboard", createDashboardPanel());
        mainTabs.addTab("✈ Flights", createFlightsPanel());
        mainTabs.addTab("👥 Passengers", createPassengersPanel());
        mainTabs.addTab("🎫 Booking", createBookingPanel());
        mainTabs.addTab("✅ Check-in", createCheckinPanel());

        mainPanel.add(mainTabs, BorderLayout.CENTER);

        return mainPanel;
    }

    private void refreshDashboard() {
        if (mainTabs == null) return;
        Component dash = mainTabs.getComponentAt(0);
        if (dash != null) {
            mainTabs.setComponentAt(0, createDashboardPanel());
        }
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        statsPanel.add(createStatCard("Total Flights", String.valueOf(flights.size()), PRIMARY, "✈"));
        statsPanel.add(createStatCard("Passengers", String.valueOf(passengers.size()), ACCENT, "👥"));
        statsPanel.add(createStatCard("Bookings", String.valueOf(bookings.size()), SUCCESS, "🎫"));
        int bookedSeatCount = bookedSeats.size();
        int totalSeats = 180 * 5; // Just for display
        statsPanel.add(createStatCard("Available Seats", String.valueOf(890 - bookedSeatCount), new Color(153, 51, 153), "💺"));
        panel.add(statsPanel, BorderLayout.NORTH);

        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setBackground(CARD_BG);
        infoCard.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(20, 20, 20, 20)));

        JLabel infoTitle = new JLabel("Recent Bookings");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoTitle.setForeground(SECONDARY);
        infoCard.add(infoTitle, BorderLayout.NORTH);

        JTextArea recentArea = new JTextArea();
        recentArea.setEditable(false);
        recentArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        if (bookings.isEmpty()) {
            recentArea.setText("\nNo bookings yet. Go to 'Booking' tab to create your first booking.\n");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-10s %-15s %-10s %-10s %-12s\n", "REF", "PASSENGER", "FLIGHT", "SEAT", "STATUS"));
            sb.append("────────────────────────────────────────────────────────────\n");
            for (Booking b : bookings) {
                sb.append(String.format("%-10s %-15s %-10s %-10s %-12s\n",
                    b.ref, b.passengerName, b.flightNumber, b.seat, b.status));
            }
            recentArea.setText(sb.toString());
        }
        infoCard.add(new JScrollPane(recentArea), BorderLayout.CENTER);

        panel.add(infoCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(20, 20, 20, 20)));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setBackground(CARD_BG);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        topPanel.add(iconLabel);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        topPanel.add(titleLabel);
        card.add(topPanel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // ============== FLIGHTS ==============
    private JTable flightsTable;

    private JPanel createFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Flight Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(SECONDARY);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(LIGHT_BG);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));

        refreshFlightsTable();
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add/Remove form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(15, 15, 15, 15)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fnField = new JTextField(10);
        JTextField originField = new JTextField(12);
        JTextField destField = new JTextField(12);
        JTextField depField = new JTextField(12);
        JTextField aircraftField = new JTextField(10);
        JTextField seatsField = new JTextField(5);
        JTextField priceField = new JTextField(5);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Flight #:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fnField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 3;
        formPanel.add(originField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        formPanel.add(destField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Departure:"), gbc);
        gbc.gridx = 3;
        formPanel.add(depField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Aircraft:"), gbc);
        gbc.gridx = 1;
        formPanel.add(aircraftField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Seats:"), gbc);
        gbc.gridx = 3;
        formPanel.add(seatsField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        JButton addBtn = new JButton("Add Flight");
        addBtn.setBackground(SUCCESS);
        addBtn.setForeground(Color.WHITE);
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        addBtn.addActionListener(e -> {
            try {
                if (fnField.getText().isEmpty() || originField.getText().isEmpty() || destField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Fill all required fields!"); return;
                }
                flights.add(new Flight(fnField.getText(), originField.getText(), destField.getText(),
                    depField.getText(), aircraftField.getText(),
                    Integer.parseInt(seatsField.getText()), Double.parseDouble(priceField.getText())));
                refreshFlightsTable();
                refreshDashboard();
                saveToFile();
                JOptionPane.showMessageDialog(panel, "Flight added!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid seats or price!");
            }
        });

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setBackground(new Color(220, 53, 69));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setOpaque(true);
        removeBtn.setBorderPainted(false);
        removeBtn.addActionListener(e -> {
            int row = flightsTable.getSelectedRow();
            if (row >= 0 && row < flights.size()) {
                flights.remove(row);
                refreshFlightsTable();
                refreshDashboard();
                saveToFile();
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void refreshFlightsTable() {
        String[][] data = new String[flights.size()][7];
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            data[i] = new String[]{f.flightNumber, f.origin, f.destination, f.departure, f.aircraft, String.valueOf(f.totalSeats), "$" + (int)f.basePrice};
        }
        flightsTable = new JTable(data, new String[]{"Flight", "Origin", "Destination", "Departure", "Aircraft", "Seats", "Price"});
        flightsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flightsTable.setRowHeight(35);
        flightsTable.setGridColor(new Color(240, 240, 240));
        flightsTable.setSelectionBackground(PRIMARY);
        flightsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        flightsTable.getTableHeader().setBackground(SECONDARY);
        flightsTable.getTableHeader().setForeground(Color.WHITE);
        flightsTable.setFillsViewportHeight(true);
    }

    // ============== PASSENGERS ==============
    private JTable passengersTable;
    private String passengerFlightFilter = null;

    private JPanel createPassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Passenger Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(SECONDARY);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(LIGHT_BG);
        titlePanel.add(titleLabel);

        JComboBox<String> flightFilterCombo = new JComboBox<>();
        flightFilterCombo.addItem("All Flights");
        for (Flight f : flights) {
            flightFilterCombo.addItem(f.flightNumber + " - " + f.origin + " → " + f.destination);
        }
        flightFilterCombo.addActionListener(e -> {
            String sel = (String) flightFilterCombo.getSelectedItem();
            if (sel != null && sel.equals("All Flights")) {
                passengerFlightFilter = null;
            } else if (sel != null) {
                passengerFlightFilter = sel.split(" - ")[0];
            }
            refreshPassengersTable();
        });

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(LIGHT_BG);
        filterPanel.add(new JLabel("Filter by Flight:"));
        filterPanel.add(flightFilterCombo);
        titlePanel.add(Box.createHorizontalStrut(20));
        titlePanel.add(filterPanel);

        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));

        refreshPassengersTable();
        JScrollPane scrollPane = new JScrollPane(passengersTable);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add/Remove form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(15, 15, 15, 15)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(10);
        JTextField passportField = new JTextField(10);
        JTextField nationField = new JTextField(10);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        formPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Passport:"), gbc);
        gbc.gridx = 3;
        formPanel.add(passportField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Nationality:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nationField, gbc);

        int nextId = passengers.isEmpty() ? 1 : passengers.stream().mapToInt(p -> p.id).max().orElse(0) + 1;
        JLabel idLabel = new JLabel("ID: " + nextId);

        JButton addBtn = new JButton("Add Passenger");
        addBtn.setBackground(SUCCESS);
        addBtn.setForeground(Color.WHITE);
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        addBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Name and Email required!"); return;
            }
            int newId = passengers.stream().mapToInt(p -> p.id).max().orElse(0) + 1;
            passengers.add(new Passenger(newId, nameField.getText(), emailField.getText(),
                phoneField.getText(), passportField.getText(), nationField.getText()));
            refreshPassengersTable();
            refreshDashboard();
            saveToFile();
            JOptionPane.showMessageDialog(panel, "Passenger added with ID: " + newId);
        });

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setBackground(new Color(220, 53, 69));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setOpaque(true);
        removeBtn.setBorderPainted(false);
        removeBtn.addActionListener(e -> {
            int row = passengersTable.getSelectedRow();
            if (row >= 0 && row < passengers.size()) {
                passengers.remove(row);
                refreshPassengersTable();
                refreshDashboard();
                saveToFile();
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.add(idLabel);
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        contentPanel.add(formPanel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void refreshPassengersTable() {
        java.util.List<Passenger> filtered = new java.util.ArrayList<>();
        for (Passenger p : passengers) {
            if (passengerFlightFilter == null) {
                filtered.add(p);
            } else {
                boolean hasBookingOnFlight = bookings.stream().anyMatch(b -> b.passengerId == p.id && b.flightNumber.equals(passengerFlightFilter) && !b.status.equals("Cancelled"));
                if (hasBookingOnFlight) filtered.add(p);
            }
        }

        String[][] data = new String[filtered.size()][7];
        for (int i = 0; i < filtered.size(); i++) {
            Passenger p = filtered.get(i);
            String status = "No Booking";
            if (passengerFlightFilter != null) {
                for (Booking b : bookings) {
                    if (b.passengerId == p.id && b.flightNumber.equals(passengerFlightFilter)) {
                        status = b.status;
                        break;
                    }
                }
            }
            data[i] = new String[]{String.valueOf(p.id), p.name, p.email, p.phone, p.passport, p.nationality, status};
        }
        passengersTable = new JTable(data, new String[]{"ID", "Name", "Email", "Phone", "Passport", "Nationality", "Status"});
        passengersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passengersTable.setRowHeight(35);
        passengersTable.setGridColor(new Color(240, 240, 240));
        passengersTable.setSelectionBackground(PRIMARY);
        passengersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        passengersTable.getTableHeader().setBackground(SECONDARY);
        passengersTable.getTableHeader().setForeground(Color.WHITE);
        passengersTable.setFillsViewportHeight(true);
    }

    private void refreshFlightCombo() {
        flightCombo.removeAllItems();
        for (Flight f : flights) {
            flightCombo.addItem(f.flightNumber + " - " + f.origin + " → " + f.destination + " ($" + (int)f.basePrice + ")");
        }
    }

    private void refreshPassengerCombo() {
        passengerCombo.removeAllItems();
        for (Passenger p : passengers) {
            boolean hasActiveBooking = bookings.stream().anyMatch(b -> b.passengerId == p.id && !b.status.equals("Cancelled"));
            if (!hasActiveBooking) {
                passengerCombo.addItem(p.id + " - " + p.name + " (" + p.passport + ")");
            }
        }
    }

    // ============== BOOKING ==============

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Selection panel
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(CARD_BG);
        selectionPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel flightLabel = new JLabel("Select Flight");
        flightLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        flightLabel.setForeground(SECONDARY);
        selectionPanel.add(flightLabel, gbc);

        gbc.gridx = 1;
        flightCombo = new JComboBox<>();
        flightCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshFlightCombo();
        flightCombo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        selectionPanel.add(flightCombo, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel passLabel = new JLabel("Select Passenger");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(SECONDARY);
        selectionPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        passengerCombo = new JComboBox<>();
        passengerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshPassengerCombo();
        passengerCombo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        selectionPanel.add(passengerCombo, gbc);

        panel.add(selectionPanel, BorderLayout.NORTH);

        // Content split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(10);

        // Seat selection panel
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setBackground(CARD_BG);
        seatPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel seatTitle = new JLabel("Select Your Seat");
        seatTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        seatTitle.setForeground(SECONDARY);
        seatPanel.add(seatTitle, BorderLayout.NORTH);

        // Seat grid - use instance variable
        seatGridPanel = new JPanel();
        seatGridPanel.setBackground(CARD_BG);
        seatGridPanel.setLayout(new BoxLayout(seatGridPanel, BoxLayout.Y_AXIS));
        seatGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        buildSeatGrid();

        JScrollPane seatScroll = new JScrollPane(seatGridPanel);
        seatScroll.getViewport().setBackground(CARD_BG);
        seatPanel.add(seatScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(seatPanel);

        // Ticket panel
        JPanel ticketPanel = new JPanel(new BorderLayout());
        ticketPanel.setBackground(CARD_BG);
        ticketPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel ticketTitle = new JLabel("Boarding Pass Preview");
        ticketTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        ticketTitle.setForeground(SECONDARY);
        ticketPanel.add(ticketTitle, BorderLayout.NORTH);

        ticketArea = new JTextArea();
        ticketArea.setEditable(false);
        ticketArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        ticketArea.setBackground(new Color(250, 250, 250));
        ticketArea.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY, 2, true),
            new EmptyBorder(10, 10, 10, 10)));
        ticketPanel.add(ticketArea, BorderLayout.CENTER);

        splitPane.setRightComponent(ticketPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(CARD_BG);

        JButton bookBtn = new JButton("Book Now");
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookBtn.setPreferredSize(new Dimension(180, 45));
        bookBtn.setBackground(SUCCESS);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setBorderPainted(false);
        bookBtn.setOpaque(true);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> confirmBooking());
        buttonPanel.add(bookBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.setPreferredSize(new Dimension(120, 45));
        clearBtn.setBackground(new Color(150, 150, 150));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            selectedSeat = null;
            ticketArea.setText("");
            buildSeatGrid();
        });
        buttonPanel.add(clearBtn);

        changeBtn = new JButton("Change Passenger");
        changeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        changeBtn.setPreferredSize(new Dimension(140, 45));
        changeBtn.setBackground(new Color(255, 140, 0));
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFocusPainted(false);
        changeBtn.setBorderPainted(false);
        changeBtn.setOpaque(true);
        changeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeBtn.setVisible(false);
        changeBtn.addActionListener(e -> {
            passengerCombo.setEnabled(true);
            refreshPassengerCombo();
            if (passengerCombo.getItemCount() > 0) {
                passengerCombo.setSelectedIndex(0);
            }
            changeBtn.setVisible(false);
        });
        buttonPanel.add(changeBtn);

        JButton removeAllBtn = new JButton("Remove All Bookings");
        removeAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeAllBtn.setPreferredSize(new Dimension(160, 45));
        removeAllBtn.setBackground(new Color(220, 53, 69));
        removeAllBtn.setForeground(Color.WHITE);
        removeAllBtn.setFocusPainted(false);
        removeAllBtn.setBorderPainted(false);
        removeAllBtn.setOpaque(true);
        removeAllBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeAllBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(panel, "Remove ALL bookings? This cannot be undone.", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                bookings.clear();
                bookedSeats.clear();
                saveToFile();
                refreshDashboard();
                selectedSeat = null;
                ticketArea.setText("");
                buildSeatGrid();
                JOptionPane.showMessageDialog(panel, "All bookings have been removed.");
            }
        });
        buttonPanel.add(removeAllBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void buildSeatGrid() {
        seatGridPanel.removeAll();

        // Column headers
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 5));
        headerRow.setBackground(CARD_BG);
        headerRow.add(Box.createHorizontalStrut(30));
        for (char col : "ABCDEF".toCharArray()) {
            JLabel colLabel = new JLabel("<html><b>" + col + "</b></html>");
            colLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            colLabel.setForeground(PRIMARY);
            colLabel.setPreferredSize(new Dimension(50, 25));
            headerRow.add(colLabel);
        }
        seatGridPanel.add(headerRow);

        // Seat colors
        Color firstClassColor = new Color(255, 215, 0);
        Color businessColor = new Color(100, 149, 237);
        Color premiumColor = new Color(144, 238, 144);
        Color economyColor = new Color(220, 220, 220);

        final String currentFlight;
        if (flightCombo != null && flightCombo.getSelectedItem() != null) {
            currentFlight = ((String) flightCombo.getSelectedItem()).split(" - ")[0];
        } else {
            currentFlight = "AI101";
        }

        for (int row = 1; row <= 30; row++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 35, 3));
            rowPanel.setBackground(CARD_BG);

            JLabel rowLabel = new JLabel(String.valueOf(row));
            rowLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rowLabel.setForeground(SECONDARY);
            rowLabel.setPreferredSize(new Dimension(30, 30));
            rowPanel.add(rowLabel);

            for (char col : "ABCDEF".toCharArray()) {
                String seatId = row + "" + col;
                String flightSeatKey = currentFlight + "_" + seatId;
                boolean isBooked = bookedSeats.containsKey(flightSeatKey);

                JButton seatBtn = new JButton(seatId);
                seatBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
                seatBtn.setPreferredSize(new Dimension(50, 30));
                seatBtn.setFocusPainted(false);
                seatBtn.setBorderPainted(false);
                seatBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                Color bgColor;
                if (isBooked) {
                    bgColor = new Color(180, 180, 180);
                    seatBtn.setEnabled(false);
                } else {
                    bgColor = (row <= 3) ? firstClassColor : (row <= 7) ? businessColor : (row <= 12) ? premiumColor : economyColor;
                }
                seatBtn.setBackground(bgColor);
                seatBtn.setForeground(isBooked ? Color.WHITE : Color.BLACK);
                seatBtn.setOpaque(true);

                final String seat = seatId;
                if (!isBooked) {
                    seatBtn.addActionListener(e -> {
                        selectedSeat = seat;
                        buildSeatGrid(); // Rebuild to reset colors
                        // Re-apply selected color - find the button and highlight it
                        for (Component c : seatGridPanel.getComponents()) {
                            if (c instanceof JPanel) {
                                for (Component btn : ((JPanel) c).getComponents()) {
                                    if (btn instanceof JButton && ((JButton) btn).getText().equals(seat)) {
                                        btn.setBackground(new Color(255, 80, 80));
                                    }
                                }
                            }
                        }
                        ticketArea.setText("Seat " + seat + " selected on flight " + currentFlight + ".\nClick 'Book Now' to confirm.");
                    });
                }

                rowPanel.add(seatBtn);
            }
            seatGridPanel.add(rowPanel);
        }

        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legendPanel.setBackground(CARD_BG);
        legendPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        legendPanel.add(createLegendItem("First", firstClassColor));
        legendPanel.add(createLegendItem("Business", businessColor));
        legendPanel.add(createLegendItem("Premium", premiumColor));
        legendPanel.add(createLegendItem("Economy", economyColor));
        legendPanel.add(createLegendItem("Booked", new Color(180, 180, 180)));
        seatGridPanel.add(legendPanel);

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(CARD_BG);
        JButton colorBox = new JButton();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 15));
        colorBox.setBorderPainted(false);
        colorBox.setOpaque(true);
        colorBox.setEnabled(false);
        panel.add(colorBox);
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        textLabel.setForeground(Color.GRAY);
        panel.add(textLabel);
        return panel;
    }

    private void confirmBooking() {
        String flightStr = (String) flightCombo.getSelectedItem();
        String passStr = (String) passengerCombo.getSelectedItem();

        if (flightStr == null || passStr == null) {
            JOptionPane.showMessageDialog(this, "Please select flight and passenger!");
            return;
        }

        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this, "Please select a seat first!");
            return;
        }

        String flightNum = flightStr.split(" - ")[0];
        int passId = Integer.parseInt(passStr.split(" - ")[0]);
        String passName = passStr.split(" - ")[1].split(" \\(")[0];
        String flightSeatKey = flightNum + "_" + selectedSeat;

        // Check if passenger already has an active booking
        for (Booking b : bookings) {
            if (b.passengerId == passId && !b.status.equals("Cancelled")) {
                JOptionPane.showMessageDialog(this, "Passenger " + passName + " already has an active booking!", "Already Booked", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Check if seat is already booked
        if (bookedSeats.containsKey(flightSeatKey)) {
            JOptionPane.showMessageDialog(this, "Seat " + selectedSeat + " is already booked on flight " + flightNum + "!", "Seat Unavailable", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ref = generateRef();

        Flight flight = null;
        for (Flight f : flights) {
            if (f.flightNumber.equals(flightNum)) {
                flight = f;
                break;
            }
        }

        // Create booking
        Booking booking = new Booking(ref, flightNum, passId, passName, selectedSeat, "Confirmed");
        bookings.add(booking);
        bookedSeats.put(flightSeatKey, ref);

        // Generate ticket
        StringBuilder ticket = new StringBuilder();
        ticket.append("╔═══════════════════════════════════════════════════════╗\n");
        ticket.append("║             BOARDING PASS                         ║\n");
        ticket.append("╠═══════════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║  Reference: %-37s ║\n", ref));
        ticket.append(String.format("║  Flight:    %-37s ║\n", flightNum));
        ticket.append(String.format("║  Route:     %-37s ║\n", flight.origin + " → " + flight.destination));
        ticket.append(String.format("║  Date:      %-37s ║\n", flight.departure));
        ticket.append("╠═══════════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║  Passenger: %-36s ║\n", passName));
        ticket.append(String.format("║  Seat:      %-37s ║\n", selectedSeat));
        ticket.append("╠═══════════════════════════════════════════════════════╣\n");
        ticket.append(String.format("║  Total Paid: $%-36s ║\n", (int)flight.basePrice));
        ticket.append("╚═══════════════════════════════════════════════════════╝\n");
        ticket.append("\n         Have a pleasant flight! ✈\n");

        ticketArea.setText(ticket.toString());
        JOptionPane.showMessageDialog(this, "Booking Confirmed!\nReference: " + ref);
        selectedSeat = null;
        refreshPassengerCombo();
        if (passengerCombo.getItemCount() > 0) {
            passengerCombo.setSelectedIndex(0);
        }
        refreshDashboard();
        buildSeatGrid();
        saveToFile();
    }

    private String generateRef() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder ref = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            ref.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return ref.toString();
    }

    // ============== CHECK-IN ==============

    private JPanel createCheckinPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Check-in & Boarding");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(SECONDARY);
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(LIGHT_BG);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Selection panel
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(CARD_BG);
        selectionPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(20, 20, 20, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> checkinFlightCombo = new JComboBox<>();
        for (Flight f : flights) {
            checkinFlightCombo.addItem(f.flightNumber + " - " + f.origin + " → " + f.destination);
        }
        JComboBox<String> checkinPassengerCombo = new JComboBox<>();
        for (Passenger p : passengers) {
            checkinPassengerCombo.addItem(p.id + " - " + p.name);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        selectionPanel.add(new JLabel("Select Flight:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(checkinFlightCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        selectionPanel.add(new JLabel("Select Passenger:"), gbc);
        gbc.gridx = 1;
        selectionPanel.add(checkinPassengerCombo, gbc);

        JButton searchBtn = new JButton("Search Booking");
        searchBtn.setBackground(PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setOpaque(true);
        searchBtn.setBorderPainted(false);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        selectionPanel.add(searchBtn, gbc);

        panel.add(selectionPanel, BorderLayout.NORTH);

        // Result panel
        JPanel resultPanel = new JPanel(new BorderLayout(20, 20));
        resultPanel.setBackground(CARD_BG);
        resultPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel resultLabel = new JLabel("Booking Details");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        JTextArea checkinArea = new JTextArea();
        checkinArea.setEditable(false);
        checkinArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        checkinArea.setText("\nSelect flight and passenger, then click 'Search Booking' to find booking.");
        resultPanel.add(new JScrollPane(checkinArea), BorderLayout.CENTER);

        // Action buttons
        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton checkinBtn = new JButton("Check In");
        checkinBtn.setBackground(PRIMARY);
        checkinBtn.setForeground(Color.WHITE);
        checkinBtn.setOpaque(true);
        checkinBtn.setBorderPainted(false);
        checkinBtn.setPreferredSize(new Dimension(140, 40));

        JButton boardBtn = new JButton("Board");
        boardBtn.setBackground(SUCCESS);
        boardBtn.setForeground(Color.WHITE);
        boardBtn.setOpaque(true);
        boardBtn.setBorderPainted(false);
        boardBtn.setPreferredSize(new Dimension(140, 40));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(220, 53, 69));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setPreferredSize(new Dimension(140, 40));

        actionBtns.add(checkinBtn);
        actionBtns.add(boardBtn);
        actionBtns.add(cancelBtn);
        resultPanel.add(actionBtns, BorderLayout.SOUTH);

        checkinListModel = new DefaultListModel<>();
        bookingList = new JList<>(checkinListModel);

        searchBtn.addActionListener(e -> {
            String flightStr = (String) checkinFlightCombo.getSelectedItem();
            String passStr = (String) checkinPassengerCombo.getSelectedItem();
            if (flightStr == null || passStr == null) {
                JOptionPane.showMessageDialog(panel, "Select flight and passenger!"); return;
            }
            String flightNum = flightStr.split(" - ")[0];
            int passId = Integer.parseInt(passStr.split(" - ")[0]);

            Booking found = null;
            for (Booking b : bookings) {
                if (b.flightNumber.equals(flightNum) && b.passengerId == passId && !b.status.equals("Cancelled")) {
                    found = b;
                    break;
                }
            }

            if (found == null) {
                checkinArea.setText("\nNo active booking found for this flight/passenger combination.");
                checkinListModel.clear();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("BOOKING FOUND\n");
                sb.append("═══════════════════════════════\n");
                sb.append("Reference:  ").append(found.ref).append("\n");
                sb.append("Flight:     ").append(found.flightNumber).append("\n");
                sb.append("Passenger:  ").append(found.passengerName).append(" (ID: ").append(found.passengerId).append(")\n");
                sb.append("Seat:       ").append(found.seat).append("\n");
                sb.append("Status:     ").append(found.status).append("\n");
                sb.append("═══════════════════════════════\n");
                checkinArea.setText(sb.toString());
                checkinListModel.clear();
                checkinListModel.addElement(found.ref + "|" + found.flightNumber + "|" + found.passengerId + "|" + found.passengerName + "|" + found.seat + "|" + found.status);
            }
        });

        checkinBtn.addActionListener(e -> {
            if (checkinListModel.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Search for a booking first!"); return;
            }
            String sel = checkinListModel.get(0);
            String ref = sel.split("\\|")[0];
            for (Booking b : bookings) {
                if (b.ref.equals(ref) && b.status.equals("Confirmed")) {
                    b.status = "Checked-in";
                    saveToFile();
                    JOptionPane.showMessageDialog(panel, "Check-in successful for " + ref);
                    searchBtn.doClick();
                    refreshDashboard();
                    return;
                }
            }
            JOptionPane.showMessageDialog(panel, "Cannot check-in. Booking may not exist or already checked-in.");
        });

        boardBtn.addActionListener(e -> {
            if (checkinListModel.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Search for a booking first!"); return;
            }
            String sel = checkinListModel.get(0);
            String ref = sel.split("\\|")[0];
            for (Booking b : bookings) {
                if (b.ref.equals(ref) && b.status.equals("Checked-in")) {
                    b.status = "Boarded";
                    saveToFile();
                    JOptionPane.showMessageDialog(panel, "Boarding completed for " + ref);
                    searchBtn.doClick();
                    refreshDashboard();
                    return;
                }
            }
            JOptionPane.showMessageDialog(panel, "Must check-in before boarding!");
        });

        cancelBtn.addActionListener(e -> {
            if (checkinListModel.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Search for a booking first!"); return;
            }
            String sel = checkinListModel.get(0);
            String ref = sel.split("\\|")[0];
            int confirm = JOptionPane.showConfirmDialog(panel, "Cancel booking " + ref + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                for (Booking b : bookings) {
                    if (b.ref.equals(ref)) {
                        b.status = "Cancelled";
                        bookedSeats.remove(b.flightNumber + "_" + b.seat);
                        saveToFile();
                        JOptionPane.showMessageDialog(panel, "Booking cancelled.");
                        checkinArea.setText("\nSelect flight and passenger, then click 'Search Booking' to find booking.");
                        checkinListModel.clear();
                        refreshDashboard();
                        return;
                    }
                }
            }
        });

        panel.add(resultPanel, BorderLayout.CENTER);

        return panel;
    }

    // Data classes
    static class Flight {
        String flightNumber, origin, destination, departure, aircraft;
        int totalSeats;
        double basePrice;
        Flight(String fn, String o, String d, String dep, String ac, int seats, double price) {
            this.flightNumber = fn;
            this.origin = o;
            this.destination = d;
            this.departure = dep;
            this.aircraft = ac;
            this.totalSeats = seats;
            this.basePrice = price;
        }
    }

    static class Passenger {
        int id;
        String name, email, phone, passport, nationality;
        Passenger(int id, String name, String email, String phone, String passport, String nat) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.passport = passport;
            this.nationality = nat;
        }
    }

    static class Booking {
        String ref, flightNumber, seat, status, passengerName;
        int passengerId;
        Booking(String ref, String fn, int pid, String pn, String seat, String status) {
            this.ref = ref;
            this.flightNumber = fn;
            this.passengerId = pid;
            this.passengerName = pn;
            this.seat = seat;
            this.status = status;
        }
    }

    public static void main(String[] args) {
        Main frame = new Main();
        frame.setVisible(true);
    }
}
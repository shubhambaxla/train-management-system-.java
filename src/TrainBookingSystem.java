import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class TrainBookingSystem extends JFrame {
    private JComboBox<String> departureStateCombo, departureDistrictCombo;
    private JComboBox<String> arrivalStateCombo, arrivalDistrictCombo;
    private JTextField dateField;
    private JButton searchButton, selectTrainButton, manageBookingsButton;
    private JTextArea resultArea;
    private Connection conn;

    public TrainBookingSystem() {
        setTitle("Train Ticket Booking System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize database connection
        initializeDatabaseConnection();

        // Create UI components
        createUIComponents();

        // Populate state combos
        populateStateCombo(departureStateCombo);
        populateStateCombo(arrivalStateCombo);

        // Add action listeners
        addActionListeners();
    }

    private void initializeDatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tproject", "root", "123456789");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
        }
    }

    private void createUIComponents() {
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 122, 204)); // Blue background
        JLabel headerLabel = new JLabel("Train Ticket Booking System");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(headerLabel);
        
        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(new Color(255, 165, 0)); // Orange background
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding for each component
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Departure State
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Departure State:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        departureStateCombo = new JComboBox<>();
        inputPanel.add(departureStateCombo, gbc);

        // Departure District
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Departure District:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        departureDistrictCombo = new JComboBox<>();
        inputPanel.add(departureDistrictCombo, gbc);

        // Arrival State
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Arrival State:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        arrivalStateCombo = new JComboBox<>();
        inputPanel.add(arrivalStateCombo, gbc);

        // Arrival District
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Arrival District:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        arrivalDistrictCombo = new JComboBox<>();
        inputPanel.add(arrivalDistrictCombo, gbc);

        // Date Field
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        dateField = new JTextField(10);
        inputPanel.add(dateField, gbc);

        // Search Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        searchButton = new JButton("Search Trains");
        searchButton.setBackground(new Color(0, 122, 204)); // Blue background
        searchButton.setForeground(Color.WHITE);
        inputPanel.add(searchButton, gbc);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(Color.WHITE);
        resultArea.setForeground(Color.BLACK);
        
        // Scroll Pane for Result Area
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Main Content Panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(inputPanel, BorderLayout.NORTH);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // Create a button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 165, 0)); // Orange background
        selectTrainButton = new JButton("Select Train");
        manageBookingsButton = new JButton("Manage Bookings");
        buttonPanel.add(selectTrainButton);
        buttonPanel.add(manageBookingsButton);

        // Add components to the main frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addActionListeners() {
        departureStateCombo.addActionListener(e -> populateDistrictCombo(departureStateCombo, departureDistrictCombo));
        arrivalStateCombo.addActionListener(e -> populateDistrictCombo(arrivalStateCombo, arrivalDistrictCombo));
        searchButton.addActionListener(e -> searchTrains());
        selectTrainButton.addActionListener(e -> proceedToBooking());
        manageBookingsButton.addActionListener(e -> openManageBookings());
    }

    private void populateStateCombo(JComboBox<String> combo) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT State_name FROM state_table");
            while (rs.next()) {
                combo.addItem(rs.getString("State_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateDistrictCombo(JComboBox<String> stateCombo, JComboBox<String> districtCombo) {
        districtCombo.removeAllItems();
        String selectedState = (String) stateCombo.getSelectedItem();

        if (selectedState != null) {
            try {
                PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT d.District_name FROM district_table d " +
                    "JOIN state_table s ON d.State_key = s.State_key " +
                    "WHERE s.State_name = ?"
                );
                pstmt.setString(1, selectedState);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    districtCombo.addItem(rs.getString("District_name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchTrains() {
        String departureDistrict = (String) departureDistrictCombo.getSelectedItem();
        String arrivalDistrict = (String) arrivalDistrictCombo.getSelectedItem();
        String date = dateField.getText();

        // Simulate train search (replace with actual DB query)
        resultArea.setText("Searching for trains from " + departureDistrict + 
                           " to " + arrivalDistrict + " on " + date + "\n\n" +
                           "Available Trains:\n" +
                           "1. Train A - Departure: " + departureDistrict + ", Arrival: " + arrivalDistrict + "\n" +
                           "2. Train B - Departure: " + departureDistrict + ", Arrival: " + arrivalDistrict);
    }

    private void proceedToBooking() {
        String selectedTrain = resultArea.getSelectedText(); // Get selected train info
        if (selectedTrain != null) {
            BookingForm bookingForm = new BookingForm(selectedTrain);
            bookingForm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a train to book.");
        }
    }

    private void openManageBookings() {
        ManageBookingsApp manageBookingsApp = new ManageBookingsApp();
        manageBookingsApp.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrainBookingSystem app = new TrainBookingSystem();
            app.setVisible(true);
        });
    }
}

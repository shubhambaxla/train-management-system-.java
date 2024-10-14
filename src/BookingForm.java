import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BookingForm extends JFrame {
    private String trainDetails;
    private JTextField nameField, ageField, genderField, emailField;
    private JButton confirmButton, manageBookingsButton;

    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tproject";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456789";

    public BookingForm(String trainDetails) {
        this.trainDetails = trainDetails;

        setTitle("Booking Form");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding for components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header Label
        JLabel headerLabel = new JLabel("Booking Details");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 122, 204)); // Blue
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        add(headerLabel, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Train Details
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Train Details:"), gbc);
        gbc.gridx = 1;
        add(new JLabel(trainDetails), gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField();
        add(nameField, gbc);

        // Age
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        ageField = new JTextField();
        add(ageField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        genderField = new JTextField();
        add(genderField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField();
        add(emailField, gbc);

        // Confirm Button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2; // Span across two columns
        confirmButton = new JButton("Confirm Booking");
        confirmButton.setBackground(new Color(0, 122, 204)); // Blue
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(e -> confirmBooking());
        add(confirmButton, gbc);

        // Manage Bookings Button
        gbc.gridy = 7; // Position below the confirm button
        manageBookingsButton = new JButton("Manage Bookings");
        manageBookingsButton.setBackground(new Color(255, 165, 0)); // Orange
        manageBookingsButton.setForeground(Color.WHITE);
        manageBookingsButton.addActionListener(e -> openTrainBookingSystem());
        add(manageBookingsButton, gbc);
    }

    private void confirmBooking() {
        String name = nameField.getText();
        String age = ageField.getText();
        String gender = genderField.getText();
        String email = emailField.getText();

        // Save booking to the database
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO bookings (passenger_name, passenger_email, train_id) VALUES (?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, extractTrainId(trainDetails)); // Placeholder logic
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Booking confirmed for " + name + "!");
            dispose(); // Close the booking form
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error confirming booking: " + ex.getMessage());
        }
    }

    private int extractTrainId(String trainDetails) {
        // Implement logic to extract train ID from trainDetails.
        return 1; // Placeholder
    }

    private void openTrainBookingSystem() {
        TrainBookingSystem bookingSystem = new TrainBookingSystem();
        bookingSystem.setVisible(true);
        dispose(); // Optionally close the booking form-
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookingForm form = new BookingForm("Train A - Departure: City A, Arrival: City B");
            form.setVisible(true);
        });
    }
}

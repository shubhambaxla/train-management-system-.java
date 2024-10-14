import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField emailField, passwordField, nameField;
    private JButton signInButton, signUpButton;

    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tproject";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456789";

    public LoginPage() {
        setTitle("Login Page");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        add(leftPanel);
        add(rightPanel);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("manage cancel/src/train image.jpg"); // Replace with actual path
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTextLabel = new JLabel("<html><center> </center></html>");
        subTextLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subTextLabel.setForeground(Color.WHITE);
        subTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        signInButton = new JButton("SIGN IN");
        signInButton.setFont(new Font("Arial", Font.BOLD, 14));
        signInButton.setForeground(new Color(76, 175, 80));
        signInButton.setBackground(Color.WHITE);
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInButton.setBorderPainted(false);
        signInButton.setFocusPainted(false);
        signInButton.setMaximumSize(new Dimension(120, 40));
        signInButton.addActionListener(e -> login());

        JPanel overlay = new JPanel();
        overlay.setBackground(new Color(0, 0, 0, 128)); // Semi-transparent black
        overlay.setLayout(new BoxLayout(overlay, BoxLayout.Y_AXIS));
        overlay.setOpaque(false);

        overlay.add(Box.createVerticalGlue());
        overlay.add(welcomeLabel);
        overlay.add(Box.createRigidArea(new Dimension(0, 20)));
        overlay.add(subTextLabel);
        overlay.add(Box.createRigidArea(new Dimension(0, 40)));
        overlay.add(signInButton);
        overlay.add(Box.createVerticalGlue());

        panel.add(overlay);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel createAccountLabel = new JLabel("Create Account");
        createAccountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        createAccountLabel.setForeground(new Color(76, 175, 80));
        createAccountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = createTextField("Name");
        emailField = createTextField("Email");
        passwordField = createTextField("Password");

        signUpButton = new JButton("SIGN UP");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBackground(new Color(76, 175, 80));
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setMaximumSize(new Dimension(120, 40));
        signUpButton.addActionListener(e -> register());

        panel.add(Box.createVerticalGlue());
        panel.add(createAccountLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(signUpButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setMaximumSize(new Dimension(250, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
        return textField;
    }

    // Method to handle user login
    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    // Open TrainBookingSystem GUI
                    openTrainBookingSystem();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error logging in: " + ex.getMessage());
        }
    }

    // Method to handle user registration
    private void register() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            // Open TrainBookingSystem GUI after registration
            openTrainBookingSystem();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering user: " + ex.getMessage());
        }
    }

    // Method to open TrainBookingSystem GUI
    private void openTrainBookingSystem() {
        // Close the current login window
        this.dispose();
        // Open the Train Booking System window
        SwingUtilities.invokeLater(() -> {
            new TrainBookingSystem().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}

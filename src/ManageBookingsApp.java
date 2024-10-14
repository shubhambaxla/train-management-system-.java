import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageBookingsApp extends JFrame {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton modifyButton, cancelButton;
    private JLabel statusLabel;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/tproject";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456789";

    public ManageBookingsApp() {
        setTitle("Manage Bookings");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createTable();
        createButtons();
        createStatusLabel();

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadBookings();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(255, 103, 31));
        JLabel titleLabel = new JLabel("Manage Bookings");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        return headerPanel;
    }

    private void createTable() {
        String[] columns = {"Booking ID", "Passenger Name", "Passenger Email"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void createButtons() {
        modifyButton = new JButton("Modify Booking");
        cancelButton = new JButton("Cancel Booking");

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyBooking();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBooking();
            }
        });
    }

    private void createStatusLabel() {
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(modifyButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private void loadBookings() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bookings")) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("passenger_name"),
                    rs.getString("passenger_email")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void modifyBooking() {
        // Logic for modifying a booking goes here
        JOptionPane.showMessageDialog(this, "Modify Booking functionality not implemented yet.");
    }

    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM bookings WHERE booking_id = ?")) {
                pstmt.setInt(1, bookingId);
                pstmt.executeUpdate();
                loadBookings(); // Refresh the table
                JOptionPane.showMessageDialog(this, "Booking canceled successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManageBookingsApp app = new ManageBookingsApp();
            app.setVisible(true);
        });
    }
}

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminTrackNotificationsDialog extends JDialog {

    private JTable notificationTable;
    private DefaultTableModel tableModel;

    public AdminTrackNotificationsDialog() {
        setTitle("Track Notifications");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Sent and Scheduled Notifications", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{
            "ID", "Title", "Message", "Recipient Group", "Event Date", "Scheduled At", "Sent"
        }, 0);
        notificationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(notificationTable);
        add(scrollPane, BorderLayout.CENTER);

        loadNotifications();
        setVisible(true);
    }

    private void loadNotifications() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Notification ORDER BY scheduledAt DESC")) {

            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0); // clear previous

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("notificationID"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getString("recipientGroup"),
                    rs.getDate("eventDate"),
                    rs.getTimestamp("scheduledAt"),
                    rs.getBoolean("sent") ? "✔️ Sent" : "🕒 Pending"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading notifications.");
        }
    }
}

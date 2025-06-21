import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminDeleteScheduledNotificationDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    public AdminDeleteScheduledNotificationDialog() {
        setTitle("Delete Scheduled Notifications");
        setSize(700, 400);
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Scheduled Notifications", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Title", "Scheduled Date", "Recipient Group"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;  // Make all cells non-editable
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(178, 34, 34));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setFont(new Font("Tahoma", Font.PLAIN, 16));

        deleteBtn.addActionListener(e -> deleteSelectedNotification());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadScheduledNotifications();
        setVisible(true);
    }

    private void loadScheduledNotifications() {
        model.setRowCount(0); // Clear previous data
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT notificationID, title, scheduledAt, recipientGroup FROM Notification WHERE sent = FALSE"
             )) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("notificationID"),
                        rs.getString("title"),
                        rs.getTimestamp("scheduledAt"),
                        rs.getString("recipientGroup")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load scheduled notifications.");
        }
    }

    private void deleteSelectedNotification() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a notification to delete.");
            return;
        }

        int notificationID = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this notification?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "DELETE FROM Notification WHERE notificationID = ?")) {
                stmt.setInt(1, notificationID);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Notification deleted.");
                loadScheduledNotifications(); // Refresh
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to delete notification.");
            }
        }
    }
}

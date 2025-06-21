import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewNotificationsDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    public ViewNotificationsDialog(String role) {
        setTitle("View Notifications");
        setSize(750, 400);
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Notifications for " + role.substring(0, 1).toUpperCase() + role.substring(1), SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Title", "Message", "Event Date", "Scheduled At"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadNotifications(role);
        setVisible(true);
    }

    private void loadNotifications(String role) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT title, message, eventDate, scheduledAt FROM Notification WHERE sent = TRUE AND (recipientGroup = ? OR recipientGroup = 'both') ORDER BY scheduledAt DESC"
             )) {
            stmt.setString(1, role.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getDate("eventDate"),
                        rs.getTimestamp("scheduledAt")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load notifications.");
        }
    }
}

import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AssignRolePanel extends JFrame {

    private JComboBox<String> userComboBox;
    private JComboBox<String> roleComboBox;
    private JButton updateButton;
    private HashMap<String, Integer> userMap = new HashMap<>();

    public AssignRolePanel() {
        setTitle("Assign Roles");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Header =====
        JLabel header = new JLabel("Assign Roles", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 26));
        header.setBorder(new EmptyBorder(30, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 20));
        formPanel.setBorder(new EmptyBorder(30, 80, 20, 80));

        formPanel.add(new JLabel("Select User:", SwingConstants.RIGHT));
        userComboBox = new JComboBox<>();
        formPanel.add(userComboBox);

        formPanel.add(new JLabel("Assign Role:", SwingConstants.RIGHT));
        roleComboBox = new JComboBox<>(new String[]{"student", "alumni"});
        formPanel.add(roleComboBox);

        formPanel.add(new JLabel(""));
        updateButton = new JButton("Update Role");
        styleButton(updateButton);
        formPanel.add(updateButton);

        add(formPanel, BorderLayout.CENTER);

        // Load users into dropdown
        loadUsers();

        // Handle role update button
        updateButton.addActionListener(e -> updateRole());

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
    }

    private void loadUsers() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT userID, firstName, lastName FROM User WHERE role IN ('student', 'alumni')")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int userID = rs.getInt("userID");
                String displayName = rs.getString("firstName") + " " + rs.getString("lastName") + " (ID: " + userID + ")";
                userComboBox.addItem(displayName);
                userMap.put(displayName, userID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load users.");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void updateRole() {
        String selectedUser = (String) userComboBox.getSelectedItem();
        String selectedRole = (String) roleComboBox.getSelectedItem();

        if (selectedUser == null || selectedRole == null) {
            JOptionPane.showMessageDialog(this, "Please select both user and role.");
            return;
        }

        int userID = userMap.get(selectedUser);
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE User SET role = ? WHERE userID = ?")) {
            stmt.setString(1, selectedRole);
            stmt.setInt(2, userID);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Role updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update role.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.");
        } finally {
            try {
                conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AssignRolePanel::new);
    }
}

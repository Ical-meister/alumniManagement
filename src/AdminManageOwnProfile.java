import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AdminManageOwnProfile extends JFrame {

    private int adminID;

    private JTextField firstNameField, lastNameField, emailField, phoneField, academicField;
    private JButton saveButton;

    public AdminManageOwnProfile(int adminID) {
        this.adminID = adminID;
        setTitle("Manage Own Profile");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Edit My Profile", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 24));
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(20, 60, 20, 60));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        academicField = new JTextField();

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Academic Background:"));
        formPanel.add(academicField);

        saveButton = new JButton("Save Changes");
        styleButton(saveButton);
        formPanel.add(new JLabel(""));
        formPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);

        loadProfileData();

        saveButton.addActionListener(e -> updateProfile());

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
    }

    private void loadProfileData() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT firstName, lastName, email, phone, academicBackground FROM User WHERE userID = ?")) {
            stmt.setInt(1, adminID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                firstNameField.setText(rs.getString("firstName"));
                lastNameField.setText(rs.getString("lastName"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
                academicField.setText(rs.getString("academicBackground"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load profile data.");
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void updateProfile() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE User SET firstName = ?, lastName = ?, email = ?, phone = ?, academicBackground = ? WHERE userID = ?")) {

            stmt.setString(1, firstNameField.getText());
            stmt.setString(2, lastNameField.getText());
            stmt.setString(3, emailField.getText());
            stmt.setString(4, phoneField.getText());
            stmt.setString(5, academicField.getText());
            stmt.setInt(6, adminID);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.");
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminManageOwnProfile(1001));
    }
}

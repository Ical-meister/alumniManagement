import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class UserFormDialog extends JDialog {

    private JTextField firstNameField, lastNameField, emailField, phoneField, academicField;
    private JComboBox<String> roleComboBox;
    private JButton saveButton;
    private JTextField passwordField;   // Changed from JPasswordField to JTextField

    private Integer userID = null;

    public UserFormDialog(Integer userID) {
        this.userID = userID;

        setTitle(userID == null ? "Create User" : "Edit User");
        setSize(500, 500);
        setModal(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel(userID == null ? "Create User" : "Edit User", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 12, 12));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        academicField = new JTextField();
        roleComboBox = new JComboBox<>(new String[]{"student", "alumni"});
        passwordField = new JTextField();  // <-- No longer hiding password (admin will enter full hash)

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
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);
        formPanel.add(new JLabel("Password Hash:"));
        formPanel.add(passwordField);

        saveButton = new JButton(userID == null ? "Create" : "Update");
        styleButton(saveButton);
        formPanel.add(new JLabel());
        formPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);

        if (userID != null) {
            loadUserData();
            passwordField.setEnabled(false);
        }

        saveButton.addActionListener(e -> saveUser());

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
    }

    private void loadUserData() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT firstName, lastName, email, phone, academicBackground, role FROM User WHERE userID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                firstNameField.setText(rs.getString("firstName"));
                lastNameField.setText(rs.getString("lastName"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone"));
                academicField.setText(rs.getString("academicBackground"));
                roleComboBox.setSelectedItem(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String academic = academicField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        String passwordHash = passwordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name, last name and email are required.");
            return;
        }

        if (userID == null && passwordHash.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password Hash is required when creating user.");
            return;
        }

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed.");
            return;
        }

        try {
            if (userID == null) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO User (firstName, lastName, email, phone, academicBackground, role, accountStatus, passwordHash) VALUES (?, ?, ?, ?, ?, ?, true, ?)");
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, academic);
                stmt.setString(6, role);
                stmt.setString(7, passwordHash);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "User created successfully.");
            } else {
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE User SET firstName = ?, lastName = ?, email = ?, phone = ?, academicBackground = ?, role = ? WHERE userID = ?");
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, academic);
                stmt.setString(6, role);
                stmt.setInt(7, userID);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "User updated successfully.");
            }
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred.");
        }
    }
}

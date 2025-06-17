import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class ViewUserProfilePanel extends JFrame {

    private int userID;
    private JLabel firstNameLabel, lastNameLabel, emailLabel, phoneLabel, academicLabel, roleLabel;

    public ViewUserProfilePanel(int userID) {
        this.userID = userID;

        setTitle("View User Profile");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("User Profile Details", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 24));
        header.setBorder(new EmptyBorder(20, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 12, 12));
        formPanel.setBorder(new EmptyBorder(30, 60, 30, 60));

        firstNameLabel = new JLabel();
        lastNameLabel = new JLabel();
        emailLabel = new JLabel();
        phoneLabel = new JLabel();
        academicLabel = new JLabel();
        roleLabel = new JLabel();

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameLabel);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameLabel);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailLabel);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneLabel);
        formPanel.add(new JLabel("Academic Background:"));
        formPanel.add(academicLabel);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleLabel);

        add(formPanel, BorderLayout.CENTER);

        loadUserProfile();

        setVisible(true);
    }

    private void loadUserProfile() {
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
                firstNameLabel.setText(rs.getString("firstName"));
                lastNameLabel.setText(rs.getString("lastName"));
                emailLabel.setText(rs.getString("email"));
                phoneLabel.setText(rs.getString("phone"));
                academicLabel.setText(rs.getString("academicBackground"));
                roleLabel.setText(rs.getString("role"));
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

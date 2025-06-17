import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import javax.swing.*;

public class StudentEditProfileDialog extends JDialog {

    private JTextField firstNameField, lastNameField, phoneField, academicField;
    private int userID;
    private String profilePicPath = null;

    public StudentEditProfileDialog(int userID) {
        this.userID = userID;
        setTitle("Manage Own Profile");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Edit My Profile", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name & Last Name (side by side)
        firstNameField = new JTextField();
        lastNameField = new JTextField();

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField();
        formPanel.add(phoneField, gbc);

        // Academic Background
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Academic Background:"), gbc);
        gbc.gridx = 1;
        academicField = new JTextField();
        formPanel.add(academicField, gbc);

        // Profile Picture Upload Button
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Profile Picture:"), gbc);
        gbc.gridx = 1;
        JButton uploadBtn = new JButton("Upload Profile Picture");
        styleButton(uploadBtn);
        formPanel.add(uploadBtn, gbc);

        // Save Button (spanning 2 columns)
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Save Changes");
        styleButton(saveBtn);
        formPanel.add(saveBtn, gbc);

        add(formPanel, BorderLayout.CENTER);

        uploadBtn.addActionListener(e -> chooseProfilePicture());
        saveBtn.addActionListener(e -> saveChanges());

        loadUserData();
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
    }

    private void loadUserData() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT firstName, lastName, phone, academicBackground, profilePicPath FROM User WHERE userID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                firstNameField.setText(rs.getString("firstName"));
                lastNameField.setText(rs.getString("lastName"));
                phoneField.setText(rs.getString("phone"));
                academicField.setText(rs.getString("academicBackground"));
                profilePicPath = rs.getString("profilePicPath");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE User SET firstName=?, lastName=?, phone=?, academicBackground=?, profilePicPath=? WHERE userID=?")) {
            stmt.setString(1, firstNameField.getText());
            stmt.setString(2, lastNameField.getText());
            stmt.setString(3, phoneField.getText());
            stmt.setString(4, academicField.getText());
            stmt.setString(5, profilePicPath);
            stmt.setInt(6, userID);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Profile updated.");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving changes.");
        }
    }

    private void chooseProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            File destFolder = new File("profile_pictures");
            if (!destFolder.exists()) destFolder.mkdir();

            String newFileName = "profile_pictures/" + userID + "_" + selectedFile.getName();

            try {
                Files.copy(selectedFile.toPath(), new File(newFileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                profilePicPath = newFileName;
                JOptionPane.showMessageDialog(this, "Profile picture uploaded.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save image.");
            }
        }
    }
}

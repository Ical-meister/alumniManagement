import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class StudentViewProfileDialog extends JDialog {

    private int userID;
    private JLabel profilePicLabel;

    public StudentViewProfileDialog(int userID) {
        this.userID = userID;
        setTitle("View My Profile");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("My Profile", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Image Panel at Top
        profilePicLabel = new JLabel("No Image", SwingConstants.CENTER);
        profilePicLabel.setPreferredSize(new Dimension(200, 200));
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentPanel.add(profilePicLabel, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel();
        JLabel emailLabel = new JLabel();
        JLabel phoneLabel = new JLabel();
        JLabel academicLabel = new JLabel();
        JLabel roleLabel = new JLabel();

        addRow(infoPanel, gbc, 0, "First Name:", nameLabel);
        addRow(infoPanel, gbc, 1, "Email:", emailLabel);
        addRow(infoPanel, gbc, 2, "Phone:", phoneLabel);
        addRow(infoPanel, gbc, 3, "Academic Background:", academicLabel);
        addRow(infoPanel, gbc, 4, "Role:", roleLabel);

        contentPanel.add(infoPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        loadUserData(nameLabel, emailLabel, phoneLabel, academicLabel, roleLabel);
        setVisible(true);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JLabel dataLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(dataLabel, gbc);
    }

    private void loadUserData(JLabel nameLabel, JLabel emailLabel, JLabel phoneLabel, JLabel academicLabel, JLabel roleLabel) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT firstName, lastName, email, phone, academicBackground, role, profilePicPath FROM User WHERE userID = ?")) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("firstName") + " " + rs.getString("lastName");
                nameLabel.setText(fullName);
                emailLabel.setText(rs.getString("email"));
                phoneLabel.setText(rs.getString("phone"));
                academicLabel.setText(rs.getString("academicBackground"));
                roleLabel.setText(rs.getString("role"));

                String profilePath = rs.getString("profilePicPath");

                if (profilePath != null && !profilePath.isEmpty()) {
                    try {
                        BufferedImage img = ImageIO.read(new File(profilePath));
                        Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        profilePicLabel.setIcon(new ImageIcon(scaledImg));
                        profilePicLabel.setText("");  // remove text if image loaded
                    } catch (Exception imgEx) {
                        profilePicLabel.setText("Image Load Error");
                    }
                } else {
                    profilePicLabel.setText("No Image");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

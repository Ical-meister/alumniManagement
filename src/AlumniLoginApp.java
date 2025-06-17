
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class AlumniLoginApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}

class LoginPage extends JFrame {

    JTextField emailField = new JTextField(20);
    JPasswordField passwordField = new JPasswordField(20);  // ✅ Changed back to JPasswordField (to hide characters)
    JLabel statusLabel = new JLabel(" ");

    public LoginPage() {
        setTitle("Alumni Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Login to Alumni Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //email box
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        //password box
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;

        // //Show Password Checkbox
        // JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
        // gbc.gridx = 1;
        // gbc.gridy = 2;
        // formPanel.add(showPasswordCheckbox, gbc);
        // Action Listener for show/hide
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));  // <- tighter spacing
        passwordPanel.add(passwordField);

        JCheckBox showPasswordCheckbox = new JCheckBox("Show");
        passwordPanel.add(showPasswordCheckbox);

        formPanel.add(passwordPanel, gbc);
// ActionListener for checkbox
        showPasswordCheckbox.addActionListener(e -> {
            if (showPasswordCheckbox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022');
            }
        });

        // Buttons
        JButton loginBtn = new JButton("Login");
        JButton guestBtn = new JButton("Guest");
        loginBtn.setBackground(new Color(30, 144, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        guestBtn.setBackground(new Color(60, 179, 113));
        guestBtn.setForeground(Color.WHITE);
        guestBtn.setFocusPainted(false);

        JLabel forgotLabel = new JLabel("<html><a href=''>Forgot Password?</a></html>");
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.setForeground(Color.BLUE);
        forgotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "Password recovery coming soon!");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(forgotLabel, gbc);

        JPanel loginGuestPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        loginGuestPanel.add(loginBtn);
        loginGuestPanel.add(guestBtn);

        gbc.gridy = 4;
        formPanel.add(loginGuestPanel, gbc);

        gbc.gridy = 5;
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(statusLabel, gbc);

        loginBtn.addActionListener(e -> validateLogin());
        guestBtn.addActionListener(e -> {
            dispose();
            new GuestPage();
        });

        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
        revalidate();
        repaint();
        SwingUtilities.invokeLater(() -> emailField.requestFocusInWindow());

        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void validateLogin() {
        String email = emailField.getText().trim();
        String passwordHash = new String(passwordField.getPassword()).trim();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/alumni_management",
                    "alumni_app",
                    "test1234"
            );

            String query = "SELECT role, userID FROM User WHERE email = ? AND passwordHash = ? AND accountStatus = TRUE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role").toLowerCase();

                switch (role) {
                    case "admin":
                        int adminID = rs.getInt("userID");
                        new AdminPage(adminID);
                        break;
                    case "student":
                        int studentID = rs.getInt("userID");
                        new StudentPage(studentID);
                        break;
                    case "alumni":
                        int alumniID = rs.getInt("userID");
                        new AlumniPage(alumniID);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Unknown role: " + role);
                        return;
                }
                this.dispose();
            } else {
                statusLabel.setText("Invalid credentials or inactive account.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Database error.");
        }
    }
}


import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class AdminPage extends JFrame {

    private int adminID;

    public AdminPage(int adminID) {
        this.adminID = adminID;
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Big Header
        JLabel header = new JLabel("Welcome Admin", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 28));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2, 20, 20));

        // Buttons
        String[] buttonLabels = {
            "Manage Profiles",
            "Create & Manage Events",
            "Track Donations",
            "Send Notifications",
            "Manage Mentorship Program",
            "Moderate Forum",
            "Assign Roles",
            "Logout"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Tahoma", Font.PLAIN, 16));
            button.setPreferredSize(new Dimension(300, 80));
            button.setFocusPainted(false);
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.addActionListener(this::handleButtonClick);
            buttonPanel.add(button);
        }

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        add(header, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void handleButtonClick(ActionEvent e) {
        String command = ((JButton) e.getSource()).getText();
        switch (command) {
            case "Logout":
                int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?",
                        "Logout Confirmation", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginPage();
                }
                break;
            case "Manage Mentorship Program":
                dispose();
                new MentorshipAdminDashboard(1001, null);
                break;
            case "Create & Manage Events":
                new AdminEventFrame(adminID);
                break;
            case "Assign Roles":  // ✅ NEW FUNCTIONALITY ADDED HERE
                new AssignRolePanel();
                break;
            case "Manage Profiles":
                new AdminManageProfilesPanel(adminID);
                break;

            default:
                JOptionPane.showMessageDialog(this,
                        command + " clicked (Functionality not implemented yet)",
                        "Feature Coming Soon",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }

}

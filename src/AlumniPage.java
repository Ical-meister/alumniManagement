
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class AlumniPage extends JFrame {

    int alumniID;

    public AlumniPage(int alumniID) {
        this.alumniID = alumniID;

        setTitle("Alumni Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Welcome Alumni", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 28));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2, 20, 20));

        String[] buttonLabels = {
            "Manage Profile",
            "Browse & Register for Events",
            "Donation",
            "Forums",
            "Apply to be a Mentor",
            "View Notifications",
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
            case "Donation":
                dispose();                // Close current AlumniPage
                new DonationPage(alumniID, "alumni");      // Open Donation sub-page
                break;
            case "Apply to be a Mentor":
                dispose();
                new MentorshipAlumniDashboard(alumniID, null);
                break;
            case "Forums":
                new ForumPostsListUI().setVisible(true);
                break;
            case "Manage Profile":
                new AlumniProfilePanel(alumniID);
                break;
             case "View Notifications":
                new ViewNotificationsDialog("alumni");
                break;


            default:
                JOptionPane.showMessageDialog(this,
                        command + " clicked (Functionality not implemented yet)",
                        "Feature Coming Soon",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

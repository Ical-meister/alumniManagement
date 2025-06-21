
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class StudentPage extends JFrame {

    private int studentID;

    public StudentPage(int studentID) {
        this.studentID = studentID;

        setTitle("Student Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Welcome Student", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 28));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        // Centered panel with buttons in GridLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 20, 20));

        String[] buttonLabels = {
            "Mentorship Program",
            "Attend Alumni Events",
            "Forums",
            "Donation",
            "Manage Profile",
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

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

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
            case "Mentorship Program":
                new MentorMenteeManagement(studentID);
                break;
            case "Donation":
                dispose();
                ;
                new DonationPage(studentID, "student"); // Specify it's a student
                break;
            case "Forums":
                new ForumPostsListUI().setVisible(true);
                break;
            case "Manage Profile":
                new StudentManageProfilesPanel(studentID);
                break;
            case "View Notifications":
                new ViewNotificationsDialog("student");
                break;

            default:
                JOptionPane.showMessageDialog(this,
                        command + " clicked (Functionality not implemented yet)",
                        "Feature Coming Soon",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

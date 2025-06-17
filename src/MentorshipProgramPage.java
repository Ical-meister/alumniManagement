import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class MentorshipProgramPage extends JFrame {

    private int studentID;
    private int adminID;

    public MentorshipProgramPage(int studentID) {
        setTitle("Mentorship Program");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Mentorship Program", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton requestButton = new JButton("Send Mentorship Request");
        JButton scheduleButton = new JButton("Schedule Appointment");
        JButton backButton = new JButton("Back");

        Font btnFont = new Font("Tahoma", Font.PLAIN, 16);
        for (JButton btn : new JButton[]{requestButton, scheduleButton, backButton}) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
        }

        requestButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Send Mentorship Request clicked"));

        scheduleButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Schedule Appointment clicked"));

        backButton.addActionListener((ActionEvent e) -> {
            dispose();          // Close this page
            new AdminPage(adminID);
            new StudentPage(studentID);  // Go back to main student dashboard
        });

        buttonPanel.add(requestButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(backButton);

        add(header, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}

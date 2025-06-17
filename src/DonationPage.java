import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class DonationPage extends JFrame {
    int userID;
    String userType; // "alumni" or "student"

    public DonationPage(int userID, String userType) {
        this.userID = userID;
        this.userType = userType;

        setTitle("Donation Center");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Donation Center", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton donateButton = new JButton("Make a Donation");
        JButton historyButton = new JButton("View Donation History");
        JButton backButton = new JButton("Back");

        Font btnFont = new Font("Tahoma", Font.PLAIN, 16);
        for (JButton btn : new JButton[]{donateButton, historyButton, backButton}) {
            btn.setFont(btnFont);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(0, 102, 204));
            btn.setForeground(Color.WHITE);
        }

        donateButton.addActionListener(e -> {
            new DonationForm(userID);
        });

        historyButton.addActionListener(e -> {
            new DonationHistory(userID, userType).setVisible(true);
        });

        backButton.addActionListener(e -> {
            dispose();
            if (userType.equals("alumni")) {
                new AlumniPage(userID);
            } else if (userType.equals("student")) {
                new StudentPage(userID);
            }
        });

        buttonPanel.add(donateButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(backButton);

        add(header, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}


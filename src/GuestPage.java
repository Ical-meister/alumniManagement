import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class GuestPage extends JFrame {

    public GuestPage() {
        setTitle("Guest Portal");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Welcome Guest", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton eventBtn = new JButton("Sign Up for Events");
        JButton donationBtn = new JButton("Make a Donation");
        JButton newsletterBtn = new JButton("Newsletter");
        JButton backBtn = new JButton("Back");

        Font btnFont = new Font("Tahoma", Font.PLAIN, 16);
        for (JButton btn : new JButton[]{eventBtn, donationBtn, newsletterBtn, backBtn}) {
            btn.setFont(btnFont);
            btn.setPreferredSize(new Dimension(300, 80)); 
            btn.setFocusPainted(false);
            btn.setBackground(new Color(70, 130, 180));
            btn.setForeground(Color.WHITE);
        }

      
        eventBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Sign Up for Events clicked"));

        donationBtn.addActionListener(e -> {
            new DonationForm(null); // null indicates it's a guest
        });


        newsletterBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Newsletter clicked"));

        // Back to Login
        backBtn.addActionListener((ActionEvent e) -> {
            dispose();
            new LoginPage();
        });

        buttonPanel.add(eventBtn);
        buttonPanel.add(donationBtn);
        buttonPanel.add(newsletterBtn);
        buttonPanel.add(backBtn);

        add(header, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}

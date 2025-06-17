import java.awt.*;
import javax.swing.*;

public class AdminManageProfilesPanel extends JFrame {

    private int adminID;

    public AdminManageProfilesPanel(int adminID) {
        this.adminID = adminID;

        setTitle("Manage Profiles");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Manage Profiles", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        JButton ownProfileBtn = new JButton("Manage Own User Profile");
        JButton otherProfilesBtn = new JButton("Manage Other Profiles");

        styleButton(ownProfileBtn);
        styleButton(otherProfilesBtn);

        buttonPanel.add(ownProfileBtn);
        buttonPanel.add(otherProfilesBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // ✅ ✅ Now you can safely pass adminID
        ownProfileBtn.addActionListener(e -> new AdminManageOwnProfile(adminID));

        otherProfilesBtn.addActionListener(e -> new AdminManageOtherProfilesPanel());


        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Tahoma", Font.PLAIN, 18));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(400, 60));
    }

    public static void main(String[] args) {
        // For testing only; just assume adminID = 1001
        SwingUtilities.invokeLater(() -> new AdminManageProfilesPanel(1001));
    }
}

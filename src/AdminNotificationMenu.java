
import java.awt.*;
import javax.swing.*;

public class AdminNotificationMenu extends JFrame {

    private int adminID;

    public AdminNotificationMenu(int adminID) {
        this.adminID = adminID;
        setTitle("Manage Notifications");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Manage Notifications", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton sendButton = new JButton("Send Notification");
        JButton trackButton = new JButton("Track Notifications");
        JButton deleteButton = new JButton("Delete Scheduled Notifications");

        styleButton(sendButton);
        styleButton(trackButton);
        styleButton(deleteButton);

        buttonPanel.add(sendButton);
        buttonPanel.add(trackButton);
        buttonPanel.add(deleteButton);

        sendButton.addActionListener(e -> new AdminSendNotificationDialog(adminID));
        trackButton.addActionListener(e -> {
            try {
                new AdminTrackNotificationsDialog();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to open tracking window.");
            }
        });

        deleteButton.addActionListener(e -> new AdminDeleteScheduledNotificationDialog());

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }
}

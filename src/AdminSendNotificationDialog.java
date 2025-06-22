
import java.awt.*;
import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AdminSendNotificationDialog extends JDialog {

    private JTextField titleField;
    private JTextArea messageArea;
    private JComboBox<String> groupBox;
    private JSpinner dateSpinner;
    private int adminID;

    public AdminSendNotificationDialog(int adminID) {
        this.adminID = adminID;
        setTitle("Send Notification");
        setSize(550, 450);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Create Notification", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(new EmptyBorder(20, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleField = new JTextField();
        messageArea = new JTextArea(4, 20);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        groupBox = new JComboBox<>(new String[]{"student", "alumni", "both"});
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm"));

        int row = 0;

        addRow(formPanel, gbc, row++, "Title:", titleField);
        addRow(formPanel, gbc, row++, "Message:", messageScroll);
        addRow(formPanel, gbc, row++, "Recipient Group:", groupBox);
        addRow(formPanel, gbc, row++, "Schedule Date/Time:", dateSpinner);

        // Send button
        JButton sendBtn = new JButton("Send Notification");
        styleButton(sendBtn);
        sendBtn.addActionListener(e -> saveNotification());

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(sendBtn, gbc);

        add(formPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 40));
    }

    private void saveNotification() {
        String title = titleField.getText().trim();
        String message = messageArea.getText().trim();
        Date selectedDate = (Date) dateSpinner.getValue();
        Timestamp scheduledAt = new Timestamp(selectedDate.getTime());
        Date eventDate = new Date(selectedDate.getTime());

        String group = groupBox.getSelectedItem().toString();

        if (title.isEmpty() || message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and message cannot be empty.");
            return;
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Notification (userID, title, message, eventDate, scheduledAt, recipientGroup, sent) VALUES (?, ?, ?, ?, ?, ?, false)"
        )) {

            stmt.setInt(1, adminID);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.setDate(4, new java.sql.Date(eventDate.getTime()));
            stmt.setTimestamp(5, scheduledAt);
            stmt.setString(6, group);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Notification scheduled successfully!");
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving notification.");
        }
            
    }
}

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class DonationForm extends JFrame {

    private JTextField phoneText;
    private JTextField nameText;

    public DonationForm(Integer userID) {
        setTitle("Donation Form");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Don't exit app on close

        JPanel panel = new JPanel();
        add(panel);

        placeComponents(panel, userID);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void placeComponents(JPanel panel, Integer userID) {
        panel.setLayout(null);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameText = new JTextField(20);
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        nameText.setBounds(100, 20, 165, 25);
        panel.add(nameText);

        // Phone
        JLabel phoneLabel = new JLabel("Phone No:");
        phoneLabel.setBounds(10, 60, 120, 25);
        panel.add(phoneLabel);

        phoneText = new JTextField();
        phoneText.setBounds(100, 60, 165, 25);
        panel.add(phoneText);

        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(10, 100, 80, 25);
        panel.add(amountLabel);

        JTextField amountText = new JTextField(20);
        amountText.setBounds(100, 100, 165, 25);
        panel.add(amountText);

        // Cause Dropdown
        JLabel causeLabel = new JLabel("Cause:");
        causeLabel.setBounds(10, 140, 80, 25);
        panel.add(causeLabel);

        String[] causes = {"Scholarship Fund", "Facilities Upgrade"};
        JComboBox<String> causeDropdown = new JComboBox<>(causes);
        causeDropdown.setBounds(100, 140, 165, 25);
        panel.add(causeDropdown);

        // Payment Method
        JLabel paymentLabel = new JLabel("Payment:");
        paymentLabel.setBounds(10, 180, 80, 25);
        panel.add(paymentLabel);

        String[] paymentMethods = {"Credit/Debit Card", "Bank Transfer", "E-Wallet"};
        JComboBox<String> paymentDropdown = new JComboBox<>(paymentMethods);
        paymentDropdown.setBounds(100, 180, 165, 25);
        panel.add(paymentDropdown);

        // Anonymity Checkbox
        JCheckBox anonymousCheckBox = new JCheckBox("Donate Anonymously");
        anonymousCheckBox.setBounds(100, 220, 200, 25);

        if (userID == null) {
            panel.add(anonymousCheckBox);  // Only add if user is a guest

            anonymousCheckBox.addItemListener(e -> {
                boolean selected = anonymousCheckBox.isSelected();
                nameText.setEnabled(!selected);
                if (selected) {
                    nameText.setText("Anonymous");
                } else {
                    nameText.setText("");
                }
            });
        }

        // Submit Button
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100, 260, 100, 25);
        panel.add(submitButton);

        // Action Listener
        submitButton.addActionListener(e -> {
            String name = nameText.getText();
            String amountStr = amountText.getText();
            String cause = (String) causeDropdown.getSelectedItem();
            String paymentMethod = (String) paymentDropdown.getSelectedItem();
            boolean isAnonymous = anonymousCheckBox.isSelected();

            if (isAnonymous) {
                name = "Anonymous";
            }

            if (amountStr.isEmpty() || cause == null || paymentMethod == null) {
                JOptionPane.showMessageDialog(panel, "Please fill in all required fields.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amountValue;
            try {
                amountValue = Double.parseDouble(amountStr);
                if (amountValue <= 0) {
                    JOptionPane.showMessageDialog(panel, "Amount must be greater than 0.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter a valid number for amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                String reference = java.util.UUID.randomUUID().toString();

                DonationDAO dao = new DonationDAO();

                Integer guestID = null;

                // Insert guest only if user is not logged in
                if (userID == null) {
                    String[] nameParts = name.split(" ", 2);
                    String firstName = nameParts.length > 0 ? nameParts[0] : " ";
                    String lastName = nameParts.length > 1 ? nameParts[1] : " ";

                    Long phoneNumber = null;
                    try {
                        String phoneStr = phoneText.getText();
                        if (!phoneStr.isEmpty()) {
                            phoneNumber = Long.parseLong(phoneStr);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panel, "Invalid Phone Number.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    guestID = dao.insertGuest(firstName, lastName, phoneNumber);
                } else {
                    guestID = null; // Not a guest
                };

                // Save donation
                dao.saveDonation(userID, guestID, amountValue, currentDate, paymentMethod, reference);

                System.out.println("=== Donation Submitted ===");
                System.out.println("Name: " + name);
                System.out.println("Amount: RM" + String.format("%.2f", amountValue));
                System.out.println("Cause: " + cause);
                System.out.println("Payment Method: " + paymentMethod);
                System.out.println("Anonymous: " + isAnonymous);

                JOptionPane.showMessageDialog(panel, "Thank you for your donation!");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error saving donation: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DonationHistory extends JFrame {

    private int userID;
    private String userType;
    private Integer guestID; // nullable
    private DonationDAO dao;

    private JTable donationTable;
    private DefaultTableModel tableModel;
    private JButton backButton;

    public DonationHistory(int userID, String userType) {
        this(userID, userType, null);
    }

    public DonationHistory(int userID, String userType, Integer guestID) {
        this.userID = userID;
        this.userType = userType;
        this.guestID = guestID;
        dao = new DonationDAO();

        setTitle("Donation History");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Table setup with columns
        String[] columnNames = {"Donation ID", "Amount", "Date", "Payment Method", "Reference"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // no editing in table
            }
        };
        donationTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(donationTable);

        add(scrollPane, BorderLayout.CENTER);

        // Back button panel
        JPanel buttonPanel = new JPanel();
        backButton = new JButton("Back");
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose(); // close DonationHistory
        });

        loadDonationHistory();
    }

    private void loadDonationHistory() {
        List<DonationDAO.DonationRecord> donations = dao.getDonationsByUserID(userID, guestID);
        tableModel.setRowCount(0); // clear existing rows

        for (DonationDAO.DonationRecord d : donations) {
            tableModel.addRow(new Object[]{
                    d.donationID,
                    String.format("%.2f", d.amount),
                    d.date.toString(),
                    d.paymentMethod,
                    d.reference
            });
        }

        if (donations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No donation records found for this user.", "No Records", JOptionPane.INFORMATION_MESSAGE);
        }
    }


}

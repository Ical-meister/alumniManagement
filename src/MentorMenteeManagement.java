import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MentorMenteeManagement extends JFrame {
    JComboBox<String> mentorList;
    JButton requestMentorShipBtn, viewMentorshipsBtn, backButton;
    JTable mentorshipTable;
    DefaultTableModel tableModel;

    public MentorMenteeManagement(int studentID) {
        setTitle("Mentorship Program");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel with title and back button
        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            // Handle return to Student Dashboard (placeholder)
            JOptionPane.showMessageDialog(this, "Returning to Student Page...");
            dispose(); // Close this window
        });

        JLabel titleLabel = new JLabel("Mentorship Program", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(18.0f));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Control Panel at bottom
        JPanel controlPanel = new JPanel(new FlowLayout());
        mentorList = new JComboBox<>();
        loadMentors();

        requestMentorShipBtn = new JButton("Request Mentorship");
        viewMentorshipsBtn = new JButton("View Mentorships");

        controlPanel.add(new JLabel("Select Mentor: "));
        controlPanel.add(mentorList);
        controlPanel.add(requestMentorShipBtn);
        controlPanel.add(viewMentorshipsBtn);
        add(controlPanel, BorderLayout.SOUTH);

        // Table to show mentorships
        String[] columnNames = {"Alumni ID", "Field of Interest", "Status", "Requested Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        mentorshipTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(mentorshipTable);
        add(scrollPane, BorderLayout.CENTER);

        loadMentorshipRequests(studentID);

        // Actions
        requestMentorShipBtn.addActionListener(e -> {
            requestMentorship(studentID);
            loadMentorshipRequests(studentID);
        });

        viewMentorshipsBtn.addActionListener(e -> new MentorshipViewer(studentID));

        setVisible(true);
    }

    private void loadMentors() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT alumniID, fieldOfInterest FROM Mentorship WHERE status = 'approved'")) {

            mentorList.removeAllItems();
            while (rs.next()) {
                mentorList.addItem("ID:" + rs.getInt("alumniID") + " - " + rs.getString("fieldOfInterest"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMentorshipRequests(int studentID) {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT alumniID, fieldOfInterest, status, requestedDate FROM Mentorship WHERE studentID = ?")) {
            pstmt.setInt(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("alumniID"),
                    rs.getString("fieldOfInterest"),
                    rs.getString("status"),
                    rs.getDate("requestedDate")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void requestMentorship(int studentID) {
        String selected = (String) mentorList.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a mentor first.");
            return;
        }

        int alumniID;
        try {
            alumniID = Integer.parseInt(selected.split(" ")[0].split(":")[1]);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid mentor selection.");
            return;
        }

        // Get field of interest for the selected alumni
        String fieldOfInterest = "";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT fieldOfInterest FROM Mentorship WHERE alumniID = ? LIMIT 1")) {
            pstmt.setInt(1, alumniID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                fieldOfInterest = rs.getString("fieldOfInterest");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Insert mentorship request
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO Mentorship (studentID, alumniID, status, requestedDate, fieldOfInterest) VALUES (?, ?, 'pending', CURDATE(), ?)")) {

            pstmt.setInt(1, studentID);
            pstmt.setInt(2, alumniID);
            pstmt.setString(3, fieldOfInterest);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Mentorship request sent.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send mentorship request.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MentorMenteeManagement(1004);
            new MentorMenteeManagement(1005);
            new MentorMenteeManagement(1007);
        });
    }
}

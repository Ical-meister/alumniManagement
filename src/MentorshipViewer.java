import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MentorshipViewer extends JFrame {
    private JTable mentorshipTable;
    private DefaultTableModel tableModel;

    public MentorshipViewer(int userID) {
        setTitle("Mentorship Viewer");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top Panel with Back Button and Title
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Back");
        JLabel titleLabel = new JLabel("Mentorship List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table Model and JTable
        tableModel = new DefaultTableModel(new String[]{"ID", "Status", "Field", "Responded Date"}, 0);
        mentorshipTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(mentorshipTable);

        // Load data
        loadMentorships(userID);

        // Back button action
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // close current window
                SwingUtilities.invokeLater(() -> new MentorMenteeManagement(1004));
                SwingUtilities.invokeLater(() -> new MentorMenteeManagement(1005));
                SwingUtilities.invokeLater(() -> new MentorMenteeManagement(1007));
                SwingUtilities.invokeLater(() -> new MentorshipAdminDashboard(1001, null));
            }
        });


        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setSize(600, 400);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }


   private void loadMentorships(int studentID) {
    String query = "SELECT mentorshipID, status, fieldOfInterest, respondedDate FROM Mentorship WHERE studentID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setInt(1, studentID);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Object[] row = {
                rs.getInt("mentorshipID"),
                rs.getString("status"),
                rs.getString("fieldOfInterest"),
                rs.getDate("respondedDate")
            };
            tableModel.addRow(row);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading mentorships: " + e.getMessage());
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MentorshipViewer(1004));
        SwingUtilities.invokeLater(() -> new MentorshipViewer(1005));
        SwingUtilities.invokeLater(() -> new MentorshipViewer(1007));
        SwingUtilities.invokeLater(() -> new MentorshipAdminDashboard(1001, null)); 


    }
}
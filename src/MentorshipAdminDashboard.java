import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class MentorshipAdminDashboard extends JFrame {
    int alumniID;
    JFrame adminFrame;
    private JPanel buttonPanel;

    public MentorshipAdminDashboard(int adminID, JFrame parentFrame) {
        this.alumniID = alumniID;
        this.adminFrame = adminFrame;

        setTitle("Mentorship Program Management");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        

        // Big Header
        JLabel header = new JLabel("Mentorship Program Management", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 28));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Button labels
        String[] buttonLabels = {
            "View Mentorship",
            "Mentorship Requests",
            "Pending Mentorship Requests",
            "Back"
        };

        // Button panel with 4 rows, 1 column, vertical gap 20px
        JPanel buttonPanel = new JPanel(new GridLayout(buttonLabels.length, 1, 0, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Create buttons and add to panel
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Tahoma", Font.PLAIN, 16));
            button.setPreferredSize(new Dimension(300, 80)); 
            button.setFocusPainted(false);
            button.setBackground(new Color(70, 130, 180));  // Steel blue
            button.setForeground(Color.WHITE);

            // Attach action listeners based on button label
            button.addActionListener(e -> {
                switch (label) {
                    case "View Mentorship":
                        showMentorshipDetailsWindow();
                        break;
                    case "Mentorship Requests":
                        showMentorshipRequestsWindow();
                        break;
                    case "Pending Mentorship Requests":
                        showPendingRequestsWindow();
                        break;
                    case "Back":
                        // Close current window and open admin home or exit
                        dispose();
                        new AdminPage(adminID);
                        break;
                }
            });

            buttonPanel.add(button);
        }

        // Layout for main frame
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }
    

    private void showMentorshipDetailsWindow() {
    JComboBox<String> studentComboBox = new JComboBox<>();
    java.util.Map<String, Integer> studentMap = new java.util.HashMap<>();

    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT userID, firstName, lastName FROM User WHERE role = 'student'")) {

        while (rs.next()) {
            int id = rs.getInt("userID");
            String name1= rs.getString("firstName");
            String name2=rs.getString("lastName");
            String label = name1 + name2 + " (ID: " + id + ")";
            studentComboBox.addItem(label);
            studentMap.put(label, id);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load students.");
        return;
    }

    int result = JOptionPane.showConfirmDialog(this, studentComboBox,
        "Select Student to View Mentorships", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
        String selectedLabel = (String) studentComboBox.getSelectedItem();
        if (selectedLabel != null && studentMap.containsKey(selectedLabel)) {
            int selectedStudentID = studentMap.get(selectedLabel);
            // Open MentorshipViewer window with selected student ID
            SwingUtilities.invokeLater(() -> new MentorshipViewer(selectedStudentID));
        }
    }
}


    private void showMentorshipRequestsWindow() {
    JFrame frame = new JFrame("Mentorship Requests");
    frame.setSize(800, 500);
    frame.setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

    // Top panel with student ID dropdown and button
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel studentLabel = new JLabel("Select Student ID:");
    JComboBox<Integer> studentComboBox = new JComboBox<>();
    JButton openPanelBtn = new JButton("Open Mentorship Panel");

    topPanel.add(studentLabel);
    topPanel.add(studentComboBox);
    topPanel.add(openPanelBtn);
    mainPanel.add(topPanel, BorderLayout.NORTH);

    // Table to show mentorship requests (read-only for admin viewing)
    DefaultTableModel model = new DefaultTableModel(new String[]{
            "Request ID", "Student Name", "Requested Date", "Student ID", "Status"
    }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // All columns non-editable
        }
    };

    JTable table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("""
                 SELECT m.mentorshipID, u.firstName, u.lastName, m.requestedDate, m.studentID, m.status
                 FROM Mentorship m
                 JOIN User u ON m.studentID = u.userID
                 ORDER BY m.status DESC
                 """)) {

        while (rs.next()) {
            int studentID = rs.getInt("studentID");
            String fullName = rs.getString("firstName") + " " + rs.getString("lastName");

            // Populate combo box (only add if not already in combo)
            if (((DefaultComboBoxModel<Integer>) studentComboBox.getModel()).getIndexOf(studentID) == -1) {
                studentComboBox.addItem(studentID);
            }

            model.addRow(new Object[]{
                    rs.getInt("mentorshipID"),
                    fullName,
                    rs.getDate("requestedDate"),
                    studentID,
                    rs.getString("status")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Button action to open mentorship panel
    openPanelBtn.addActionListener(e -> {
        Integer selectedID = (Integer) studentComboBox.getSelectedItem();
        if (selectedID != null) {
            JFrame mentorFrame = new JFrame("Mentorship Panel for Student ID: " + selectedID);
            mentorFrame.setContentPane(new MentorMenteeManagement(selectedID));
            mentorFrame.setSize(700, 500);
            mentorFrame.setLocationRelativeTo(null);
            mentorFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a student ID.");
        }
    });

    frame.setContentPane(mainPanel);
    frame.setVisible(true); 
    }

    private void showPendingRequestsWindow() {
    int alumniID = 1002;

    
    MentorshipAlumniDashboard alumniDashboard = new MentorshipAlumniDashboard(alumniID, this);
    this.setVisible(false); // Hide admin window
    }

   private void updateMentorshipStatus(int mentorshipID, String newStatus) {
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(
             "UPDATE Mentorship SET status = ? WHERE mentorshipID = ?"
         )) {
        pstmt.setString(1, newStatus);
        pstmt.setInt(2, mentorshipID);
        pstmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Status updated to: " + newStatus);
        dispose();
        new MentorshipAdminDashboard(1001, null);
    } catch (SQLException e) {
        e.printStackTrace();
    }


    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MentorshipAdminDashboard(1001,null));
    }
}

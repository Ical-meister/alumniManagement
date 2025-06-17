import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class MentorshipAlumniDashboard extends JFrame {
    DefaultTableModel tableModel;
    JTable mentorshipTable;
    int adminID;
    int alumniID;
    JFrame adminFrame;
    private int currentAlumniID;

    public MentorshipAlumniDashboard(int alumniID, JFrame adminFrame) {
        this.alumniID = alumniID;
        this.adminFrame = adminFrame;
        this.currentAlumniID = alumniID;
        

        setTitle("Alumni - Pending Mentorship Requests");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false); // Set to true if you want to remove window title bar
        setLocationRelativeTo(null);

        // ======= Top Panel (Back button + Title) =======
        JPanel topPanel = new JPanel(new BorderLayout());

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(80, 5));

        backButton.addActionListener(e -> {
            dispose();
            if (this.adminFrame != null) {
                this.adminFrame.setVisible(true);
            } else {
                // fallback if adminFrame is null
                new AlumniPage(alumniID).setVisible(true);
            }
        });
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Pending Mentorship Requests", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        this.add(topPanel, BorderLayout.NORTH);

        // ======= Table Setup =======
        tableModel = new DefaultTableModel(new Object[]{"Mentorship ID", "Student ID", "Student Name"}, 0);
        mentorshipTable = new JTable(tableModel);
        mentorshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(mentorshipTable);
        add(scrollPane, BorderLayout.CENTER);

        // ======= Side Buttons =======
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton acceptButton = new JButton("Accept");
        JButton declineButton = new JButton("Decline");
        JButton scheduleButton = new JButton("Schedule Meeting");
        JButton viewMeetingSlotButton = new JButton("View Meeting Slot");

        acceptButton.setMaximumSize(new Dimension(150, 30));
        declineButton.setMaximumSize(new Dimension(150, 30));
        scheduleButton.setMaximumSize(new Dimension(150, 30));
        viewMeetingSlotButton.setMaximumSize(new Dimension(150, 30));

        buttonPanel.add(acceptButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(declineButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(scheduleButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(viewMeetingSlotButton);

        add(buttonPanel, BorderLayout.EAST);

        // ======= Button Actions =======
        acceptButton.addActionListener(e -> handleRequest("approved"));
        declineButton.addActionListener(e -> handleRequest("declined"));
        scheduleButton.addActionListener(e -> openScheduleDialog());
        viewMeetingSlotButton.addActionListener(e -> openMeetingSlot());


        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Load table data
        loadRequests();
    }

    private void loadRequests() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT m.mentorshipID, m.studentID, CONCAT(u.firstName, ' ', u.lastName) AS studentName " +
                     "FROM Mentorship m " +
                     "JOIN Student s ON m.studentID = s.userID " +
                     "JOIN User u ON s.userID = u.userID " +
                     "WHERE m.alumniID = ? AND m.status = 'pending'")) {

            stmt.setInt(1, this.alumniID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("mentorshipID"),
                            rs.getInt("studentID"),
                            rs.getString("studentName")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(String status) {
        int selectedRow = mentorshipTable.getSelectedRow();
        if (selectedRow != -1) {
            int mentorshipID = (Integer) tableModel.getValueAt(selectedRow, 0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE Mentorship SET status = ?, respondDate = CURDATE() WHERE mentorshipID = ?")) {

                stmt.setString(1, status);
                stmt.setInt(2, mentorshipID);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Request " + status + "ed.");
                loadRequests();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void openScheduleDialog() {
        int selectedRow = mentorshipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to schedule.");
            return;
        }

        int mentorshipID = (Integer) tableModel.getValueAt(selectedRow, 0);

        JDialog scheduleDialog = new JDialog(this, "Schedule Meeting", true);
        scheduleDialog.setSize(400, 200);
        scheduleDialog.setLayout(new BorderLayout());

        DatePicker datePicker = new DatePicker();
        TimePicker timePicker = new TimePicker();

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Select Date:"));
        inputPanel.add(datePicker);
        inputPanel.add(new JLabel("Select Time:"));
        inputPanel.add(timePicker);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            LocalDate date = datePicker.getDate();
            LocalTime time = timePicker.getTime();

            if (date == null || time == null) {
                JOptionPane.showMessageDialog(this, "Please select both date and time.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false); // Start transaction

                // Insert into MeetingSlot
                PreparedStatement insertSlot = conn.prepareStatement(
                    "INSERT INTO MeetingSlot (mentorshipID, meetingDate, meetingTime, status) VALUES (?, ?, ?, ?)");
                insertSlot.setInt(1, mentorshipID);
                insertSlot.setDate(2, java.sql.Date.valueOf(date));
                insertSlot.setTime(3, java.sql.Time.valueOf(time));
                insertSlot.setString(4, "scheduled");
                insertSlot.executeUpdate();

                // Update Mentorship table
                PreparedStatement updateMentorship = conn.prepareStatement(
                "UPDATE Mentorship SET respondedDate = ?, status = ? WHERE mentorshipID = ?");
                updateMentorship.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                updateMentorship.setString(2, "approved");
                updateMentorship.setInt(3, mentorshipID);
                updateMentorship.executeUpdate();

                conn.commit(); // Commit both actions together

                JOptionPane.showMessageDialog(this, "Meeting scheduled successfully!");
                scheduleDialog.dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error scheduling meeting.");
            }

        });

        scheduleDialog.add(inputPanel, BorderLayout.CENTER);
        scheduleDialog.add(confirmButton, BorderLayout.SOUTH);
        scheduleDialog.setLocationRelativeTo(this);
        scheduleDialog.setVisible(true);
    }

    private void openMeetingSlot() {
    JFrame meetingFrame = new JFrame("Meeting Slot");
    meetingFrame.setSize(900, 600);
    meetingFrame.setLocationRelativeTo(null);
    meetingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    meetingFrame.setLayout(new BorderLayout());

    // Title Label
    JLabel titleLabel = new JLabel("Meeting Slot", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
    meetingFrame.add(titleLabel, BorderLayout.NORTH);

    // Table Model
    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);
    model.addColumn("Meeting Slot ID");
    model.addColumn("Mentorship ID");
    model.addColumn("Meeting Date");
    model.addColumn("Meeting Time");
    model.addColumn("Status");

    try (Connection conn = DBConnection.getConnection()) {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT meetingSlotID, mentorshipID, meetingDate, meetingTime, status FROM MeetingSlot WHERE mentorshipID IN " +
            "(SELECT mentorshipID FROM Mentorship WHERE alumniID = ?)"
        );
        stmt.setInt(1, currentAlumniID); // Replace with actual alumniID
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            model.addRow(new Object[] {
                rs.getInt("meetingSlotID"),
                rs.getInt("mentorshipID"),
                rs.getDate("meetingDate"),
                rs.getTime("meetingTime"),
                rs.getString("status")
            });
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to load meeting slots.");
    }

    // Scroll Pane for Table
    JScrollPane scrollPane = new JScrollPane(table);
    meetingFrame.add(scrollPane, BorderLayout.CENTER);

    // Back Button Panel
    JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton backButton = new JButton("Back");
    topLeftPanel.add(backButton);
    meetingFrame.add(topLeftPanel, BorderLayout.PAGE_START);

    backButton.addActionListener(e -> {
            dispose();

            new AlumniPage(alumniID);
            /*if (this.adminFrame != null) {
                this.adminFrame.setVisible(false);
            }*/
        });

    meetingFrame.setVisible(true);
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MentorshipAlumniDashboard(1002, null));
    }
}

import java.awt.*;
import javax.swing.*;

public class StudentManageProfilesPanel extends JFrame {

    private int studentID;

    public StudentManageProfilesPanel(int studentID) {
        this.studentID = studentID;
        setTitle("Manage Profile");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Manage Profile", SwingConstants.CENTER);
        header.setFont(new Font("Verdana", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JButton editBtn = new JButton("Edit My Profile");
        JButton viewBtn = new JButton("View Profile");

        styleButton(editBtn);
        styleButton(viewBtn);

        buttonPanel.add(editBtn);
        buttonPanel.add(viewBtn);

        add(buttonPanel, BorderLayout.CENTER);

        editBtn.addActionListener(e -> new StudentEditProfileDialog(studentID));
        viewBtn.addActionListener(e -> new StudentViewProfileDialog(studentID));

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Tahoma", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
    }
}

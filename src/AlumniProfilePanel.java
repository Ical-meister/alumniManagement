import java.awt.*;
import javax.swing.*;

public class AlumniProfilePanel extends UserProfilePanel {

    public AlumniProfilePanel(int userID) {
        super(userID);
        setTitle("Alumni - Manage Profile");
        setSize(500, 300);
        showMainMenu();
        setVisible(true);
    }

    @Override
    protected void showMainMenu() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton editBtn = new JButton("Edit Own Profile");
        JButton viewBtn = new JButton("View Profile");

        styleButton(editBtn);
        styleButton(viewBtn);

        editBtn.addActionListener(e -> openEditProfile());
        viewBtn.addActionListener(e -> openViewProfile());

        panel.add(editBtn);
        panel.add(viewBtn);
        add(panel);
    }

    @Override
    protected void openEditProfile() {
        new AlumniEditProfileDialog(userID);
    }

    @Override
    protected void openViewProfile() {
        new AlumniViewProfileDialog(userID);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
    }
}

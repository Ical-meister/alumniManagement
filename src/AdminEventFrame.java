import javax.swing.*;

public class AdminEventFrame extends JFrame {

    public AdminEventFrame(int adminID) {
        setTitle("Admin - Event Management");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        AdminEventPanel panel = new AdminEventPanel(); // Your JPanel
        add(panel);

        setVisible(true);
    }
}

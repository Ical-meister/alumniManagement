import javax.swing.*;
import java.awt.*;

public class EventManagementUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Event Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 750);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Admin and User views
        AdminEventPanel adminPanel = new AdminEventPanel();
        UserEventPanel userPanel = new UserEventPanel();

        tabbedPane.addTab("Admin Panel", adminPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);


    }
}

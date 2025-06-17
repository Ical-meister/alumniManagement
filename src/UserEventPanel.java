import javax.swing.*;
import java.awt.*;

public class UserEventPanel extends JPanel {
    private JTable eventTable;
    private JButton registerButton;

    public UserEventPanel() {
        setLayout(new BorderLayout());

        // Dummy table for now L0L
        String[] columns = {"Event ID", "Title", "Location", "Capacity", "Registered"};
        Object[][] data = {
                {"1", "Annual Meetup", "Hall A", "100", "50"},
                {"2", "Tech Talk", "Room 101", "60", "59"}
        };

        eventTable = new JTable(data, columns);
        registerButton = new JButton("Register for Selected Event");

        add(new JScrollPane(eventTable), BorderLayout.CENTER);
        add(registerButton, BorderLayout.SOUTH);

        // Need to add registration handling, limit checking and date validation.
    }
}

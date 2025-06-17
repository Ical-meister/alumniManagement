import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.Date;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminEventPanel extends JPanel {

    private JTextField titleField, locationField, capacityField, dateField, eventIDField;
    private JTextArea descriptionArea;
    private JButton createButton, updateButton, cancelButton, clearSelectionButton;

    private JTable eventTable;
    private DefaultTableModel tableModel;

    private EventDAO dao;

    public AdminEventPanel() {
        dao = new EventDAO();

        setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"Event ID", "Title"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // disable cell editing
            }
        };
        eventTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(eventTable);

        loadEventsToTable();

        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = eventTable.getSelectedRow();
                if (row != -1) {
                    int selectedEventID = (int) tableModel.getValueAt(row, 0);
                    loadEventDetails(selectedEventID);
                }
            }
        });

        add(tableScrollPane, BorderLayout.NORTH);

        // Form setup
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        titleField = new JTextField();
        locationField = new JTextField();
        capacityField = new JTextField();
        descriptionArea = new JTextArea(3, 20);
        dateField = new JTextField(); // for event date input as String

        eventIDField = new JTextField();
        eventIDField.setEditable(true); // Initially editable for new events
        formPanel.add(new JLabel("Event ID:"));
        formPanel.add(eventIDField);

        formPanel.add(new JLabel("Event Title:"));
        formPanel.add(titleField);

        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);

        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);

        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descriptionArea));

        formPanel.add(new JLabel("Event Date (yyyy-MM-dd):"));
        formPanel.add(dateField);

        // Buttons
        createButton = new JButton("Create Event");
        updateButton = new JButton("Update Event");
        cancelButton = new JButton("Cancel Event");
        clearSelectionButton = new JButton("Clear Selection");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(clearSelectionButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Create button listener
        createButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String location = locationField.getText().trim();
            String capacityStr = capacityField.getText().trim();
            String description = descriptionArea.getText().trim();
            String dateStr = dateField.getText().trim();

            if (title.isEmpty() || location.isEmpty() || capacityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.", "Invalid Capacity", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity must be a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse event date
            java.sql.Date eventDate;
            try {
                java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                eventDate = new java.sql.Date(utilDate.getTime());

                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                if (eventDate.before(today)) {
                    JOptionPane.showMessageDialog(this, "Event date cannot be in the past.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "Date must be in yyyy-MM-dd format.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String createdBy = "Admin";
            java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());

            boolean success = dao.createEvent(title, description, eventDate, location, createdBy, capacity, createdAt);

            if (success) {
                JOptionPane.showMessageDialog(this, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEventsToTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create event.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update button listener
        updateButton.addActionListener(e -> {
            String eventIDStr = eventIDField.getText().trim();
            String title = titleField.getText().trim();
            String location = locationField.getText().trim();
            String capacityStr = capacityField.getText().trim();
            String description = descriptionArea.getText().trim();
            String dateStr = dateField.getText().trim();

            if (eventIDStr.isEmpty() || title.isEmpty() || location.isEmpty() || capacityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int eventID;
            int capacity;
            java.sql.Date eventDate;
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            try {
                eventID = Integer.parseInt(eventIDStr);
                capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.", "Invalid Capacity", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                eventDate = new java.sql.Date(utilDate.getTime());

                if (eventDate.before(today)) {
                    JOptionPane.showMessageDialog(this, "Event date cannot be in the past.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } catch (NumberFormatException | ParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values and date format (yyyy-MM-dd).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = dao.updateEvent(eventID, title, description, eventDate, location, capacity);

            if (success) {
                JOptionPane.showMessageDialog(this, "Event updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEventsToTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update event. Make sure the event ID exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button listener
        cancelButton.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event from the table to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int eventID = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel event ID " + eventID + "?", "Confirm Cancel", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success = dao.cancelEvent(eventID);
            if (success) {
                JOptionPane.showMessageDialog(this, "Event cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEventsToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel event. Check if the Event ID exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear selection button listener
        clearSelectionButton.addActionListener(e -> {
            eventTable.clearSelection();
            clearEventFields();
        });
    }

    private void loadEventsToTable() {
        List<EventDAO.EventSummary> events = dao.getActiveEvents();

        tableModel.setRowCount(0); // clear existing rows

        for (EventDAO.EventSummary event : events) {
            tableModel.addRow(new Object[]{event.eventID, event.title});
        }
    }

    private void loadEventDetails(int eventID) {
        EventDAO.Event event = dao.getEventById(eventID);
        if (event != null) {
            eventIDField.setText(String.valueOf(event.eventID));
            titleField.setText(event.title);
            locationField.setText(event.location);
            capacityField.setText(String.valueOf(event.capacity));
            descriptionArea.setText(event.description);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(event.eventDate);
            dateField.setText(dateStr);

            eventIDField.setEditable(false);
        }
    }

    private void clearForm() {
        eventIDField.setText("");
        titleField.setText("");
        locationField.setText("");
        capacityField.setText("");
        descriptionArea.setText("");
        dateField.setText("");
        eventIDField.setEditable(true);
    }

    private void clearEventFields() {
        eventIDField.setText("");
        titleField.setText("");
        locationField.setText("");
        capacityField.setText("");
        descriptionArea.setText("");
        dateField.setText("");
        eventIDField.setEditable(true);
    }
}

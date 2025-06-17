import java.awt.*;
import javax.swing.*;

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int selectedRow;
    private JTable table;
    private MentorshipStatusHandler statusHandler;

    // At the top of ButtonEditor.java
    public interface MentorshipStatusHandler {
        void handleStatusChange(int mentorshipID, String newStatus);
    }


    public ButtonEditor(JCheckBox checkBox, MentorshipStatusHandler handler) {
        super(checkBox);
        this.statusHandler = handler;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        this.selectedRow = row;
        label = (value == null) ? "Approve / Decline" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int mentorshipID = (int) table.getValueAt(selectedRow, 0);
            String[] options = {"Approve", "Decline"};
            int result = JOptionPane.showOptionDialog(button, "Update mentorship status:",
                    "Action", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            if (result == 0) {
                statusHandler.handleStatusChange(mentorshipID, "Approved");
            } else if (result == 1) {
                statusHandler.handleStatusChange(mentorshipID, "Declined");
            }
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }


}

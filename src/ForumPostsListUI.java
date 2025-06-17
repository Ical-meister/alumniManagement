import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ForumPostsListUI extends JFrame {
    private JTable postsTable;
    private DefaultTableModel tableModel;
    private ForumPostDAO dao;
    private JButton openThreadButton;

    public ForumPostsListUI() {
        dao = new ForumPostDAO();

        setTitle("All Forum Posts");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Define columns you want to show
        String[] columnNames = {"Post ID", "Thread ID", "User ID", "Created By", "Content", "Timestamp"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // make table non-editable
            }
        };

        postsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(postsTable);

        add(scrollPane, BorderLayout.CENTER);

        loadPosts();
        setVisible(true);
        // Add button panel at bottom
        JPanel buttonPanel = new JPanel();
        openThreadButton = new JButton("Open Selected Thread");
        buttonPanel.add(openThreadButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add button action
        openThreadButton.addActionListener(e -> openSelectedThread());
    }

    private void loadPosts() {
        List<ForumPost> posts = dao.getAllPosts();
        tableModel.setRowCount(0); // clear previous rows

        for (ForumPost post : posts) {
            tableModel.addRow(new Object[]{
                    post.getPostId(),
                    post.getThreadId(),
                    post.getUserId(),
                    post.getCreatedBy(),
                    post.getContent(),
                    post.getTimestamp()
            });
        }

        if(posts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No forum posts found.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForumPostsListUI());
    }

    private void openSelectedThread() {
        int selectedRow = postsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a post first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get threadId from selected row (column index 1, based on table model)
        int threadId = (int) tableModel.getValueAt(selectedRow, 1);

        // Open ForumThreadUI with that threadId
        SwingUtilities.invokeLater(() -> {
            new ForumThreadUI(threadId, 0,"Tom");
        });
    }

}

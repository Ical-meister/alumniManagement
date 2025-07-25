import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.sql.Timestamp;
import java.util.Date;

public class ForumThreadUI extends JFrame {

    private int threadId;
    private int currentUserId;      // You must set this (current logged in user)
    private String currentUsername; // Same here (for createdBy field)
    private JTextArea replyArea;
    private JButton postButton;
    private JPanel postsPanel;
    private JScrollPane postsScrollPane;

    private ForumPostDAO postDAO;

    public ForumThreadUI(int threadId, int currentUserId, String currentUsername) {
        this.threadId = threadId;
        this.currentUserId = currentUserId;
        this.currentUsername = currentUsername;
        postDAO = new ForumPostDAO();

        setTitle("Discussion Forum - Thread " + threadId);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Reply panel on top
        JPanel replyPanel = new JPanel(new BorderLayout(5, 5));
        replyArea = new JTextArea(4, 50);
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);
        postButton = new JButton("Post Reply");

        replyPanel.add(new JScrollPane(replyArea), BorderLayout.CENTER);
        replyPanel.add(postButton, BorderLayout.EAST);

        add(replyPanel, BorderLayout.NORTH);

        // Posts panel in center inside scroll pane
        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsScrollPane = new JScrollPane(postsPanel);
        add(postsScrollPane, BorderLayout.CENTER);

        // Post button action
        postButton.addActionListener(e -> postReply());

        // Load posts from DB
        loadPosts();

        setVisible(true);
    }

    private void postReply() {
        String content = replyArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reply content cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create ForumPost object (no postID and timestamp yet, DB will handle)
        Timestamp currentTimestamp = new Timestamp(new Date().getTime());

        ForumPost newPost = new ForumPost(
                0,                  // postID, will be auto-generated by DB
                threadId,
                currentUserId,
                currentUsername,    // createdBy (String)
                content,            // content (String)
                currentTimestamp               // timestamp (null if DB will set it)
        );


        boolean success = postDAO.insertPost(newPost);
        if (success) {
            replyArea.setText(""); // clear input
            loadPosts();           // reload posts from DB including new post
        } else {
            JOptionPane.showMessageDialog(this, "Failed to post reply.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPosts() {
        postsPanel.removeAll();

        List<ForumPost> posts = postDAO.getPostsByThreadId(threadId);
        if (posts == null || posts.isEmpty()) {
            postsPanel.add(new JLabel("No posts in this thread yet."));
        } else {
            for (ForumPost post : posts) {
                JPanel postPanel = new JPanel(new BorderLayout());
                postPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                postPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                JLabel userLabel = new JLabel("User: " + post.getCreatedBy() + " (ID: " + post.getUserId() + ")");
                JTextArea contentArea = new JTextArea(post.getContent());
                contentArea.setLineWrap(true);
                contentArea.setWrapStyleWord(true);
                contentArea.setEditable(false);
                contentArea.setBackground(getBackground());

                JLabel timestampLabel = new JLabel(post.getTimestamp().toString());
                timestampLabel.setFont(new Font("Serif", Font.ITALIC, 10));
                timestampLabel.setHorizontalAlignment(SwingConstants.RIGHT);

                postPanel.add(userLabel, BorderLayout.NORTH);
                postPanel.add(contentArea, BorderLayout.CENTER);
                postPanel.add(timestampLabel, BorderLayout.SOUTH);

                postsPanel.add(postPanel);
                postsPanel.add(Box.createVerticalStrut(10));
            }
        }

        postsPanel.revalidate();
        postsPanel.repaint();
    }

    // For testing only:
    public static void main(String[] args) {
        // You must provide real currentUserId and username when integrating
        SwingUtilities.invokeLater(() -> new ForumThreadUI(1, 123, "JohnDoe"));
    }
}

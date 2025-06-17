import java.sql.Timestamp;

public class ForumPost {
    private int postId;
    private int threadId;
    private int userId;
    private String createdBy;
    private String content;
    private Timestamp timestamp;

    public ForumPost(int postId, int threadId, int userId, String createdBy, String content, Timestamp timestamp) {
        this.postId = postId;
        this.threadId = threadId;
        this.userId = userId;
        this.createdBy = createdBy;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Optional: constructor for inserting new posts (no postId yet)
    public ForumPost(int threadId, int userId, String createdBy, String content, Timestamp timestamp) {
        this.threadId = threadId;
        this.userId = userId;
        this.createdBy = createdBy;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters
    public int getPostId() {
        return postId;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getUserId() {
        return userId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setters (optional if you plan to modify fields later)
    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

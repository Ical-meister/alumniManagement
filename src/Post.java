package model;

import java.util.Date;

public class Post {
    private int postId;
    private int threadId;
    private int userId;
    private String content;
    private Date timestamp;

    public Post(int postId, int threadId, int userId, String content, Date timestamp) {
        this.postId = postId;
        this.threadId = threadId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getThreadId() { return threadId; }
    public void setThreadId(int threadId) { this.threadId = threadId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}

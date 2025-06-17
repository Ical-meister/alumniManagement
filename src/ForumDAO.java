package dao;

import model.Post;
import java.sql.*;
import java.util.*;

public class ForumDAO {
    private Connection conn;

    public ForumDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Post> getPostsByThread(int threadId) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE thread_id = ? ORDER BY timestamp ASC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, threadId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Post post = new Post(
                rs.getInt("post_id"),
                rs.getInt("thread_id"),
                rs.getInt("user_id"),
                rs.getString("content"),
                rs.getTimestamp("timestamp")
            );
            posts.add(post);
        }
        return posts;
    }

    public void addPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (thread_id, user_id, content, timestamp) VALUES (?, ?, ?, NOW())";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, post.getThreadId());
        stmt.setInt(2, post.getUserId());
        stmt.setString(3, post.getContent());
        stmt.executeUpdate();
    }
}

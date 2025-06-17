import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumPostDAO {

    // Fetch posts belonging to a specific thread
    public List<ForumPost> getPostsByThreadId(int threadId) {
        List<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT * FROM Post WHERE threadID = ? ORDER BY timestamp ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threadId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ForumPost post = new ForumPost(
                        rs.getInt("postID"),
                        rs.getInt("threadID"),
                        rs.getInt("userID"),
                        rs.getString("createdBy"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp")
                );
                posts.add(post);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }

    // Insert a new post into the database
    public boolean insertPost(ForumPost post) {
        String sql = "INSERT INTO Post (threadID, userID, createdBy, content, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, post.getThreadId());
            stmt.setInt(2, post.getUserId());
            stmt.setString(3, post.getCreatedBy());
            stmt.setString(4, post.getContent());
            stmt.setTimestamp(5, post.getTimestamp());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<ForumPost> getAllPosts() {
        List<ForumPost> posts = new ArrayList<>();
        String sql = "SELECT postID, threadID, userID, createdBy, content, timestamp FROM Post ORDER BY timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ForumPost post = new ForumPost(
                        rs.getInt("postID"),
                        rs.getInt("threadID"),
                        rs.getInt("userID"),
                        rs.getString("createdBy"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp")
                );
                posts.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }

}

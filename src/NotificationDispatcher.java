import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationDispatcher {

    public static void start() {
        Timer timer = new Timer(true); // Run as daemon
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT * FROM Notification WHERE scheduledAt <= NOW() AND sent = FALSE")) {

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        System.out.println("Dispatching: " + rs.getString("title"));

                        // Mark as sent
                        try (PreparedStatement update = conn.prepareStatement(
                                "UPDATE Notification SET sent = TRUE WHERE notificationID = ?")) {
                            update.setInt(1, rs.getInt("notificationID"));
                            update.executeUpdate();
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60000); // Every 60 seconds
    }
}

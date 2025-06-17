/* This uses an updated Event table in MySQL, please update your table with this command
* CREATE TABLE Event (
    eventID INT PRIMARY KEY AUTO_INCREMENT,
    Title VARCHAR(100) NOT NULL,
    Description VARCHAR(255) NOT NULL,
    eventDate DATE NOT NULL,
    location VARCHAR(100) NOT NULL,
    createdBy VARCHAR(100) NOT NULL,
    Capacity INT NOT NULL,
    createdAt DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Active'
);
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

public class EventDAO {

    // Method to create a new event
    public boolean createEvent(String title, String description, Date eventDate, String location,
                               String createdBy, int capacity, Timestamp createdAt) {
        String sql = "INSERT INTO Event (Title, Description, eventDate, location, createdBy, Capacity, createdAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDate(3, eventDate);
            stmt.setString(4, location);
            stmt.setString(5, createdBy);
            stmt.setInt(6, capacity);
            stmt.setTimestamp(7, createdAt);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEvent(int eventID, String title, String description, Date eventDate,
                               String location, int capacity) {
        String sql = "UPDATE Event SET Title = ?, Description = ?, eventDate = ?, location = ?, Capacity = ? WHERE eventID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setDate(3, eventDate);
            stmt.setString(4, location);
            stmt.setInt(5, capacity);
            stmt.setInt(6, eventID);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelEvent(int eventID) {
        String sql = "UPDATE Event SET status = 'Cancelled' WHERE eventID = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventID);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Simple Event DTO
    public static class EventSummary {
        public int eventID;
        public String title;

        public EventSummary(int eventID, String title) {
            this.eventID = eventID;
            this.title = title;
        }
    }

    public List<EventSummary> getActiveEvents() {
        List<EventSummary> events = new ArrayList<>();
        String sql = "SELECT eventID, Title FROM Event WHERE status = 'Active' ORDER BY eventID";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(new EventSummary(rs.getInt("eventID"), rs.getString("Title")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Method to fetch full event details by eventID
    public Event getEventById(int eventID) {
        String sql = "SELECT * FROM Event WHERE eventID = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Event e = new Event();
                    e.eventID = rs.getInt("eventID");
                    e.title = rs.getString("Title");
                    e.description = rs.getString("Description");
                    e.eventDate = rs.getDate("eventDate");
                    e.location = rs.getString("location");
                    e.createdBy = rs.getString("createdBy");
                    e.capacity = rs.getInt("Capacity");
                    e.createdAt = rs.getDate("createdAt");
                    e.status = rs.getString("status");
                    return e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Simple Event model for getEventById
    public static class Event {
        public int eventID;
        public String title;
        public String description;
        public Date eventDate;
        public String location;
        public String createdBy;
        public int capacity;
        public Date createdAt;
        public String status;
    }
}



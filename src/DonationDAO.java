import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationDAO {

    public void saveDonation(Integer userID, Integer guestID, double amount, Date date, String paymentMethod, String reference) {
        String sql = "INSERT INTO Donation (userID, guestID, amount, date, paymentMethod, reference) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (userID != null) {
                stmt.setInt(1, userID);
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }

            if (guestID != null) {
                stmt.setInt(2, guestID);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }

            stmt.setDouble(3, amount);
            stmt.setDate(4, date);
            stmt.setString(5, paymentMethod);
            stmt.setString(6, reference);

            stmt.executeUpdate();
            System.out.println("Donation saved to database!");

        } catch (SQLException e) {
            System.err.println("Error saving donation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int insertGuest(String firstName, String lastName, Long phoneNumber) {
        int guestID = -1;
        String sql = "INSERT INTO Guest (firstName, lastName, phoneNumber, eventRegisteredDate) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);

            if (phoneNumber != null) {
                stmt.setLong(3, phoneNumber);
            } else {
                stmt.setNull(3, java.sql.Types.BIGINT);
            }

            stmt.setDate(4, new Date(System.currentTimeMillis()));

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                guestID = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return guestID;
    }

    // New method to fetch donation history by userID or guestID
    public List<DonationRecord> getDonationsByUserID(Integer userID, Integer guestID) {
        List<DonationRecord> donations = new ArrayList<>();
        String sql = "SELECT donationID, userID, guestID, amount, date, paymentMethod, reference FROM Donation WHERE ";

        if (userID != null) {
            sql += "userID = ?";
        } else if (guestID != null) {
            sql += "guestID = ?";
        } else {
            // Neither userID nor guestID given
            return donations; // empty list
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (userID != null) {
                stmt.setInt(1, userID);
            } else {
                stmt.setInt(1, guestID);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DonationRecord record = new DonationRecord(
                        rs.getInt("donationID"),
                        rs.getObject("userID") != null ? rs.getInt("userID") : null,
                        rs.getObject("guestID") != null ? rs.getInt("guestID") : null,
                        rs.getDouble("amount"),
                        rs.getDate("date"),
                        rs.getString("paymentMethod"),
                        rs.getString("reference")
                );
                donations.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return donations;
    }

    // DonationRecord data class
    public static class DonationRecord {
        public int donationID;
        public Integer userID;   // nullable
        public Integer guestID;  // nullable
        public double amount;
        public Date date;
        public String paymentMethod;
        public String reference;

        public DonationRecord(int donationID, Integer userID, Integer guestID,
                              double amount, Date date, String paymentMethod, String reference) {
            this.donationID = donationID;
            this.userID = userID;
            this.guestID = guestID;
            this.amount = amount;
            this.date = date;
            this.paymentMethod = paymentMethod;
            this.reference = reference;
        }
    }
}


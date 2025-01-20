package lu.Kontermatt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    public List<Card> getCardsForPlayer(int userId, int sessionId) {
        List<Card> cards = new ArrayList<>();
        String query = "SELECT c.card_id, c.suit, c.rank, c.points, sc.strength " +
                "FROM session_cards sc " +
                "JOIN cards c ON sc.card_id = c.card_id " +
                "WHERE sc.user_id = ? AND sc.session_id = ? AND sc.location = 'hand'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, sessionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int cardId = rs.getInt("card_id");
                String suit = rs.getString("suit");
                String rank = rs.getString("rank");
                int points = rs.getInt("points");
                int strength = rs.getInt("strength");

                Card card = new Card(cardId, suit, rank, points, strength);
                cards.add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public boolean isCardPlayable(int sessionId, int cardId) {
        String query = "SELECT is_playable FROM session_cards WHERE session_id = ? AND card_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, cardId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("is_playable");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateCardLocation(int sessionId, int cardId, String newLocation) {
        String query = "UPDATE session_cards " +
                "SET location = ? " +
                "WHERE session_id = ? AND card_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newLocation); // Set the new location
            stmt.setInt(2, sessionId);      // Set the session ID
            stmt.setInt(3, cardId);         // Set the card ID

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
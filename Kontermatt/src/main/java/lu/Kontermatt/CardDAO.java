package lu.Kontermatt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO {

    public List<Card> getCardsForPlayer(int userId, int sessionId) {
        List<Card> cards = new ArrayList<>();

        String query = "SELECT c.suit, c.rank, c.points, sc.strength " +
                "FROM session_cards sc " +
                "JOIN cards c ON sc.card_id = c.card_id " +
                "WHERE sc.user_id = ? AND sc.session_id = ? AND sc.location = 'hand'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, sessionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String suit = rs.getString("suit");
                String rank = rs.getString("rank");
                int points = rs.getInt("points");
                int strength = rs.getInt("strength");

                Card card = new Card(suit, rank, points, strength);
                cards.add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }
}

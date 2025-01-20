package lu.Kontermatt;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/matchmaking")
public class MatchmakingServlet extends HttpServlet {

    // Number of players required to start a game
    private static final int REQUIRED_PLAYERS = 4;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve user_id from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            // User is not logged in; redirect to main page
            response.sendRedirect("main");
            return;
        }

        int userId = (int) session.getAttribute("user_id");

        try {
            // Add user to matchmaking queue
            addUserToQueue(userId);

            // Check if there are enough players to start a game
            List<Integer> userIds = getUsersInQueue();

            if (userIds.size() >= REQUIRED_PLAYERS) {
                // Start a new game session with the first REQUIRED_PLAYERS users
                List<Integer> players = userIds.subList(0, REQUIRED_PLAYERS);
                int sessionId = startNewSession(players);

                // Remove players from matchmaking queue
                removeUsersFromQueue(players);

                // Redirect users to the game page
                if (players.contains(userId)) {
                    // Current user is part of the new game
                    response.sendRedirect("game?sessionId=" + sessionId);
                } else {
                    // User is still in the queue; inform them to wait
                    response.sendRedirect("waiting");
                }
            } else {
                // Not enough players yet; inform the user to wait
                response.sendRedirect("waiting");
            }
        } catch (SQLException | ClassNotFoundException e) {
            // Handle exceptions
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        }
    }

    private void addUserToQueue(int userId) throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Check if user is already in the queue
            String checkSql = "SELECT COUNT(*) FROM matchmaking_queue WHERE user_id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // User is already in the queue; do nothing
                        return;
                    }
                }
            }

            // Add user to the queue
            String insertSql = "INSERT INTO matchmaking_queue (user_id) VALUES (?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.executeUpdate();
            }
        }
    }

    private List<Integer> getUsersInQueue() throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        List<Integer> userIds = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "SELECT user_id FROM matchmaking_queue ORDER BY timestamp";
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        }
        return userIds;
    }

    private int startNewSession(List<Integer> userIds) throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        int sessionId = -1;
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);
            try {
                // Create a new session
                String insertSessionSql = "INSERT INTO sessions (status, start_time) VALUES ('active', NOW())";
                try (PreparedStatement sessionStmt = connection.prepareStatement(insertSessionSql, Statement.RETURN_GENERATED_KEYS)) {
                    sessionStmt.executeUpdate();
                    try (ResultSet rs = sessionStmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            sessionId = rs.getInt(1);
                        } else {
                            throw new SQLException("Creating session failed, no ID obtained.");
                        }
                    }
                }

                // Assign seat numbers (1 to 4) and add to session_users
                String insertSessionUserSql = "INSERT INTO session_users (session_id, user_id, seat_number) VALUES (?, ?, ?)";
                try (PreparedStatement sessionUserStmt = connection.prepareStatement(insertSessionUserSql)) {
                    for (int i = 0; i < userIds.size(); i++) {
                        sessionUserStmt.setInt(1, sessionId);
                        sessionUserStmt.setInt(2, userIds.get(i));
                        sessionUserStmt.setInt(3, i + 1);
                        sessionUserStmt.addBatch();
                    }
                    sessionUserStmt.executeBatch();
                }

                // Initialize session_cards
                initializeSessionCards(connection, sessionId);

                // Deal cards to players
                dealCards(connection, sessionId, userIds);

                // Commit transaction
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
        return sessionId;
    }

    private void initializeSessionCards(Connection connection, int sessionId) throws SQLException {
        String sql = "INSERT INTO session_cards (session_id, card_id, location, strength, is_trump) " +
                "SELECT ?, card_id, 'deck', base_strength, base_is_trump FROM cards";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            stmt.executeUpdate();
        }
    }

    private void dealCards(Connection connection, int sessionId, List<Integer> userIds) throws SQLException {
        // Retrieve all card_ids for the session
        List<Integer> cardIds = new ArrayList<>();
        String selectCardsSql = "SELECT card_id FROM session_cards WHERE session_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectCardsSql)) {
            stmt.setInt(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cardIds.add(rs.getInt("card_id"));
                }
            }
        }

        // Shuffle the cards
        java.util.Collections.shuffle(cardIds);

        // Deal cards to players
        int cardsPerPlayer = cardIds.size() / userIds.size();
        String updateCardSql = "UPDATE session_cards SET user_id = ?, location = 'hand' WHERE session_id = ? AND card_id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateCardSql)) {
            for (int i = 0; i < userIds.size(); i++) {
                List<Integer> playerCards = cardIds.subList(i * cardsPerPlayer, (i + 1) * cardsPerPlayer);
                for (Integer cardId : playerCards) {
                    updateStmt.setInt(1, userIds.get(i));
                    updateStmt.setInt(2, sessionId);
                    updateStmt.setInt(3, cardId);
                    updateStmt.addBatch();
                }
            }
            updateStmt.executeBatch();
        }
    }

    private void removeUsersFromQueue(List<Integer> userIds) throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        try (Connection connection = DatabaseUtil.getConnection()) {
            String sql = "DELETE FROM matchmaking_queue WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (Integer userId : userIds) {
                    stmt.setInt(1, userId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }
}

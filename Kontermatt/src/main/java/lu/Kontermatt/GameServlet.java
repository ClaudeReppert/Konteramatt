package lu.Kontermatt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    private static final int SESSION_ID = 2;
    private static final int VIEWER_USER_ID = 7;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        List<UserSeat> userSeats = getUserSeats();
        List<Card> userHand = getUserHand(VIEWER_USER_ID);
        CardDAO cardDAO = new CardDAO(); // DAO to check playability

        // CSS styling for card display
        out.println("<style>");
        out.println(".center-container {");
        out.println("  position: fixed;");
        out.println("  top: 50%;");
        out.println("  left: 50%;");
        out.println("  transform: translate(-50%, -50%);");
        out.println("  display: flex;");
        out.println("  justify-content: center;");
        out.println("  align-items: center;");
        out.println("  border: 1px solid #000;");
        out.println("  padding: 20px;");
        out.println("  background-color: #f9f9f9;");
        out.println("}");

        out.println(".inner-container {");
        out.println("  width: 33%;");
        out.println("  height: 100%;");
        out.println("  border: 1px solid #ccc;");
        out.println("  display: flex;");
        out.println("  align-items: center;");
        out.println("  justify-content: center;");
        out.println("}");

        out.println(".middle-inner-container {");
        out.println("  display: flex;");
        out.println("  flex-direction: column;");
        out.println("  width: 100%;");
        out.println("  height: 100%;");
        out.println("}");

        out.println(".top-section, .bottom-section {");
        out.println("  height: 50%;");
        out.println("  border: 1px solid #aaa;");
        out.println("  display: flex;");
        out.println("  align-items: center;");
        out.println("  justify-content: center;");
        out.println("}");

        // CSS for cards
        out.println(".hand-container {");
        out.println("  position: fixed;");
        out.println("  bottom: 10px;");
        out.println("  width: 100%;");
        out.println("  display: flex;");
        out.println("  justify-content: center;");
        out.println("}");
        out.println(".card-hand {");
        out.println("  display: flex;");
        out.println("  gap: 10px;");
        out.println("  max-width: 50%;");
        out.println("  justify-content: center;");
        out.println("}");
        out.println(".card-button img {");
        out.println("  filter: drop-shadow(0 0 10px green);"); // Green glow for playable cards
        out.println("  transition: transform 0.2s, filter 0.2s;");
        out.println("}");
        out.println(".card-button:hover img {");
        out.println("  transform: scale(1.1);");
        out.println("}");
        out.println(".card-item img {");
        out.println("  opacity: 0.5;"); // Dim unplayable cards
        out.println("  filter: drop-shadow(0 0 10px red);"); // Red glow for unplayable cards
        out.println("}");
        out.println("</style>");

        // Display the game information
        out.println("<h2>Game State for Session ID: " + SESSION_ID + "</h2>");
        out.println("<h3>Players in the Session</h3>");
        out.println("<ul>");
        for (UserSeat seat : userSeats) {
            out.println("<li>Seat " + seat.getSeatNumber() + ": User " + seat.getUserId() + "</li>");
        }
        out.println("</ul>");

        // Render the center-container (game state display)
        out.println("<div class='center-container'>");
        out.println("  <div class='inner-container'>");
        out.println("    <img src='/Kontermatt/images/cards_front/ADiamonds.svg' alt='Card' width='120px' height='180px' />");
        out.println("  </div>");
        out.println("  <div class='inner-container middle-inner-container'>");
        out.println("    <div class='top-section'>");
        out.println("      <img src='/Kontermatt/images/cards_front/ADiamonds.svg' alt='Card' width='120px' height='180px' />");
        out.println("    </div>");
        out.println("    <div class='bottom-section'>");
        out.println("      <img src='/Kontermatt/images/cards_front/ADiamonds.svg' alt='Card' width='120px' height='180px' />");
        out.println("    </div>");
        out.println("  </div>");
        out.println("  <div class='inner-container'>");
        out.println("    <img src='/Kontermatt/images/cards_front/ADiamonds.svg' alt='Card' width='120px' height='180px' />");
        out.println("  </div>");
        out.println("</div>");

        // Display the user's hand
        out.println("<div class='hand-container'>");
        out.println("<div class='card-hand'>");
        for (Card card : userHand) {
            if (cardDAO.isCardPlayable(SESSION_ID, card.getCardId())) {
                // Render clickable button for playable cards with data attributes
                out.println("<button type='button' class='card-button' data-card-id='" + card.getCardId() + "'>");
                out.println("<img src='" + card.getImagePath() + "' alt='" + card.getRank() + " of " + card.getSuit() + "' width='160px' height='240px'/>");
                out.println("</button>");
            } else {
                // Render static display for unplayable cards
                out.println("<div class='card-item'>");
                out.println("<img src='" + card.getImagePath() + "' alt='" + card.getRank() + " of " + card.getSuit() + "' width='160px' height='240px'/>");
                out.println("<p>" + card.getRank() + " of " + card.getSuit() + "</p>");
                out.println("</div>");
            }
        }
        out.println("</div>");
        out.println("</div>");
        out.println("<script src='/Kontermatt/static/js/playCard.js'></script>");
        out.close();
    }

    private List<UserSeat> getUserSeats() {
        List<UserSeat> seats = new ArrayList<>();
        String query = "SELECT user_id, seat_number FROM session_users WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, SESSION_ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                int seatNumber = rs.getInt("seat_number");
                seats.add(new UserSeat(userId, seatNumber));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    private List<Card> getUserHand(int userId) {
        CardDAO cardDAO = new CardDAO();
        return cardDAO.getCardsForPlayer(userId, SESSION_ID);
    }

    private class UserSeat {
        private int userId;
        private int seatNumber;

        public UserSeat(int userId, int seatNumber) {
            this.userId = userId;
            this.seatNumber = seatNumber;
        }

        public int getUserId() {
            return userId;
        }

        public int getSeatNumber() {
            return seatNumber;
        }
    }
}
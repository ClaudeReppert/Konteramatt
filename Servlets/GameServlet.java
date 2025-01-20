package lu.Kontermatt;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/game")
public class GameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve sessionId from request parameters
        String sessionIdParam = request.getParameter("sessionId");
        if (sessionIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session ID is missing.");
            return;
        }

        int sessionId;
        try {
            sessionId = Integer.parseInt(sessionIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Session ID.");
            return;
        }

        // Retrieve user_id from session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            // User is not logged in; redirect to main page
            response.sendRedirect("main");
            return;
        }

        int userId = (int) session.getAttribute("user_id");

        // Use CardDAO to get the list of cards for the player
        CardDAO cardDAO = new CardDAO();
        List<Card> playerCards = cardDAO.getCardsForPlayer(userId, sessionId);

        // Generate HTML to display the cards
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html><head><title>Game Session</title></head><body>");
        response.getWriter().println("<h1>Game Session ID: " + sessionId + "</h1>");
        response.getWriter().println("<p>Welcome, User ID: " + userId + "</p>");
        response.getWriter().println("<h2>Your Cards:</h2>");
        response.getWriter().println("<div style='display: flex;'>");

        for (Card card : playerCards) {
            response.getWriter().println("<div style='margin: 5px;'>");
            response.getWriter().println("<img src='" + card.getImagePath() + "' alt='" +
                    card.getRank() + " of " + card.getSuit() + "' width='100'/>");
            response.getWriter().println("</div>");
        }

        response.getWriter().println("</div>");
        response.getWriter().println("</body></html>");
    }
}
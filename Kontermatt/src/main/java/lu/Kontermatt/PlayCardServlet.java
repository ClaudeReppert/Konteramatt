package lu.Kontermatt;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/playCard")
public class PlayCardServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Set correct content type
        response.setCharacterEncoding("UTF-8");     // Ensure UTF-8 encoding

        int sessionId = Integer.parseInt(request.getParameter("sessionId"));
        int cardId = Integer.parseInt(request.getParameter("cardId"));

        CardDAO cardDAO = new CardDAO();
        boolean success = false;

        // Update the card location to 'table'
        if (cardDAO.isCardPlayable(sessionId, cardId)) {
            cardDAO.updateCardLocation(sessionId, cardId, "table");
            success = true;
        }

        // Send JSON response
        try (PrintWriter out = response.getWriter()) {
            out.write("{\"success\": " + success + "}");
        }
    }
}
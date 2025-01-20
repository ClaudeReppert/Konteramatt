package lu.Kontermatt;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/waiting")
public class WaitingServlet extends HttpServlet {

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
        int sessionId = getAssignedSessionId(userId);

        if (sessionId != -1) {
            // User has been assigned to a session; redirect to game
            response.sendRedirect("game?sessionId=" + sessionId);
        } else {
            // User is still waiting; display waiting message
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<!DOCTYPE html>");
            response.getWriter().println("<html><head><title>Waiting for Players</title>");
            response.getWriter().println("<meta http-equiv='refresh' content='5; URL=waiting'></head><body>");
            response.getWriter().println("<h1>Please wait...</h1>");
            response.getWriter().println("<p>Waiting for other players to join.</p>");
            response.getWriter().println("</body></html>");
        }
    }

    private int getAssignedSessionId(int userId) {
        int sessionId = -1;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            try (Connection connection = DatabaseUtil.getConnection()) {
                String sql = "SELECT session_id FROM session_users WHERE user_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            sessionId = rs.getInt("session_id");
                        }
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle exceptions as needed
        }
        return sessionId;
    }
}

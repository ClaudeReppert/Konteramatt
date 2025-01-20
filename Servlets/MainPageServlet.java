package lu.Kontermatt;

import java.io.IOException;
import java.sql.*;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Servlet annotation maps this servlet to the URL pattern "/main"
@WebServlet("/main")
public class MainPageServlet extends HttpServlet {

    // Handles GET requests
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Generate the HTML page with a button
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html><head><title>Main Page</title></head><body>");
        response.getWriter().println("<h1>Kontermatt</h1>");
        response.getWriter().println("<form method='post' action='main'>");
        response.getWriter().println("<input type='submit' name='playAsGuest' value='Play as Guest'/>");
        response.getWriter().println("</form>");
        response.getWriter().println("</body></html>");
    }

    // Handles POST requests
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if the playAsGuest button was clicked
        if (request.getParameter("playAsGuest") != null) {
            try {
                // Create a guest user
                int userId = createGuestUser();

                // Store userId in the session
                HttpSession session = request.getSession();
                session.setAttribute("user_id", userId);

                // Redirect to the matchmaking or game page
                response.sendRedirect("matchmaking");
            } catch (SQLException e) {
                // Handle SQL exceptions
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
            } catch (ClassNotFoundException e) {
                // Handle Class.forName exceptions
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JDBC Driver not found.");
            }
        }
    }

    // Method to create a guest user in the database
    private int createGuestUser() throws SQLException, ClassNotFoundException {
        // Load the MariaDB JDBC driver
        Class.forName("org.mariadb.jdbc.Driver");

        // Establish a database connection
        try (Connection connection = DatabaseUtil.getConnection()) {
            // Generate a unique guest username
            String guestUsername = "Guest" + UUID.randomUUID().toString().substring(0, 8);

            // Prepare the SQL statement to insert a new guest user
            String sql = "INSERT INTO users (username, is_guest) VALUES (?, TRUE)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, guestUsername);
                stmt.executeUpdate();

                // Retrieve the generated user_id
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the user_id
                    } else {
                        throw new SQLException("Creating guest user failed, no ID obtained.");
                    }
                }
            }
        }
    }
}

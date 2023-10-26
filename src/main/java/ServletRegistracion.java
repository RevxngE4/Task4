import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/signup")
public class ServletRegistracion extends HttpServlet {
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "andrescamilo4";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/11-200";
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/html/signup.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from usuarios");

            String first_name = request.getParameter("first_name");
            String surname = request.getParameter("surname");
            String login = request.getParameter("login");
            String password = request.getParameter("password");
            String sqlInsertUser = "insert into usuarios(first_name, surname, login, password) values('" + first_name + "', '" + surname + "', '" + login + "', '" + password +"');";
            int affectedRows = statement.executeUpdate(sqlInsertUser);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

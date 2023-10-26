import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/index")
public class ServletLogin extends HttpServlet {
    private UserRepository userRepository;

    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "andrescamilo4";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/11-200";

    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      Cookie[] cookies = request.getCookies();
      UUID sessionUUID = null;
      User user = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionUUID")) {
                    sessionUUID = UUID.fromString(cookie.getValue());
                    break;
                }
            }
        }
        if (sessionUUID != null) {
            // Verificar si el UUID existe en la tabla user_sessions
            try {
                Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String selectQuery = "SELECT user_id FROM user_sessions WHERE session_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setObject(1, sessionUUID);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    long userId = resultSet.getLong("user_id");
                    UserRepository userRepository = new UserRepositoryLogin(connection);
                    user = userRepository.findById(userId);
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (user != null) {
            // Si se encontró un usuario asociado al UUID, redirige al usuario a la página de autorización exitosa
            // y no a la página de registro.
            List<User> userList = getUsersFromDatabase();
            request.setAttribute("userList", userList);
            request.getRequestDispatcher("/userList.jsp").forward(request, response);
        } else {
            // Si no se encontró un usuario, muestra la página de registro.
            request.getRequestDispatcher("/html/index.html").forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String login = request.getParameter("login");
            String password = request.getParameter("password");

            UserRepository userRepository = new UserRepositoryLogin(connection);

            User user = userRepository.findByEmailAndPassword(login, password);

            if (user != null) {
                System.out.println("Good!");
                UUID sessionUUID = UUID.randomUUID();
                Cookie sessionCookie = new Cookie("sessionUUID", sessionUUID.toString());
                response.addCookie(sessionCookie);
                try {
                    String insertQuery = "INSERT INTO user_sessions (session_id, user_id) VALUES (?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setObject(1, sessionUUID);
                    preparedStatement.setObject(2, user.getId());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
                List<User> userList = getUsersFromDatabase();
                request.setAttribute("userList", userList);
                request.getRequestDispatcher("/userList.jsp").forward(request, response);
            } else {

                response.getWriter().write("Usuario no registrado");
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<User> getUsersFromDatabase() {

        List<User> userList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String selectQuery = "SELECT * FROM usuarios"; // Ajusta esto según la estructura de tu tabla
            preparedStatement = connection.prepareStatement(selectQuery);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password")
                );
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userList;
    }
}
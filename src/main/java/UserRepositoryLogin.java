import java.sql.*;

public class UserRepositoryLogin implements UserRepository {


    private Connection connection;
    private static final String SQl_SELECT_ALL_FROM_DRIVER = "SELECT * FROM usuarios WHERE login = ? AND password = ?";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "andrescamilo4";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/11-200";

    public UserRepositoryLogin(Connection connection){
        this.connection = connection;
    }

    @Override
    public User findByEmailAndPassword(String login, String password) {
        User result = null;

        try (PreparedStatement preparedStatement = connection.prepareStatement(SQl_SELECT_ALL_FROM_DRIVER)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    result = new User(
                            resultSet.getLong("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("surname"),
                            resultSet.getString("login"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    public User findById(long userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String selectQuery = "SELECT * FROM usuarios WHERE id = ?";
            preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("surname"),
                        resultSet.getString("login"),
                        resultSet.getString("password")
                );
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

        return user;
    }


}


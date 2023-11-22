package DAO;

import Models.User;
import java.sql.SQLException;

/**
 * The UserDAO class is responsible for storing and retrieving User objects from the database
 */
public class UserDAO implements DAO {

    private final Database database = Database.getInstance();

    /**
     * Gets a user from the list of users, given the username.
     * @param username the username
     * @return the user
     */
    public User getUser(String username) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "SELECT * FROM userinfo WHERE username = ?";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, username);
            var resultSet = preparedStatement.executeQuery();
            database.returnConnection(connection);
            if (resultSet.next()) {
                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email")
                );
            }
//            throw new DataAccessException(500, "User with username '" + username + "' not found");
            return null;
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Adds a user to the list of users.
     * @param username the username
     * @param password the password
     * @param email the email
     * @throws DataAccessException if the user already exists
     */
    public void addUser(String username, String password, String email) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "INSERT INTO userinfo (username, password, email) VALUES (?, ?, ?)";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.executeUpdate();
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Clears the list of users.
     * @throws DataAccessException if clearing the list of users fails
     */
    public void clear() throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "DELETE FROM userinfo";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.execute();
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }
}

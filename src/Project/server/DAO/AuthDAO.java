package server.DAO;

import server.Models.AuthToken;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The AuthDAO class is responsible for storing and retrieving auth tokens.
 */
public class AuthDAO implements DAO {
    private final Database database = Database.getInstance();

    /**
     * Adds an auth token to the list of auth tokens.
     * @param authToken the auth token to add
     * @throws DataAccessException if the auth token already exists
     */
    public void addAuthToken(AuthToken authToken) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            var preparedStatement = connection.prepareStatement(command);

            preparedStatement.setString(1, authToken.authToken);
            preparedStatement.setString(2, authToken.username);

            preparedStatement.executeUpdate();
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Clears the list of auth tokens.
     * @throws DataAccessException if clearing the list of auth tokens fails
     */
    public void clear() throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "DELETE FROM auth";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.execute();
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Gets an auth token from the list of auth tokens, given the auth token string.
     * @param authToken the auth token string
     * @return the auth token
     */
    public AuthToken getAuthToken(String authToken) throws DataAccessException {
        var connection = database.getConnection();

        try {
            var command = "SELECT * FROM auth WHERE authToken = ?";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, authToken);
            var resultSet = preparedStatement.executeQuery();
            database.returnConnection(connection);
            AuthToken returnToken = null;
            if (resultSet.next()) {
                var username = resultSet.getString("username");
                returnToken = new AuthToken(username);
                returnToken.authToken = authToken;
            }
            return returnToken;
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Gets an auth token from the list of auth tokens, given the username.
     * @param username the username
     * @return the auth token
     * @throws DataAccessException if the auth token does not exist
     */
    public AuthToken getAuthTokenByUsername(String username) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "SELECT * FROM auth WHERE username = ?";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, username);
            var resultSet = preparedStatement.executeQuery();
            database.returnConnection(connection);
            if (resultSet.next()) {
                var authToken = resultSet.getString("authToken");
                var returnToken = new AuthToken(username);
                returnToken.authToken = authToken;
                return returnToken;
            } else {
                throw new DataAccessException(500, "Auth token for username " + username + " not found");
            }
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Deletes an auth token from the list of auth tokens, given the auth token string.
     * @param authToken the auth token string
     * @throws DataAccessException if the auth token does not exist
     */
    public void deleteAuthToken(String authToken) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "DELETE FROM auth WHERE authToken = ?";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, authToken);
            preparedStatement.execute();
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }
}

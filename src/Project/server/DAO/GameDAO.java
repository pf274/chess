package server.DAO;

import chess.ChessBoard;
import chess.ChessBoardImpl;
import server.Models.Game;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The GameDAO class is responsible for storing and retrieving Game objects from the database
 */
public class GameDAO implements DAO {
    private final Database database = Database.getInstance();

    /**
     * Gets a game from the list of games, given the game ID.
     * @param gameID the game ID
     * @return the game
     * @throws DataAccessException if the game does not exist
     */
    public Game getGameByID(int gameID) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "SELECT * FROM gameinfo WHERE gameID = ?";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setInt(1, gameID);
            var resultSet = preparedStatement.executeQuery();
            database.returnConnection(connection);
            if (resultSet.next()) {
                var gameName = resultSet.getString("gameName");
                // TODO: get the rest of the columns
                var game = new Game(gameID, gameName);
                // TODO: set the board
                ChessBoardImpl chessBoard = new ChessBoardImpl();
                return game;
            }
            throw new DataAccessException(500, "Game with ID " + gameID + " not found");
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Gets a game from the list of games, given the game name.
     * @param gameName the game name
     * @return the game
     * @throws DataAccessException if the game does not exist
     */
    public Game getGameByGameName(String gameName) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "SELECT * FROM gameinfo WHERE gameName = ?";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, gameName);
            var resultSet = preparedStatement.executeQuery();
            database.returnConnection(connection);
            if (resultSet.next()) {
                var gameID = resultSet.getInt("gameID");
                // TODO: get the rest of the columns
                var game = new Game(gameID, gameName);
                // TODO: set the board
                ChessBoardImpl chessBoard = new ChessBoardImpl();
                return game;
            }
            throw new DataAccessException(500, "Game with Name " + gameName + " not found");
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    public void addGameHistory(Game game) throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "INSERT INTO gamehistory (gameID, moveNumber, gameState) VALUES (?, ?, ?)";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setInt(1, game.gameID);
            preparedStatement.setInt(2, game.moveNumber);
            // TODO: get the game state
            preparedStatement.setString(3, "");
            preparedStatement.execute();
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Adds a game to the list of games.
     * @param gameName the game name
     * @throws DataAccessException if the game already exists
     */
    public Game addGame(String gameName) throws DataAccessException {
        var connection = database.getConnection();
        try {
            // create a new game and get its new ID
            var command = "INSERT INTO gameinfo (gameName) VALUES (?)";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, gameName);
            preparedStatement.execute();
            // get the newly created game
            command = "SELECT * FROM gameinfo WHERE gameName = ?";
            preparedStatement = connection.prepareStatement(command);
            preparedStatement.setString(1, gameName);
            var resultSet = preparedStatement.executeQuery();
            database.returnConnection(connection);
            if (resultSet.next()) {
                var gameID = resultSet.getInt("gameID");
                var gameState = resultSet.getString("gameState");
                // create the game object
                var game = new Game(gameID, gameName);
                // TODO: set the board
                // create a new game history entry
                addGameHistory(game);
                return game;
            }
            throw new DataAccessException(500, "Failed to add game " + gameName);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Clears the list of games.
     * @throws DataAccessException if clearing the list of games fails
     */
    public void clear() throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "DELETE FROM gameinfo";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.execute();
            command = "DELETE FROM gamehistory";
            preparedStatement = connection.prepareStatement(command);
            preparedStatement.execute();
            command = "DELETE FROM gamespectators";
            preparedStatement = connection.prepareStatement(command);
            preparedStatement.execute();
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Gets all the games from the list of games.
     * @return the list of games
     * @throws DataAccessException if getting the list of games fails
     */
    public ArrayList<Game> getAllGames() throws DataAccessException {
        var connection = database.getConnection();
        try {
            var command = "SELECT * FROM gameinfo";
            var preparedStatement = connection.prepareStatement(command);
            var resultSet = preparedStatement.executeQuery();
            database.returnConnection(connection);
            var games = new ArrayList<Game>();
            while (resultSet.next()) {
                var gameID = resultSet.getInt("gameID");
                var gameName = resultSet.getString("gameName");
                var gameState = resultSet.getString("gameState");
                // TODO: get the rest of the columns
                var game = new Game(gameID, gameName);
                // TODO: set the board
                games.add(game);
            }
            return games;
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Removes a user from a game.
     * @param gameID the game ID
     * @param username the username
     */
    public void removeUserFromGame(int gameID, String username) throws DataAccessException {
        var connection = database.getConnection();
        try {
            // get games where the white or black username is the username
            var command = "SELECT * FROM gameinfo WHERE gameID = ? AND (whiteUser = ? OR blackUser = ?)";
            var preparedStatement = connection.prepareStatement(command);
            preparedStatement.setInt(1, gameID);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, username);
            var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (Objects.equals(resultSet.getString("whiteUser"), username)) {
                    // if the username is the white username, set the white username to null
                    command = "UPDATE gameinfo SET whiteUser = NULL WHERE gameID = ?";
                    preparedStatement = connection.prepareStatement(command);
                    preparedStatement.setInt(1, gameID);
                    preparedStatement.execute();
                } else if (Objects.equals(resultSet.getString("blackUser"), username)) {
                    // if the username is the black username, set the black username to null
                    command = "UPDATE gameinfo SET blackUser = NULL WHERE gameID = ?";
                    preparedStatement = connection.prepareStatement(command);
                    preparedStatement.setInt(1, gameID);
                    preparedStatement.execute();
                } else {
                    // if the username is not the white or black username, remove the username from the list of spectators
                    command = "DELETE FROM gamespectators WHERE gameID = ? AND username = ?";
                    preparedStatement = connection.prepareStatement(command);
                    preparedStatement.setInt(1, gameID);
                    preparedStatement.setString(2, username);
                    preparedStatement.execute();
                }
            }
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }

    /**
     * Adds a user to a game.
     * @param gameID the game ID
     * @param username the username
     * @param playerColor the player color
     * @throws DataAccessException if the game does not exist or if the color is already in use.
     */
    public void addUserToGame(int gameID, String username, String playerColor) throws DataAccessException {
        var connection = database.getConnection();
        try {
            if (playerColor.equalsIgnoreCase("WHITE")) {
                String command = "UPDATE gameinfo SET blackUser = NULL WHERE gameID = ?";
                var preparedStatement = connection.prepareStatement(command);
                preparedStatement.setInt(1, gameID);
                preparedStatement.execute();
            } else if (playerColor.equalsIgnoreCase("BLACK")) {
                String command = "UPDATE gameinfo SET blackUser = NULL WHERE gameID = ?";
                var preparedStatement = connection.prepareStatement(command);
                preparedStatement.setInt(1, gameID);
                preparedStatement.execute();
            } else {
                String command = "INSERT INTO gamespectators (gameID, username) VALUES (?, ?)";
                var preparedStatement = connection.prepareStatement(command);
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, username);
                preparedStatement.execute();
            }
            database.returnConnection(connection);
        } catch (SQLException e) {
            database.returnConnection(connection);
            throw new DataAccessException(500, e.getMessage());
        }
    }
}

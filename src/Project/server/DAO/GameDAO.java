package server.DAO;

import server.Models.Game;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The GameDAO class is responsible for storing and retrieving Game objects from the database
 */
public class GameDAO implements DAO {

    /**
     * The list of games.
     */
    public final ArrayList<Game> games = new ArrayList<>();

    /**
     * Gets a game from the list of games, given the game ID.
     * @param gameID the game ID
     * @return the game
     * @throws DataAccessException if the game does not exist
     */
    public Game getGameByID(int gameID) throws DataAccessException {
        for (Game game : games) {
            if (game.gameID == gameID) {
                return game;
            }
        }
        return null;
    }

    /**
     * Gets a game from the list of games, given the game name.
     * @param gameName the game name
     * @return the game
     * @throws DataAccessException if the game does not exist
     */
    public Game getGameByGameName(String gameName) throws DataAccessException {
        for (Game game : games) {
            if (game.gameName.equals(gameName)) {
                return game;
            }
        }
        return null;
    }

    /**
     * Adds a game to the list of games.
     * @param gameName the game name
     * @throws DataAccessException if the game already exists
     */
    public Game addGame(String gameName) throws DataAccessException {
        for (Game g : games) {
            if (Objects.equals(g.gameName, gameName)) {
                throw new DataAccessException(500, "Game with Name " + gameName + " already exists");
            }
        }
        int gameID = games.size() + 1;
        Game game = new Game(gameID, gameName);
        games.add(game);
        return game;
    }

    /**
     * Clears the list of games.
     * @throws DataAccessException if clearing the list of games fails
     */
    public void clear() throws DataAccessException {
        games.clear();
    }

    /**
     * Gets all the games from the list of games.
     * @return the list of games
     * @throws DataAccessException if getting the list of games fails
     */
    public ArrayList<Game> getAllGames() throws DataAccessException {
        return games;
    }

    /**
     * Removes a user from a game.
     * @param gameID the game ID
     * @param username the username
     */
    public void removeUserFromGame(int gameID, String username) throws DataAccessException {
        for (var game : games) {
            if (game.gameID == gameID) {
                if (game.whiteUsername.equals(username)) {
                    game.whiteUsername = null;
                    return;
                } else if (game.blackUsername.equals(username)) {
                    game.blackUsername = null;
                    return;
                }
            }
        }
        throw new DataAccessException(500, "User " + username + " not found in game " + gameID);
    }

    /**
     * Adds a user to a game.
     * @param gameID the game ID
     * @param username the username
     * @param playerColor the player color
     * @throws DataAccessException if the game does not exist or if the color is already in use.
     */
    public void addUserToGame(int gameID, String username, String playerColor) throws DataAccessException {
        for (var game : games) {
            if (game.gameID == gameID) {
                if (Objects.equals(playerColor, "WHITE")) {
                    if (game.whiteUsername == null) {
                        game.whiteUsername = username;
                        return;
                    } else {
                        throw new DataAccessException(500, "White player already exists");
                    }
                } else if (Objects.equals(playerColor, "BLACK")) {
                    if (game.blackUsername == null) {
                        game.blackUsername = username;
                        return;
                    } else {
                        throw new DataAccessException(500, "Black player already exists");
                    }
                } else {
                    // TODO: handle spectators
                    return;
                }
            }
        }
        throw new DataAccessException(500, "Game " + gameID + " not found");
    }
}

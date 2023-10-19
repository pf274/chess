package server.DAO;

import server.Models.Game;
import server.Models.User;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The GameDAO class is responsible for storing and retrieving Game objects from the database
 */
public class GameDAO implements DAO {

    /**
     * The list of games.
     */
    private final ArrayList<Game> games = new ArrayList<>();

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
        throw new DataAccessException("Game not found for game ID " + gameID);
    }

    /**
     * Gets a game from the list of games, given the username of one of the players.
     * @param username the username
     * @return the game
     * @throws DataAccessException if no such game exists
     */
    public Game getGameByUser(String username) throws DataAccessException {
        for (Game game : games) {
            if (game.whiteUsername.equals(username) || game.blackUsername.equals(username)) {
                return game;
            }
        }
        throw new DataAccessException("Game not found for user " + username);
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
        throw new DataAccessException("Game not found for game name " + gameName);
    }

    /**
     * Adds a game to the list of games.
     * @param gameName the game name
     * @throws DataAccessException if the game already exists
     */
    public Game addGame(String gameName) throws DataAccessException {
        for (Game g : games) {
            if (Objects.equals(g.gameName, gameName)) {
                throw new DataAccessException("Game with Name " + gameName + " already exists");
            }
        }
        int gameID = games.size();
        Game game = new Game(gameID, gameName);
        games.add(game);
        return game;
    }

    /**
     * Updates a game in the list of games, given the game ID and the new game.
     * @param gameID the game ID
     * @param game the new game
     * @throws DataAccessException if the game does not exist
     */
    public void updateGame(int gameID, Game game) throws DataAccessException {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID == gameID) {
                games.set(i, game);
                return;
            }
        }
        throw new DataAccessException("Game not found for game ID " + gameID);
    }

    /**
     * Deletes a game from the list of games, given the game ID.
     * @param gameID the game ID
     * @throws DataAccessException if the game does not exist
     */
    public void deleteGame(int gameID) throws DataAccessException {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID == gameID) {
                games.remove(i);
                return;
            }
        }
        throw new DataAccessException("Game not found for game ID " + gameID);
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
        throw new DataAccessException("User " + username + " not found in game " + gameID);
    }

    /**
     * Adds a user to a game.
     * @param gameID the game ID
     * @param username the username
     * @param playerColor the player color
     * @throws DataAccessException if the game does not exist or if the color is already in use.
     */
    public void addUserToGame(int gameID, String username, User.Color playerColor) throws DataAccessException {
        for (var game : games) {
            if (game.gameID == gameID) {
                if (playerColor.equals(User.Color.WHITE)) {
                    if (game.whiteUsername == null) {
                        game.whiteUsername = username;
                        return;
                    } else {
                        throw new DataAccessException("White player already exists");
                    }
                } else if (playerColor.equals(User.Color.BLACK)) {
                    if (game.blackUsername == null) {
                        game.blackUsername = username;
                        return;
                    } else {
                        throw new DataAccessException("Black player already exists");
                    }
                } else {
                    // TODO: handle spectators
                }
            }
        }
        throw new DataAccessException("Game " + gameID + " not found");
    }
}

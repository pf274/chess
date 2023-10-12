package server.Models;

import chess.ChessGame;

/**
 * Represents a game
 */
public class Game {

    /**
     * The game's ID
     */
    public int gameID;

    /**
     * The username of the player playing white
     */
    public String whiteUsername;

    /**
     * The username of the player playing black
     */
    public String blackUsername;

    /**
     * The game's name
     */
    public String gameName;

    /**
     * The chess game instance
     */
    public ChessGame game;

    /**
     * Creates a new game
     * @param gameID the game ID
     * @param gameName the game name
     */
    public Game(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
        // TODO: Initialize game
    }
}

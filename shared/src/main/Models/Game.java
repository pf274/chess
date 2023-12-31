package Models;

import chess.ChessGameImpl;

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
    public ChessGameImpl game;
    public int moveNumber = 0;

    public boolean gameOver = false;

    /**
     * Creates a new game
     * @param gameID the game's ID
     * @param gameName the game name
     */
    public Game(int gameID, String gameName) {
        this.gameID = gameID;
        this.gameName = gameName;
        game = new ChessGameImpl();
        game.getBoard().resetBoard();
    }
}

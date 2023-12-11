package userCommands;

import chess.ChessGame;

public class UserGameCommandJoinPlayer extends UserGameCommand {
    public final ChessGame.TeamColor playerColor;

    public final int gameID;

    public UserGameCommandJoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }


}

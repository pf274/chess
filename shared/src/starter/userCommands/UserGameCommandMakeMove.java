package userCommands;

import chess.ChessMove;

public class UserGameCommandMakeMove extends UserGameCommand {

    public final int gameID;
    public final ChessMove move;

    public UserGameCommandMakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
    }

    protected CommandType commandType = CommandType.JOIN_OBSERVER;
}

package userCommands;

import chess.ChessMove;
import chess.ChessMoveImpl;

public class UserGameCommandMakeMove extends UserGameCommand {

    public final int gameID;
    public final ChessMoveImpl move;

    public UserGameCommandMakeMove(String authToken, int gameID, ChessMoveImpl move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }
}

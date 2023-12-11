package serverMessages;

import chess.ChessGame;
import chess.ChessGameImpl;

public class ServerMessageLoadGame extends ServerMessage {

    public final ChessGameImpl game;
    public ServerMessageLoadGame(ChessGameImpl game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}

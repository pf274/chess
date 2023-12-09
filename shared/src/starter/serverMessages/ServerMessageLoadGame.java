package serverMessages;

import Models.Game;
import chess.ChessGame;

public class ServerMessageLoadGame extends ServerMessage {

    public final ChessGame game;
    ServerMessageType serverMessageType = ServerMessageType.NOTIFICATION;
    public ServerMessageLoadGame(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}

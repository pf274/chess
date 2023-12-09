package serverMessages;

public class ServerMessageError extends ServerMessage {
    public final String errorMessage;
    public ServerMessageError(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
}

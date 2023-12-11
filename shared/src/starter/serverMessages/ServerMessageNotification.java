package serverMessages;

public class ServerMessageNotification extends ServerMessage {
    public final String message;
    public ServerMessageNotification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}

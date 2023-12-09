package serverMessages;

public class ServerMessageNotification extends ServerMessage {
    public final String message;
    ServerMessageType serverMessageType = ServerMessageType.NOTIFICATION;
    public ServerMessageNotification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
}

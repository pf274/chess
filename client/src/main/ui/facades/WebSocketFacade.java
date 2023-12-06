package ui.facades;

import WebSocket.ChessAction;
import WebSocket.MessageFormatter;
import ui.menus.MenuBase;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;

    private MenuBase parentMenu;
    public WebSocketFacade(MenuBase parentMenu) {
        try {
            String serverUrl = "http://localhost:8080";
            String url = serverUrl.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                System.out.println(message);
                this.parentMenu.handleWSMessage(message);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    public void reassign(MenuBase newParentMenu) {
        this.parentMenu = newParentMenu;
    }

    public void sendMessage(int gameID, String username, ChessAction action, String details) {
        try {
            String body = MessageFormatter.prepareBody(username, gameID, action, details);
            this.session.getBasicRemote().sendText(body);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void leaveGame() {
        try {
            sendMessage(parentMenu.gameID, parentMenu.authToken.username, ChessAction.DISCONNECT, "");
            this.session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

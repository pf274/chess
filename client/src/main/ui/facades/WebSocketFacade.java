package ui.facades;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import serverMessages.ServerMessage;
import serverMessages.ServerMessageError;
import serverMessages.ServerMessageLoadGame;
import serverMessages.ServerMessageNotification;
import ui.BoardDisplay;
import deserializer.ChessPieceDeserializer;
import deserializer.ChessPositionDeserializer;
import ui.menus.MenuBase;
import userCommands.*;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private static WebSocketFacade instance = null;
    public static WebSocketFacade getInstance() {
        if (instance == null) {
            instance = new WebSocketFacade();
        }
        if (!instance.session.isOpen()) {
            instance.session = instance.getNewSession();
        }
        return instance;
    }

    private Session session;

    public WebSocketFacade() {
        session = getNewSession();
    }

    private Session getNewSession(){
        try {
            String serverUrl = "ws://localhost:8080";
            URI socketURI = new URI(serverUrl + "/connect");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session newSession = container.connectToServer(this, socketURI);
            newSession.addMessageHandler(new MessageHandler.Whole<String>() { // DON'T change this to a lambda function. IntelliJ is wrong.
                @Override
                public void onMessage(String message) {
                    handleWSMessage(message);
                }
            });
            return newSession;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to initialize websocket");
            return null;
        }
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendMessage(UserGameCommand command) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(ChessPieceImpl.class, new ChessPieceDeserializer());
            gsonBuilder.registerTypeAdapter(ChessPositionImpl.class, new ChessPositionDeserializer());
            String message = gsonBuilder.create().toJson(command);
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void joinGameAsPlayer(int gameID, String teamColor) {
        try {
            UserGameCommandJoinPlayer joinPlayerCommand = new UserGameCommandJoinPlayer(MenuBase.authToken.authToken, gameID, ChessGame.TeamColor.valueOf(teamColor.toUpperCase()));
            sendMessage(joinPlayerCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void joinGameAsObserver(int gameID) {
        try {
            UserGameCommandJoinObserver joinObserverCommand = new UserGameCommandJoinObserver(MenuBase.authToken.authToken, gameID);
            sendMessage(joinObserverCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void leaveGame(int gameID) {
        try {
            UserGameCommandLeave leaveCommand = new UserGameCommandLeave(MenuBase.authToken.authToken, gameID);
            sendMessage(leaveCommand);
            this.session.close();
            WebSocketFacade.instance = null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(ChessMove move, int gameID) {
        try {
            ChessMoveImpl moveImpl = (ChessMoveImpl) move;
            UserGameCommandMakeMove makeMoveCommand = new UserGameCommandMakeMove(MenuBase.authToken.authToken, gameID, moveImpl);
            sendMessage(makeMoveCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void resign(int gameID) {
        try {
            UserGameCommandResign resignCommand = new UserGameCommandResign(MenuBase.authToken.authToken, gameID);
            sendMessage(resignCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void handleWSMessage(String message) {
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case ERROR:
                    ServerMessageError error = new Gson().fromJson(message, ServerMessageError.class);
                    System.out.println(error.errorMessage);
                    System.out.print(">>> ");
                    break;
                case NOTIFICATION:
                    ServerMessageNotification notification = new Gson().fromJson(message, ServerMessageNotification.class);
                    System.out.println(notification.message);
                    System.out.print(">>> ");
                    break;
                case LOAD_GAME:
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(ChessPieceImpl.class, new ChessPieceDeserializer());
                    gsonBuilder.registerTypeAdapter(ChessPosition.class, new ChessPositionDeserializer());
                    ServerMessageLoadGame loadGame = gsonBuilder.create().fromJson(message, ServerMessageLoadGame.class);
                    MenuBase.chessGame = loadGame.game;
                    BoardDisplay.displayBoard();
                    if (MenuBase.chessGame.isInCheckmate(ChessGame.TeamColor.valueOf(MenuBase.playerColor.toUpperCase()))) {
                        System.out.println("Checkmate!");
                    } else if (MenuBase.chessGame.isInCheck(ChessGame.TeamColor.valueOf(MenuBase.playerColor.toUpperCase()))) {
                        System.out.println("Check!");
                    } else if (MenuBase.chessGame.isInStalemate(ChessGame.TeamColor.valueOf(MenuBase.playerColor.toUpperCase()))) {
                        System.out.println("Stalemate!");
                    }
                    if (MenuBase.getInstance().isMyTurn()) {
                        System.out.println("Your turn!");
                    } else {
                        System.out.println("Opponent's turn.");
                    }
                    System.out.print(">>> ");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error in handling websocket message: " + e.getMessage());
        }
        MenuBase.socketResponded = true;
    }
}

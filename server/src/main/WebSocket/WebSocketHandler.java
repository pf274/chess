package WebSocket;

import DAO.DataAccessException;
import Models.Game;
import Services.GameDataService;
import Services.LoginService;
import Services.ServiceException;
import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializer.ChessPieceDeserializer;
import deserializer.ChessPositionDeserializer;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import serverMessages.ServerMessageError;
import serverMessages.ServerMessageLoadGame;
import serverMessages.ServerMessageNotification;
import userCommands.*;
import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final GameDataService gameDataService;

    private final LoginService loginService;
    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Websocket message: " + message);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessPiece.class, new ChessPieceDeserializer());
        gsonBuilder.registerTypeAdapter(ChessPosition.class, new ChessPositionDeserializer());
        UserGameCommand data = gsonBuilder.create().fromJson(message, UserGameCommand.class);
        String authString = data.getAuthString();
        String username = getUsernameFromAuthString(authString);
        try {
            switch (data.getCommandType()) {
                case JOIN_PLAYER:
                case JOIN_OBSERVER:
                    UserGameCommandJoinPlayer joinPlayerCommand = gsonBuilder.create().fromJson(message, UserGameCommandJoinPlayer.class);
                    connect(joinPlayerCommand.gameID, joinPlayerCommand.playerColor, username, session);
                    break;
                case LEAVE:
                    UserGameCommandLeave leaveCommand = gsonBuilder.create().fromJson(message, UserGameCommandLeave.class);
                    disconnect(username, leaveCommand.gameID);
                    break;
                case RESIGN:
                    UserGameCommandResign resignCommand = gsonBuilder.create().fromJson(message, UserGameCommandResign.class);
                    resign(username, resignCommand.gameID);
                    break;
                case MAKE_MOVE:
                    UserGameCommandMakeMove makeMoveCommand = gsonBuilder.create().fromJson(message, UserGameCommandMakeMove.class);
                    attemptMove(username, makeMoveCommand.gameID, makeMoveCommand.move);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        System.out.println("Websocket error: " + error);
    }


    public void connect(int gameID, chess.ChessGame.TeamColor playerColor, String username, Session session) {
        try {
            connectionManager.addConnection(gameID, session, username);
            Game game = gameDataService.getGame(gameID);
            String message = username + " has connected to the game";
            if (playerColor == ChessGame.TeamColor.WHITE) {
                message = message + " as white.";
            } else if (playerColor == ChessGame.TeamColor.BLACK) {
                message = message + " as black.";
            } else {
                message = message + " as an observer.";
            }
            ServerMessageNotification notification = new ServerMessageNotification(message);
            ServerMessageLoadGame loadGame = new ServerMessageLoadGame(game.game);
            connectionManager.broadcastMessageToOthers(username, gameID, notification);
            connectionManager.sendMessage(username, gameID, loadGame);
        } catch (ServiceException e) {
            ServerMessageError error = new ServerMessageError("Error: invalid game");
            connectionManager.sendMessage(username, gameID, error);
        }
    }


    public void disconnect(String username, int gameID) {
        try {
            Game loadedGame = gameDataService.getGame(gameID);
            if (Objects.equals(loadedGame.whiteUsername, username)) {
                loadedGame.whiteUsername = null;
                gameDataService.saveGame(loadedGame);
            } else if (Objects.equals(loadedGame.blackUsername, username)) {
                loadedGame.blackUsername = null;
                gameDataService.saveGame(loadedGame);
            }
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
        ServerMessageNotification notification = new ServerMessageNotification(username + " has disconnected from the game.");
        connectionManager.broadcastMessageToOthers(username, gameID, notification);
        connectionManager.removeConnection(gameID, username);
    }
    public void attemptMove(String username, int gameID, ChessMove move) throws IOException {
        try {
            Game loadedGame = gameDataService.getGame(gameID);
            ChessPositionImpl startingPosition = (ChessPositionImpl) move.getStartPosition();
            ChessPositionImpl endingPosition = (ChessPositionImpl) move.getEndPosition();
            ChessMoveImpl chessMove = new ChessMoveImpl(startingPosition, endingPosition, null);
            loadedGame.game.makeMove(chessMove);
            gameDataService.saveGame(loadedGame);
            ServerMessageLoadGame loadGameMessage = new ServerMessageLoadGame(loadedGame.game);
            connectionManager.broadcastMessage(gameID, loadGameMessage);

        } catch (ServiceException | InvalidMoveException e) {
            ServerMessageError error = new ServerMessageError("Error: invalid move");
            connectionManager.sendMessage(username, gameID, error);
        }
    }

    public void resign(String username, int gameID) {
        ServerMessageNotification resignNotification = new ServerMessageNotification(username + " has resigned.");
        try {
            Game loadedGame = gameDataService.getGame(gameID);
            loadedGame.gameOver = true;
            gameDataService.saveGame(loadedGame);
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
        connectionManager.broadcastMessageToOthers(username, gameID, resignNotification);
    }

    public WebSocketHandler(GameDataService gameDataService, LoginService loginService) {
        this.gameDataService = gameDataService;
        this.loginService = loginService;
    }

    public String getUsernameFromAuthString(String authString) {
        try {
            return loginService.authDAO.getAuthToken(authString).username;
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

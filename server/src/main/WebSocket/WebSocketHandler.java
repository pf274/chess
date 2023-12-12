package WebSocket;

import DAO.DataAccessException;
import Models.AuthToken;
import Models.Game;
import Services.GameDataService;
import Services.LoginService;
import Services.ServiceException;
import chess.*;
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
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final GameDataService gameDataService;

    private final LoginService loginService;
    private final ConnectionManager connectionManager = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("Websocket incoming message: " + message);
        // make gson builder
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessPiece.class, new ChessPieceDeserializer());
        gsonBuilder.registerTypeAdapter(ChessPosition.class, new ChessPositionDeserializer());
        // check authorization
        UserGameCommand data = gsonBuilder.create().fromJson(message, UserGameCommand.class);
        String authString = data.getAuthString();
        String username = getUsernameFromAuthString(authString);
        if (username == null) {
            ServerMessageError error = new ServerMessageError("Error: invalid auth token");
            connectionManager.sendMessageToSession(session, error);
            return;
        }
        // handle message based on type
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
            default:
                System.out.println("Error: invalid command type");
        }
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        System.out.println("Websocket error: " + error);
    }


    public void connect(int gameID, chess.ChessGame.TeamColor playerColor, String username, Session session) {
        try {
            Game game = gameDataService.getGame(gameID);
            // check for errors
            if (game == null) {
                ServerMessageError error = new ServerMessageError("Error: invalid game");
                connectionManager.sendMessageToSession(session, error);
                return;
            }
            if (playerColor != null) {
                if (playerColor.toString().equalsIgnoreCase("white") && !Objects.equals(game.whiteUsername, username)) {
                    ServerMessageError error = new ServerMessageError("Error: wrong color");
                    connectionManager.sendMessageToSession(session, error);
                    return;
                }
                if (playerColor.toString().equalsIgnoreCase("black") && !Objects.equals(game.blackUsername, username)) {
                    ServerMessageError error = new ServerMessageError("Error: wrong color");
                    connectionManager.sendMessageToSession(session, error);
                    return;
                }
            }
            // send notifications
            connectionManager.addConnection(gameID, session, username);
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
            // check for errors
            if (loadedGame == null) {
                ServerMessageError error = new ServerMessageError("Error: invalid game");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            // remove user from the game
            boolean disconnected = false;
            if (Objects.equals(loadedGame.whiteUsername, username)) {
                loadedGame.whiteUsername = null;
                gameDataService.saveGame(loadedGame);
                disconnected = true;
            } else if (Objects.equals(loadedGame.blackUsername, username)) {
                loadedGame.blackUsername = null;
                gameDataService.saveGame(loadedGame);
                disconnected = true;
            }
            if (disconnected) {
                ServerMessageNotification notification = new ServerMessageNotification(username + " has disconnected from the game.");
                connectionManager.broadcastMessageToOthers(username, gameID, notification);
                connectionManager.removeConnection(gameID, username);
            }
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }
    public void attemptMove(String username, int gameID, ChessMove move) {
        try {
            Game loadedGame = gameDataService.getGame(gameID);
            // check for errors
            if (loadedGame == null) {
                ServerMessageError error = new ServerMessageError("Error: invalid game");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            if (loadedGame.game.getTeamTurn() == ChessGame.TeamColor.WHITE && !Objects.equals(loadedGame.whiteUsername, username)) {
                ServerMessageError error = new ServerMessageError("Error: not your turn");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            if (loadedGame.game.getTeamTurn() == ChessGame.TeamColor.BLACK && !Objects.equals(loadedGame.blackUsername, username)) {
                ServerMessageError error = new ServerMessageError("Error: not your turn");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            if (loadedGame.gameOver) {
                ServerMessageError error = new ServerMessageError("Error: game already over");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            // make the move
            loadedGame.game.makeMove(move);
            // check if the game is over
            boolean checkmated = loadedGame.game.isInCheckmate(loadedGame.game.getTeamTurn() == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
            boolean inCheck = loadedGame.game.isInCheck(loadedGame.game.getTeamTurn() == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
            boolean stalemate = loadedGame.game.isInStalemate(loadedGame.game.getTeamTurn() == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
            if (checkmated) {
                loadedGame.gameOver = true;
                ServerMessageNotification checkmateNotification = new ServerMessageNotification("Checkmate! " + (loadedGame.game.getTeamTurn() == ChessGame.TeamColor.WHITE ? loadedGame.whiteUsername : loadedGame.blackUsername) + " wins!");
                connectionManager.broadcastMessage(gameID, checkmateNotification);
            } else if (inCheck) {
                ServerMessageNotification checkNotification = new ServerMessageNotification("Check!");
                connectionManager.broadcastMessage(gameID, checkNotification);
            } else if (stalemate) {
                loadedGame.gameOver = true;
                ServerMessageNotification stalemateNotification = new ServerMessageNotification("Stalemate!");
                connectionManager.broadcastMessage(gameID, stalemateNotification);
            }
            // save the game
            gameDataService.saveGame(loadedGame);
            // send messages to all players
            ServerMessageLoadGame loadGameMessage = new ServerMessageLoadGame(loadedGame.game);
            connectionManager.broadcastMessage(gameID, loadGameMessage);
            ServerMessageNotification playerMovedNotification = new ServerMessageNotification(username + " has moved.");
            connectionManager.broadcastMessageToOthers(username, gameID, playerMovedNotification);
        } catch (ServiceException e) {
            System.out.println("Error making move: " + e.getMessage());
        } catch (InvalidMoveException e) {
            ServerMessageError error = new ServerMessageError("Error: invalid move");
            connectionManager.sendMessage(username, gameID, error);
        }
    }

    public void resign(String username, int gameID) {
        ServerMessageNotification resignNotification = new ServerMessageNotification(username + " has resigned.");
        try {
            Game loadedGame = gameDataService.getGame(gameID);
            // check for errors
            if (loadedGame == null) {
                ServerMessageError error = new ServerMessageError("Error: invalid game");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            if (loadedGame.gameOver) {
                ServerMessageError error = new ServerMessageError("Error: game already over");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            if (!Objects.equals(loadedGame.whiteUsername, username) && !Objects.equals(loadedGame.blackUsername, username)) {
                ServerMessageError error = new ServerMessageError("Error: not playing in this game");
                connectionManager.sendMessage(username, gameID, error);
                return;
            }
            // send messages to all players
            loadedGame.gameOver = true;
            gameDataService.saveGame(loadedGame);
            connectionManager.broadcastMessage(gameID, resignNotification);
        } catch (ServiceException e) {
            System.out.println("Error resigning: " + e.getMessage());
        }

    }

    public WebSocketHandler(GameDataService gameDataService, LoginService loginService) {
        this.gameDataService = gameDataService;
        this.loginService = loginService;
    }

    public String getUsernameFromAuthString(String authString) {
        try {
            AuthToken authToken = loginService.authDAO.getAuthToken(authString);
            if (authToken == null) {
                return null;
            }
            return authToken.username;
        } catch (DataAccessException e) {
            System.out.println("Error getting username from auth string: " + e.getMessage());
            return null;
        }
    }
}

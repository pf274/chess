package userCommands;

public class UserGameCommandJoinObserver extends UserGameCommand {

    public final int gameID;

    public UserGameCommandJoinObserver(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
    }

    protected CommandType commandType = CommandType.JOIN_OBSERVER;
}

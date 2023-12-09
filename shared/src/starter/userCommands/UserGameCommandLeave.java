package userCommands;

public class UserGameCommandLeave extends UserGameCommand {

    public final int gameID;
    public UserGameCommandLeave(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
    }
    protected CommandType commandType = CommandType.LEAVE;

}

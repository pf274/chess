package userCommands;

public class UserGameCommandResign extends UserGameCommand {

    public final int gameID;
    public UserGameCommandResign(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
    }
    protected CommandType commandType = CommandType.LEAVE;

}

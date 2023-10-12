package server.Responses;

import server.Models.Game;

import java.util.ArrayList;

/**
 * A response for when a user lists games
 */
public class ListGamesResponse extends APIResponse {
    /**
     * Creates a new ListGamesResponse
     * @param games The list of games
     */
    public ListGamesResponse(ArrayList<Game> games) {
        // TODO: handle this
        super(200, "should show all the games");
    }
}

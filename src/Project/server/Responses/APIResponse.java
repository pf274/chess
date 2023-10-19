package server.Responses;

import com.google.gson.Gson;
import java.util.HashMap;

/**
 * A base class for API responses
 */
public class APIResponse {

    /**
     * The status code of the response
     */
    public int statusCode;

    /**
     * The status message of the response
     */
    public String statusMessage;

    /**
     * Creates a new APIResponse
     * @param statusCode The status code of the response
     * @param responseValues The response values
     */
    public APIResponse(int statusCode, HashMap responseValues) {
        // convert to json with gson
        Gson gson = new Gson();
        this.statusMessage = gson.toJson(responseValues);
        this.statusCode = statusCode;
    }

}

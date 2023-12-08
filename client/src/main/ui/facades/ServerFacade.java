package ui.facades;

import Responses.APIResponse;
import Responses.ResponseMapper;
import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ServerFacade {
    private static ServerFacade instance;  // Singleton instance

    // Public method to access the singleton instance
    public static ServerFacade getInstance() {
        if (instance == null) {
            instance = new ServerFacade();
        }
        return instance;
    }

    public APIResponse login(String username, String password) {
        var path = "/session";
        HashMap<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        return this.makeRequest("POST", path, body, null);
    }

    public APIResponse register(String username, String password, String email) {
        var path = "/user";
        HashMap<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        return this.makeRequest("POST", path, body, null);
    }

    public APIResponse logout(String authToken) {
        var path = "/session";
        return this.makeRequest("DELETE", path, null, authToken);
    }

    public APIResponse createGame(String gameName, String authToken) {
        var path = "/game";
        HashMap<String, String> body = new HashMap<>();
        body.put("gameName", gameName);
        return this.makeRequest("POST", path, body, authToken);
    }

    public APIResponse clearDatabase() {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, null);
    }

    public APIResponse listGames(String authToken) {
        var path = "/game";
        return this.makeRequest("GET", path, null, authToken);
    }

    public APIResponse joinGame(String authToken, int gameID, String playerColor) {
        var path = "/game";
        HashMap<String, String> body = new HashMap<>();
        body.put("gameID", Integer.toString(gameID));
        if (playerColor != null) {
            body.put("playerColor", playerColor);
        }
        return this.makeRequest("PUT", path, body, authToken);
    }

    public APIResponse leaveGame(String authToken, int gameID) {
        var path = "/game";
        HashMap<String, String> body = new HashMap<>();
        body.put("gameID", Integer.toString(gameID));
        return this.makeRequest("DELETE", path, body, authToken);
    }
    private APIResponse makeRequest(String method, String path, Object body, String authToken) {
        try {

            String serverUrl = "http://localhost:8080";
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", "Bearer " + authToken);
            }
            writeBody(body, http);
            http.connect();
            APIResponse error = errorIfNotSuccessful(http);
            if (error != null) {
                return error;
            }
            return readBody(http);
        } catch (Exception ex) {
            return ResponseMapper.exceptionResponse(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private APIResponse errorIfNotSuccessful(HttpURLConnection http) throws IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            return ResponseMapper.exceptionResponse(status, "failure: " + status);
        }
        return null;
    }

    private static APIResponse readBody(HttpURLConnection http) throws IOException {
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                HashMap parsedBody = new Gson().fromJson(reader, HashMap.class);
                return new APIResponse(200, parsedBody);
            }
        }
        return ResponseMapper.exceptionResponse(http.getResponseCode(), "no body");
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }



}
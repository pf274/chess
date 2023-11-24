package ui.menus;

import Models.AuthToken;
import Responses.APIResponse;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class APICaller {

    private static AuthToken authToken = null;

    private APICaller(AuthToken authToken) {
        APICaller.authToken = authToken;
    }

    public static APICaller withAuth(AuthToken authToken) {
        return new APICaller(authToken);
    }
    public static APIResponse get(String path, Map<String, String> queryParams) throws Exception {
        URI uri = buildUri(path, queryParams);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        return performCall(connection);
    }

    public static APIResponse get(String path) throws Exception {
        URI uri = buildUri(path, null);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        return performCall(connection);
    }

    public static APIResponse post(String path, Map body, Map<String, String> queryParams) throws Exception {
        URI uri = buildUri(path, queryParams);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        writeRequestBody(new Gson().toJson(body), connection);
        return performCall(connection);
    }

    public static APIResponse post(String path, Map body) throws Exception {
        URI uri = buildUri(path, null);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        writeRequestBody(new Gson().toJson(body), connection);
        return performCall(connection);
    }

    public static APIResponse delete(String path, Map<String, String> queryParams) throws Exception {
        URI uri = buildUri(path, queryParams);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("DELETE");
        return performCall(connection);
    }

    public static APIResponse delete(String path) throws Exception {
        URI uri = buildUri(path, null);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("DELETE");
        return performCall(connection);
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws Exception {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private static APIResponse performCall(HttpURLConnection connection) throws Exception {
        try {
            // Add Authorization header with bearer token
            if (authToken != null && !authToken.authToken.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + authToken.authToken);
            }
            connection.connect();
            InputStream respBody = connection.getInputStream();
            InputStreamReader respBodyReader = new InputStreamReader(respBody);
            HashMap parsedBody = new Gson().fromJson(respBodyReader, HashMap.class);
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return new APIResponse(responseCode, parsedBody);
        } catch (Exception e) {
            InputStream respBody = connection.getErrorStream();
            try {
                InputStreamReader respBodyReader = new InputStreamReader(respBody);
                HashMap parsedBody = new Gson().fromJson(respBodyReader, HashMap.class);
                return new APIResponse(connection.getResponseCode(), parsedBody);
            } catch (Exception e2) {
                HashMap<String, String> unknownError = new HashMap<>();
                unknownError.put("error", e.getMessage());
                return new APIResponse(500, unknownError);
            }
        }
    }

    private static URI buildUri(String path, Map<String, String> queryParams) throws Exception {
        StringBuilder uriBuilder = new StringBuilder("http://localhost:8080/" + path);

        if (queryParams != null && !queryParams.isEmpty()) {
            uriBuilder.append("?");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                String value = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
                uriBuilder.append(key).append("=").append(value).append("&");
            }
            // Remove the trailing "&"
            uriBuilder.setLength(uriBuilder.length() - 1);
        }

        return new URI(uriBuilder.toString());
    }
}

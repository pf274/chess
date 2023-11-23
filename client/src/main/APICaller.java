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
        connection.connect();
        try {
            InputStream respBody = connection.getInputStream();
            InputStreamReader respBodyReader = new InputStreamReader(respBody);
            HashMap parsedBody = new Gson().fromJson(respBodyReader, HashMap.class);
            return new APIResponse(connection.getResponseCode(), parsedBody);
        } catch (Exception e) {
            InputStream respBody = connection.getErrorStream();
            InputStreamReader respBodyReader = new InputStreamReader(respBody);
            HashMap parsedBody = new Gson().fromJson(respBodyReader, HashMap.class);
            return new APIResponse(connection.getResponseCode(), parsedBody);
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

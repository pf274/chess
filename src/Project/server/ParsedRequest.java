package server;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import server.APIHandlers.HandlerBase;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to parse an HTTP request.
 */
public class ParsedRequest {

    /**
     * The HTTP method of the request.
     */
    private final HandlerBase.method method;

    /**
     * The path of the request.
     */
    private final String path;

    /**
     * The body of the request.
     */
    private final Map<String, String> body;

    /**
     * The request headers.
     */
    private final Headers headers;


    /**
     * Gets the body of the request.
     * @return the body as a map of key-value pairs
     */
    public Map<String, String> getBody() {
        return body;
    }

    /**
     * Gets the HTTP method of the request.
     * @return the HTTP method
     */
    public HandlerBase.method getMethod() {
        return method;
    }

    /**
     * Gets the path of the request.
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the request headers.
     * @return the request headers
     */
    public Headers getHeaders() {
        return headers;
    }

    /**
     * Gets the auth token from the request headers.
     * @return the auth token
     */
    public String getAuthToken() {
        return headers.getFirst("Authorization");
    }

    /**
     * Creates a new ParsedRequest with the given HTTP exchange.
     * @param exchange the HTTP exchange
     */
    public ParsedRequest(HttpExchange exchange) {
        // path
        this.path = String.valueOf(exchange.getRequestURI());
        // method
        String rawMethod = exchange.getRequestMethod();
        switch (rawMethod) {
            case "POST" -> this.method = HandlerBase.method.POST;
            case "PUT" -> this.method = HandlerBase.method.PUT;
            case "DELETE" -> this.method = HandlerBase.method.DELETE;
            default -> this.method = HandlerBase.method.GET;
        }
        // body
        this.body = parseBody(exchange.getRequestBody());
        // headers
        this.headers = exchange.getRequestHeaders();
    }

    /**
     * Parses the body of the request into a map of key-value pairs.
     * @param bodyStream the body of the request
     * @return a map of key-value pairs
     */
    private Map<String, String> parseBody(InputStream bodyStream) {
        try {
            Map<String, String> formData = new HashMap<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(bodyStream));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    formData.put(parts[0], parts[1]);
                }
                line = reader.readLine();
            }
            return formData;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

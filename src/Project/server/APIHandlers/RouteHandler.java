package server.APIHandlers;
import server.ParsedRequest;
import server.Responses.APIResponse;

@FunctionalInterface
public interface RouteHandler {
    APIResponse handle(ParsedRequest request) throws APIException;
}

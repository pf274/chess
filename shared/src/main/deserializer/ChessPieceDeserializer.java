package deserializer;

import chess.ChessGame;
import chess.ChessPieceImpl;
import chess.Pieces.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessPieceDeserializer implements JsonSerializer<ChessPieceImpl>, JsonDeserializer<ChessPieceImpl> {

    @Override
    public JsonElement serialize(ChessPieceImpl src, Type srcType, JsonSerializationContext context) throws JsonParseException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive(src.getPieceType().toString()));
        jsonObject.add("color", new JsonPrimitive(src.getTeamColor().toString()));
        return jsonObject;
    }

    @Override
    public ChessPieceImpl deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String pieceType = jsonObject.get("type").getAsString();
        String pieceColor = jsonObject.get("color").getAsString();
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(pieceColor);
        return switch (pieceType) {
            case "KNIGHT" -> new Knight(teamColor);
            case "BISHOP" -> new Bishop(teamColor);
            case "ROOK" -> new Rook(teamColor);
            case "QUEEN" -> new Queen(teamColor);
            case "KING" -> new King(teamColor);
            default -> new Pawn(teamColor);
        };
    }
}

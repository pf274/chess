package deserializer;

import chess.ChessGame;
import chess.ChessPieceImpl;
import chess.ChessPosition;
import chess.ChessPositionImpl;
import chess.Pieces.*;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessPositionDeserializer implements JsonSerializer<ChessPosition>, JsonDeserializer<ChessPositionImpl> {

    @Override
    public JsonElement serialize(ChessPosition src, Type srcType, JsonSerializationContext context) throws JsonParseException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("row", new JsonPrimitive(src.getRow()));
        jsonObject.add("column", new JsonPrimitive(src.getColumn()));
        return jsonObject;
    }

    @Override
    public ChessPositionImpl deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        try {
            int row = jsonObject.get("row").getAsInt();
            int column = jsonObject.get("column").getAsInt();
            return new ChessPositionImpl(row, column);
        } catch (Exception e) {
//            System.out.println("Error 1: " + e.getMessage());
        }
        return null;
    }
}

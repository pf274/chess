import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn extends ChessPieceImplementation {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        // check space ahead
        if (canMoveForward(board, myPosition)) {

        }

        return null;
    }

    private boolean canMoveForward(ChessBoard board, ChessPosition startingPosition) {
        int myRow = startingPosition.getRow();
        int myCol = startingPosition.getColumn();
        int newRow = myRow + (color == ChessGame.TeamColor.WHITE ? 1 : -1);
        if (newRow > 8 || newRow < 1) {
            return false;
        }
        var positionAhead = new ChessPositionImplementation(newRow, myCol);
        var targetPiece = board.getPiece(positionAhead);
        if (targetPiece == null) {
            return true;
        }
        return targetPiece.getTeamColor() != color;
    }

    public Pawn(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        super(type, color);
    }
}

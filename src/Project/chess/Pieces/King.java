package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class King extends ChessPieceImpl {
    public King(ChessGame.TeamColor color) {
        super(PieceType.KING, color);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        HashSet<ChessMove> validMoves = new HashSet<>();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset += (rowOffset == 0) ? 2 : 1) {
                int newRow = myRow + rowOffset;
                int newCol = myCol + colOffset;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    var endPosition = new ChessPositionImpl(newRow, newCol);
                    var pieceFound = board.getPiece(endPosition);
                    if (pieceFound == null) {
                        validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                    } else if (pieceFound.getTeamColor() != color) {
                        validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                    }
                }
            }
        }
        return validMoves;
    }
}

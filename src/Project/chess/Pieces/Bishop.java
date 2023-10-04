package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class Bishop extends ChessPieceImpl {
    public Bishop(ChessGame.TeamColor color) {
        super(PieceType.BISHOP, color);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        HashSet<ChessMove> validMoves = new HashSet<>();
        // look in all four diagonal directions
        for (int verticalDirection = -1; verticalDirection <= 1; verticalDirection += 2) {
            for (int horizontalDirection = -1; horizontalDirection <= 1; horizontalDirection += 2) {
                boolean directionFinished = false;
                for (int offset = 1; offset <= 7 && !directionFinished; offset++) {
                    int newRow = myRow + verticalDirection * offset;
                    int newCol = myCol + horizontalDirection * offset;
                    if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                        var endPosition = new ChessPositionImpl(newRow, newCol);
                        var pieceFound = board.getPiece(endPosition);
                        if (pieceFound != null) {
                            if (pieceFound.getTeamColor() != color) {
                                validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                            }
                            directionFinished = true;
                        } else {
                            validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                        }
                    }
                }
            }
        }
        validMoves.removeIf(validMove -> !isSafeMove((ChessBoardImpl) board, color, validMove));
        return validMoves;
    }
}

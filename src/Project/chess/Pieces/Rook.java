package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class Rook extends ChessPieceImpl {
    public Rook(ChessGame.TeamColor color) {
        super(PieceType.ROOK, color);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        HashSet<ChessMove> validMoves = new HashSet<>();
        // look in all four diagonal directions
        for (int verticalDirection = -1; verticalDirection <= 1; verticalDirection += 1) {
            if (verticalDirection == 0) {
                // check horizontally
                for (int horizontalDirection = -1; horizontalDirection <= 1; horizontalDirection += 2) {
                    boolean directionFinished = false;
                    for (int offset = 1; offset <= 7 && !directionFinished; offset++) {
                        int newCol = myCol + horizontalDirection * offset;
                        if (newCol >= 1 && newCol <= 8) {
                            var endPosition = new ChessPositionImpl(myRow, newCol);
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
            } else {
                // complete this vertical check
                boolean directionFinished = false;
                for (int offset = 1; offset <= 7 && !directionFinished; offset++) {
                    int newRow = myRow + verticalDirection * offset;
                    if (newRow >= 1 && newRow <= 8) {
                        var endPosition = new ChessPositionImpl(newRow, myCol);
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
        return validMoves;
    }
}

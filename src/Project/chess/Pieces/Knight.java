package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class Knight extends ChessPieceImpl {
    public Knight(ChessGame.TeamColor color) {
        super(PieceType.KNIGHT, color);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        HashSet<ChessMove> validMoves = new HashSet<>();
        // check top/bottom spaces
        for (int rowOffset = -2; rowOffset <= 2; rowOffset += 4) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset += 2) {
                int newRow = myRow + rowOffset;
                int newCol = myCol + columnOffset;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    var endPosition = new ChessPositionImpl(newRow, newCol);
                    var pieceFound = board.getPiece(endPosition);
                    if (pieceFound != null) {
                        if (pieceFound.getTeamColor() != color) {
                            validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                        }
                    } else {
                        validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                    }
                }
            }
        }
        // check left/right spaces
        for (int rowOffset = -1; rowOffset <= 1; rowOffset += 2) {
            for (int columnOffset = -2; columnOffset <= 2; columnOffset += 4) {
                int newRow = myRow + rowOffset;
                int newCol = myCol + columnOffset;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    var endPosition = new ChessPositionImpl(newRow, newCol);
                    var pieceFound = board.getPiece(endPosition);
                    if (pieceFound != null) {
                        if (pieceFound.getTeamColor() != color) {
                            validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                        }
                    } else {
                        validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                    }
                }
            }
        }
        return validMoves;
    }
}

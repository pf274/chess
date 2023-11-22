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
        HashSet<ChessPosition> potentialPositions = new HashSet<>();
        potentialPositions.add(new ChessPositionImpl(myRow - 2, myCol - 1));
        potentialPositions.add(new ChessPositionImpl(myRow - 2, myCol + 1));
        potentialPositions.add(new ChessPositionImpl(myRow - 1, myCol - 2));
        potentialPositions.add(new ChessPositionImpl(myRow - 1, myCol + 2));
        potentialPositions.add(new ChessPositionImpl(myRow + 1, myCol - 2));
        potentialPositions.add(new ChessPositionImpl(myRow + 1, myCol + 2));
        potentialPositions.add(new ChessPositionImpl(myRow + 2, myCol - 1));
        potentialPositions.add(new ChessPositionImpl(myRow + 2, myCol + 1));
        assessPositions(board, myPosition, validMoves, potentialPositions);
        return validMoves;
    }
}

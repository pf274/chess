package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class Queen extends ChessPieceImpl {
    public Queen(ChessGame.TeamColor color) {
        super(PieceType.QUEEN, color);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        assessCardinals(board, myPosition, validMoves);
        assessDiagonals(board, myPosition, validMoves);
        return validMoves;
    }
}

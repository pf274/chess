package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Bishop extends ChessPieceImpl {
    public Bishop(ChessGame.TeamColor color) {
        super(PieceType.BISHOP, color);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> validMoves = new HashSet<>();
        assessDiagonals(board, myPosition, validMoves);
        return validMoves;
    }
}

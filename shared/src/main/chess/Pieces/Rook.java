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
        HashSet<ChessMove> validMoves = new HashSet<>();
        assessCardinals(board, myPosition, validMoves);
        return validMoves;
    }
}

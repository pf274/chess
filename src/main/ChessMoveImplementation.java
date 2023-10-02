import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessMoveImplementation implements ChessMove {

    ChessPosition startingPosition;
    ChessPosition endingPosition;
    /**
     * @return ChessPosition of starting location
     */
    @Override
    public ChessPosition getStartPosition() {
        return startingPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    @Override
    public ChessPosition getEndPosition() {
        return endingPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return null;
    }
}

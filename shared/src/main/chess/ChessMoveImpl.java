package chess;

import java.util.Objects;

public class ChessMoveImpl implements ChessMove {

    /**
     * The starting position
     */
    ChessPosition startingPosition;
    /**
     * The ending position
     */
    ChessPosition endingPosition;
    /**
     * The piece to promote to
     */
    ChessPiece.PieceType promotionPiece;

    /**
     * Constructor for a chess move
     * @param startingPosition the starting position
     * @param endingPosition the ending position
     * @param promotionPiece the piece to promote to
     */
    public ChessMoveImpl(ChessPosition startingPosition, ChessPosition endingPosition, ChessPiece.PieceType promotionPiece) {
        this.startingPosition = startingPosition;
        this.endingPosition = endingPosition;
        this.promotionPiece = promotionPiece;
    }
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
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMoveImpl chessMove = (ChessMoveImpl) o;
        return Objects.equals(startingPosition, chessMove.startingPosition) && Objects.equals(endingPosition, chessMove.endingPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingPosition, endingPosition, promotionPiece);
    }
}

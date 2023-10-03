import chess.*;

import java.util.Collection;

public abstract class ChessPieceImplementation implements ChessPiece {

    public ChessGame.TeamColor color;
    public ChessPiece.PieceType type;

    public ChessPieceImplementation(ChessPiece.PieceType type, ChessGame.TeamColor color) {
        this.color = color;
        this.type = type;
    }

    /**
     * @return Which team this chess piece belongs to
     */
    @Override
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    @Override
    public PieceType getPieceType() {
        return type;
    }

    @Override
    abstract public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in danger
     *
     * @param board
     * @param myPosition
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves2(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case PAWN:

                break;
            case ROOK:

                break;
            case BISHOP:

                break;
            case KNIGHT:

                break;
            case QUEEN:

                break;
            case KING:

                break;
            default:
                // TODO: THIS SHOULD NEVER HAPPEN
                break;
        }
        // TODO: pieceMoves
        return null;
    }
}

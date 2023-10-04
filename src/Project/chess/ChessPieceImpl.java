package chess;

import chess.Pieces.*;

import java.util.Collection;

public abstract class ChessPieceImpl implements ChessPiece {

    public ChessGame.TeamColor color;
    public ChessPiece.PieceType type;

    public ChessPieceImpl(ChessPiece.PieceType type, ChessGame.TeamColor color) {
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


    public static boolean isSafeMove(ChessBoardImpl board, ChessGame.TeamColor currentTurn, ChessMove potentialMove) {
        var fakeGame = new ChessGameImpl();
        var boardCopy = board.duplicate();
        fakeGame.setBoard(boardCopy);
        fakeGame.setTeamTurn(currentTurn);
        // do a manual move without checks, then see if the king is in check afterwards
        var selectedPiece = boardCopy.getPiece(potentialMove.getStartPosition());
        ChessPieceImpl replacedPiece = null;
        switch (selectedPiece.getPieceType()) {
            case KING -> replacedPiece = new King(currentTurn);
            case QUEEN -> replacedPiece = new Queen(currentTurn);
            case ROOK -> replacedPiece = new Rook(currentTurn);
            case BISHOP -> replacedPiece = new Bishop(currentTurn);
            case KNIGHT -> replacedPiece = new Knight(currentTurn);
            default -> replacedPiece = new Pawn(currentTurn);
        }
        // castling
        if (selectedPiece.getPieceType() == PieceType.KING) {
            if (Math.abs(potentialMove.getStartPosition().getColumn() - potentialMove.getEndPosition().getColumn()) > 1) {
                if (potentialMove.getEndPosition().getColumn() == 3) {
                    // move left knight
                    boardCopy.removePiece(new ChessPositionImpl(potentialMove.getEndPosition().getRow(), 1));
                    boardCopy.addPiece(new ChessPositionImpl(potentialMove.getEndPosition().getRow(), 4), new Rook(selectedPiece.getTeamColor()));
                } else {
                    // move right knight
                    boardCopy.removePiece(new ChessPositionImpl(potentialMove.getEndPosition().getRow(), 8));
                    boardCopy.addPiece(new ChessPositionImpl(potentialMove.getEndPosition().getRow(), 6), new Rook(selectedPiece.getTeamColor()));
                }
            }
        }
        boardCopy.removePiece(potentialMove.getStartPosition());
        boardCopy.addPiece(potentialMove.getEndPosition(), replacedPiece);
        // check to see if king is in check
        boolean dangerous = fakeGame.isInCheck(currentTurn);
        return !dangerous;
    }
}

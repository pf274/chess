package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class Pawn extends ChessPieceImpl {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        HashSet<ChessMove> validMoves = new HashSet<>();
        int newRow = myRow + (color == ChessGame.TeamColor.WHITE ? 1 : -1);
        var forwardPosition = new ChessPositionImpl(newRow, myCol);
        var forwardLeftPosition = new ChessPositionImpl(newRow, myCol - 1);
        var forwardRightPosition = new ChessPositionImpl(newRow, myCol + 1);
        var doubleForwardPosition = new ChessPositionImpl(myRow + (color == ChessGame.TeamColor.WHITE ? 2 : -2), myCol);
        // assess forward position
        if (forwardPosition.getRow() >= 1 && forwardPosition.getRow() <= 8) {
            if (board.getPiece(forwardPosition) == null) {
                addMoves(myPosition, validMoves, forwardPosition);
            }
        }
        ChessBoardImpl boardImpl = (ChessBoardImpl) board;
        // assess forward left position
        if (forwardLeftPosition.getRow() >= 1 && forwardLeftPosition.getRow() <= 8 && forwardLeftPosition.getColumn() >= 1) {
            assessAttack(board, myPosition, validMoves, forwardLeftPosition, boardImpl);
        }
        // assess forward right position
        if (forwardRightPosition.getRow() >= 1 && forwardRightPosition.getRow() <= 8 && forwardRightPosition.getColumn() <= 8) {
            assessAttack(board, myPosition, validMoves, forwardRightPosition, boardImpl);
        }
        // assess double forward position
        if ((myRow == 2 && color == ChessGame.TeamColor.WHITE) || (myRow == 7 && color == ChessGame.TeamColor.BLACK)) {
            if (board.getPiece(forwardPosition) == null && board.getPiece(doubleForwardPosition) == null) {
                validMoves.add(new ChessMoveImpl(myPosition, doubleForwardPosition, null));
            }
        }
        return validMoves;
    }

    private void assessAttack(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves, ChessPositionImpl forwardLeftPosition, ChessBoardImpl boardImpl) {
        boolean isEnPassantMove = boardImpl.enPassantMove != null && boardImpl.enPassantMove.getRow() == forwardLeftPosition.getRow() && boardImpl.enPassantMove.getColumn() == forwardLeftPosition.getColumn();
        if (board.getPiece(forwardLeftPosition) != null || isEnPassantMove) {
            if (isEnPassantMove || board.getPiece(forwardLeftPosition).getTeamColor() != color) {
                addMoves(myPosition, validMoves, forwardLeftPosition);
            }
        }
    }

    private void addMoves(ChessPosition myPosition, HashSet<ChessMove> validMoves, ChessPositionImpl forwardLeftPosition) {
        boolean hasReachedEndWhite = forwardLeftPosition.getRow() == 8 && color == ChessGame.TeamColor.WHITE;
        boolean hasReachedEndBlack = forwardLeftPosition.getRow() == 1 && color == ChessGame.TeamColor.BLACK;
        if (hasReachedEndWhite || hasReachedEndBlack) {
            validMoves.add(new ChessMoveImpl(myPosition, forwardLeftPosition, PieceType.KNIGHT));
            validMoves.add(new ChessMoveImpl(myPosition, forwardLeftPosition, PieceType.BISHOP));
            validMoves.add(new ChessMoveImpl(myPosition, forwardLeftPosition, PieceType.QUEEN));
            validMoves.add(new ChessMoveImpl(myPosition, forwardLeftPosition, PieceType.ROOK));
        } else {
            validMoves.add(new ChessMoveImpl(myPosition, forwardLeftPosition, null));
        }
    }

    public Pawn(ChessGame.TeamColor color) {
        super(PieceType.PAWN, color);
    }
}

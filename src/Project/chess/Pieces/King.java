package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.HashSet;

public class King extends ChessPieceImpl {
    public King(ChessGame.TeamColor color) {
        super(PieceType.KING, color);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();
        HashSet<ChessMove> validMoves = new HashSet<>();
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset += (rowOffset == 0) ? 2 : 1) {
                int newRow = myRow + rowOffset;
                int newCol = myCol + colOffset;
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    var endPosition = new ChessPositionImpl(newRow, newCol);
                    var pieceFound = board.getPiece(endPosition);
                    if (pieceFound == null) {
                        validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                    } else if (pieceFound.getTeamColor() != color) {
                        validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                    }
                }
            }
        }
        // castling moves
        var implBoard = (ChessBoardImpl) board;
        int castleRow = (color == ChessGame.TeamColor.WHITE ? 1 : 8);
        // left castle
        var position2 = new ChessPositionImpl(castleRow, 2);
        var position3 = new ChessPositionImpl(castleRow, 3);
        var position4 = new ChessPositionImpl(castleRow, 4);
        var position2Empty = implBoard.getPiece(position2) == null;
        var position3Empty = implBoard.getPiece(position3) == null;
        var position4Empty = implBoard.getPiece(position4) == null;
        if ((color == ChessGame.TeamColor.WHITE ? implBoard.whiteLeftCastlePossible : implBoard.blackLeftCastlePossible) && position2Empty && position3Empty && position4Empty) {
            validMoves.add(new ChessMoveImpl(myPosition, position3, null));
        }
        // right castle
        var position6 = new ChessPositionImpl(castleRow, 6);
        var position7 = new ChessPositionImpl(castleRow, 7);
        var position6Empty = implBoard.getPiece(position6) == null;
        var position7Empty = implBoard.getPiece(position7) == null;
        if ((color == ChessGame.TeamColor.WHITE ? implBoard.whiteRightCastlePossible : implBoard.blackRightCastlePossible) && position6Empty && position7Empty) {
            validMoves.add(new ChessMoveImpl(myPosition, position7, null));
        }
        return validMoves;
    }
}

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
        if(myPosition.getRow() == 8 || myPosition.getRow() == 1) {
            var implBoard = (ChessBoardImpl) board;
            int castleRow = (color == ChessGame.TeamColor.WHITE ? 1 : 8);
            var position1 = new ChessPositionImpl(castleRow, 1);
            var position1IsRook = implBoard.getPiece(position1) != null && implBoard.getPiece(position1).getPieceType() == PieceType.ROOK && implBoard.getPiece(position1).getTeamColor() == color;
            var position2 = new ChessPositionImpl(castleRow, 2);
            var position2Empty = implBoard.getPiece(position2) == null;
            var position3 = new ChessPositionImpl(castleRow, 3);
            var position3Empty = implBoard.getPiece(position3) == null;
            var position4 = new ChessPositionImpl(castleRow, 4);
            var position4Empty = implBoard.getPiece(position4) == null;
            var position5 = new ChessPositionImpl(castleRow, 5);
            var position5IsKing = implBoard.getPiece(position5) != null && implBoard.getPiece(position5).getPieceType() == PieceType.KING  && implBoard.getPiece(position5).getTeamColor() == color;
            var position6 = new ChessPositionImpl(castleRow, 6);
            var position6Empty = implBoard.getPiece(position6) == null;
            var position7 = new ChessPositionImpl(castleRow, 7);
            var position7Empty = implBoard.getPiece(position7) == null;
            var position8 = new ChessPositionImpl(castleRow, 8);
            var position8IsRook = implBoard.getPiece(position8) != null && implBoard.getPiece(position8).getPieceType() == PieceType.ROOK  && implBoard.getPiece(position8).getTeamColor() == color;
            // left castle
            if ((color == ChessGame.TeamColor.WHITE ? implBoard.whiteLeftCastlePossible : implBoard.blackLeftCastlePossible) && position2Empty && position3Empty && position4Empty && position1IsRook && position5IsKing) {
                validMoves.add(new ChessMoveImpl(myPosition, position3, null));
            }
            // right castle
            if ((color == ChessGame.TeamColor.WHITE ? implBoard.whiteRightCastlePossible : implBoard.blackRightCastlePossible) && position6Empty && position7Empty && position5IsKing && position8IsRook) {
                validMoves.add(new ChessMoveImpl(myPosition, position7, null));
            }
        }
        return validMoves;
    }
}

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
        // check all eight squares around the king
        HashSet<ChessPosition> potentialPositions = new HashSet<>();
        potentialPositions.add(new ChessPositionImpl(myRow - 1, myCol - 1));
        potentialPositions.add(new ChessPositionImpl(myRow - 1, myCol));
        potentialPositions.add(new ChessPositionImpl(myRow - 1, myCol + 1));
        potentialPositions.add(new ChessPositionImpl(myRow, myCol - 1));
        potentialPositions.add(new ChessPositionImpl(myRow, myCol + 1));
        potentialPositions.add(new ChessPositionImpl(myRow + 1, myCol - 1));
        potentialPositions.add(new ChessPositionImpl(myRow + 1, myCol));
        potentialPositions.add(new ChessPositionImpl(myRow + 1, myCol + 1));
        assessPositions(board, myPosition, validMoves, potentialPositions);
        // castling moves
        if(myPosition.getRow() == 8 || myPosition.getRow() == 1) {
            ChessPositionImpl[] positions = new ChessPositionImpl[8];
            int castleRow = (color == ChessGame.TeamColor.WHITE ? 1 : 8);
            for (int position = 0; position < 8; position++) {
                positions[position] = new ChessPositionImpl(castleRow, position + 1);
            }
            var implBoard = (ChessBoardImpl) board;
            var position1IsRook = implBoard.getPiece(positions[0]) != null && implBoard.getPiece(positions[0]).getPieceType() == PieceType.ROOK && implBoard.getPiece(positions[0]).getTeamColor() == color;
            var position2Empty = implBoard.getPiece(positions[1]) == null;
            var position3Empty = implBoard.getPiece(positions[2]) == null;
            var position4Empty = implBoard.getPiece(positions[3]) == null;
            var position5IsKing = implBoard.getPiece(positions[4]) != null && implBoard.getPiece(positions[4]).getPieceType() == PieceType.KING  && implBoard.getPiece(positions[4]).getTeamColor() == color;
            var position6Empty = implBoard.getPiece(positions[5]) == null;
            var position7Empty = implBoard.getPiece(positions[6]) == null;
            var position8IsRook = implBoard.getPiece(positions[7]) != null && implBoard.getPiece(positions[7]).getPieceType() == PieceType.ROOK  && implBoard.getPiece(positions[7]).getTeamColor() == color;
            // left castle
            if ((color == ChessGame.TeamColor.WHITE ? implBoard.whiteLeftCastlePossible : implBoard.blackLeftCastlePossible) && position2Empty && position3Empty && position4Empty && position1IsRook && position5IsKing) {
                validMoves.add(new ChessMoveImpl(myPosition, positions[2], null));
            }
            // right castle
            if ((color == ChessGame.TeamColor.WHITE ? implBoard.whiteRightCastlePossible : implBoard.blackRightCastlePossible) && position6Empty && position7Empty && position5IsKing && position8IsRook) {
                validMoves.add(new ChessMoveImpl(myPosition, positions[6], null));
            }
        }
        return validMoves;
    }
}

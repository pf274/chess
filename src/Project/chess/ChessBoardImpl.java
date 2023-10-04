package chess;

import chess.Pieces.*;

import java.util.Collection;
import java.util.HashSet;


public class ChessBoardImpl implements ChessBoard {

    public boolean whiteLeftCastlePossible = true;
    public boolean whiteRightCastlePossible = true;
    public boolean blackLeftCastlePossible = true;
    public boolean blackRightCastlePossible = true;

    public ChessPosition enPassantMove = null;

    private ChessPieceImpl[][] board = new ChessPieceImpl[8][8];

    public ChessBoardImpl duplicate() {
        var newBoard = new ChessBoardImpl();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPositionImpl(row, col);
                var piece = getPiece(position);
                if (piece != null) {
                    ChessPieceImpl newPiece;
                    switch (piece.getPieceType()) {
                        case ROOK -> newPiece = new Rook(piece.getTeamColor());
                        case KING -> newPiece = new King(piece.getTeamColor());
                        case QUEEN -> newPiece = new Queen(piece.getTeamColor());
                        case BISHOP -> newPiece = new Bishop(piece.getTeamColor());
                        case KNIGHT -> newPiece = new Knight(piece.getTeamColor());
                        default -> newPiece = new Pawn(piece.getTeamColor());
                    }
                    newBoard.addPiece(position, newPiece);
                }
            }
        }
        return newBoard;
    }

    public ChessBoardImpl() {}

    public ChessPieceImpl[][] getBoard() {
        return board;
    }

    public void setBoard(ChessPiece[][] board) {
        this.board = (ChessPieceImpl[][]) board;
        initializeCastling();
    }

    private void initializeCastling() {
        var whiteLeftRook = board[0][0];
        var whiteLeftRookGood = whiteLeftRook != null && whiteLeftRook.getPieceType() == ChessPiece.PieceType.ROOK && whiteLeftRook.getTeamColor() == ChessGame.TeamColor.WHITE;
        var whiteRightRook = board[0][7];
        var whiteRightRookGood = whiteRightRook != null && whiteRightRook.getPieceType() == ChessPiece.PieceType.ROOK && whiteRightRook.getTeamColor() == ChessGame.TeamColor.WHITE;
        var whiteKing = board[0][4];
        var whiteKingGood = whiteKing != null && whiteKing.getPieceType() == ChessPiece.PieceType.KING && whiteKing.getTeamColor() == ChessGame.TeamColor.WHITE;
        whiteLeftCastlePossible = whiteKingGood && whiteLeftRookGood;
        whiteRightCastlePossible = whiteKingGood && whiteRightRookGood;
        var blackLeftRook = board[7][0];
        var blackLeftRookGood = blackLeftRook != null && blackLeftRook.getPieceType() == ChessPiece.PieceType.ROOK && blackLeftRook.getTeamColor() == ChessGame.TeamColor.BLACK;
        var blackRightRook = board[7][7];
        var blackRightRookGood = blackRightRook != null && blackRightRook.getPieceType() == ChessPiece.PieceType.ROOK && blackRightRook.getTeamColor() == ChessGame.TeamColor.BLACK;
        var blackKing = board[7][4];
        var blackKingGood = blackKing != null && blackKing.getPieceType() == ChessPiece.PieceType.KING && blackKing.getTeamColor() == ChessGame.TeamColor.BLACK;
        blackLeftCastlePossible = blackKingGood && blackLeftRookGood;
        blackRightCastlePossible = blackKingGood && blackRightRookGood;

    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;
        board[row][column] = (ChessPieceImpl) piece;
    }

    public void removePiece(ChessPosition position) {
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;
        board[row][column] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that position
     */
    @Override
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;
        return board[row][column];
    }

    public void movePiece(ChessPosition startingPosition, ChessPosition endingPosition) {
        int startingRow = startingPosition.getRow() - 1;
        int startingCol = startingPosition.getColumn() - 1;
        ChessPiece selectedPiece = getPiece(startingPosition);
        int endingRow = endingPosition.getRow() - 1;
        int endingCol = endingPosition.getColumn() - 1;
        board[endingRow][endingCol] = (ChessPieceImpl) selectedPiece;
        board[startingRow][startingCol] = null;
    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    @Override
    public void resetBoard() {
        board = new ChessPieceImpl[8][8];
        // create chess pieces
        ChessPieceImpl whiteKnight = new Knight(ChessGame.TeamColor.WHITE);
        ChessPieceImpl blackKnight = new Knight(ChessGame.TeamColor.BLACK);
        ChessPieceImpl whitePawn = new Pawn(ChessGame.TeamColor.WHITE);
        ChessPieceImpl blackPawn = new Pawn(ChessGame.TeamColor.BLACK);
        ChessPieceImpl whiteRook = new Rook(ChessGame.TeamColor.WHITE);
        ChessPieceImpl blackRook = new Rook(ChessGame.TeamColor.BLACK);
        ChessPieceImpl whiteBishop = new Bishop(ChessGame.TeamColor.WHITE);
        ChessPieceImpl blackBishop = new Bishop(ChessGame.TeamColor.BLACK);
        ChessPieceImpl whiteKing = new King(ChessGame.TeamColor.WHITE);
        ChessPieceImpl blackKing = new King(ChessGame.TeamColor.BLACK);
        ChessPieceImpl whiteQueen = new Queen(ChessGame.TeamColor.WHITE);
        ChessPieceImpl blackQueen = new Queen(ChessGame.TeamColor.BLACK);
        // add the white special pieces
        board[0][0] = whiteRook;
        board[0][1] = whiteKnight;
        board[0][2] = whiteBishop;
        board[0][3] = whiteQueen;
        board[0][4] = whiteKing;
        board[0][5] = whiteBishop;
        board[0][6] = whiteKnight;
        board[0][7] = whiteRook;
        // add the black special pieces
        board[7][0] = blackRook;
        board[7][1] = blackKnight;
        board[7][2] = blackBishop;
        board[7][3] = blackQueen;
        board[7][4] = blackKing;
        board[7][5] = blackBishop;
        board[7][6] = blackKnight;
        board[7][7] = blackRook;
        // add the pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = whitePawn;
            board[6][i] = blackPawn;
        }
        // reset special moves
        whiteRightCastlePossible = true;
        whiteLeftCastlePossible = true;
        blackRightCastlePossible = true;
        blackLeftCastlePossible = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("   1 2 3 4 5 6 7 8 \n");
        for (int i = 7; i >= 0; i--) {
            sb.append(i + 1).append(" ");
            for (int j = 0; j < 8; j++) {
                sb.append("|");
                ChessPieceImpl piece = board[i][j];
                if (piece == null) {
                    sb.append(" ");
                } else {
                    switch (piece.getPieceType()) {
                        case ROOK -> sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? "r" : "R");
                        case BISHOP -> sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? "b" : "B");
                        case QUEEN -> sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? "q" : "Q");
                        case KING -> sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? "k" : "K");
                        case KNIGHT -> sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? "n" : "N");
                        default -> sb.append(piece.getTeamColor() == ChessGame.TeamColor.BLACK ? "p" : "P");
                    }
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }


    public Collection<ChessPosition> positionInDanger(ChessPosition testPosition, ChessGame.TeamColor teamColor) {
        HashSet<ChessPosition> perpetrators = new HashSet<>();
        // ensure that the position being tested is attackable
        boolean addedPiece = false;
        ChessPiece replacedPiece = null;
        if (getPiece(testPosition) == null) {
            addPiece(testPosition, new Pawn(teamColor));
            addedPiece = true;
        } else if (getPiece(testPosition).getTeamColor() != teamColor) {
            var oppositeColor = getPiece(testPosition).getTeamColor();
            var existingPiece = getPiece(testPosition);
            switch (existingPiece.getPieceType()) {
                case KING -> replacedPiece = new King(oppositeColor);
                case QUEEN -> replacedPiece = new Queen(oppositeColor);
                case ROOK -> replacedPiece = new Rook(oppositeColor);
                case BISHOP -> replacedPiece = new Bishop(oppositeColor);
                case KNIGHT -> replacedPiece = new Knight(oppositeColor);
                default -> replacedPiece = new Pawn(oppositeColor);
            }
            addPiece(testPosition, new Pawn(teamColor));
        }
        // evaluate potential attacks
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPositionImpl(row, col);
                var foundPiece = getPiece(position);
                if (foundPiece != null) {
                    if (foundPiece.getTeamColor() != teamColor) {
                        var validMoves = foundPiece.pieceMoves(this, position);
                        for (ChessMove validMove : validMoves) {
                            if (validMove.getEndPosition().getRow() == testPosition.getRow() && validMove.getEndPosition().getColumn() == testPosition.getColumn()) {
                                perpetrators.add(position);
                            }
                        }
                    }
                }
            }
        }
        if (addedPiece) {
            removePiece(testPosition);
        } else if (replacedPiece != null) {
            addPiece(testPosition, replacedPiece);
        }
        return perpetrators;
    }
}

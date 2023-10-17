package chess;

import chess.Pieces.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public abstract class ChessPieceImpl implements ChessPiece {

    public ChessGame.TeamColor color;
    public ChessPiece.PieceType type;

    /**
     * Constructor for a chess piece
     * @param type the type of chess piece
     * @param color the team color of this piece
     */
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

    /**
     * Retrieves all possible moves for this piece
     * @param board the chess board
     * @param myPosition the position of this piece
     * @return a collection of valid moves
     */
    @Override
    abstract public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);


    /**
     * Checks if a move is safe for the king
     * @param board the chess board
     * @param currentTurn the current turn
     * @param potentialMove the move to check
     * @return true if the move is safe, false if it is not
     */
    public static boolean isSafeMove(ChessBoardImpl board, ChessGame.TeamColor currentTurn, ChessMove potentialMove) {
        var fakeGame = new ChessGameImpl();
        var boardCopy = board.duplicate();
        fakeGame.setBoard(boardCopy);
        fakeGame.setTeamTurn(currentTurn);
        // do a manual move without checks, then see if the king is in check afterward
        var selectedPiece = boardCopy.getPiece(potentialMove.getStartPosition());
        ChessPieceImpl replacedPiece = (ChessPieceImpl) createPiece(selectedPiece.getPieceType(), selectedPiece.getTeamColor());
        // castling
        if (selectedPiece.getPieceType() == PieceType.KING) {
            if (Math.abs(potentialMove.getStartPosition().getColumn() - potentialMove.getEndPosition().getColumn()) > 1) {
                // king is castling, since it moved more than one column over
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

    public enum direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT
    }

    /**
     * Checks a ray in a given direction and adds spaces to valid moves if the space is empty or contains an enemy piece. Stops once it hits the edge of the board or a piece.
     * @param board the chess board
     * @param position the position of this piece
     * @param dir the direction to check
     * @return a collection of valid moves
     */
    public Collection<ChessMove> rayTrace(ChessBoard board, ChessPosition position, direction dir) {
        // split directions into horizontal and vertical components
        int xOffset = 0;
        int yOffset = 0;
        if (dir == direction.LEFT || dir == direction.UP_LEFT || dir == direction.DOWN_LEFT) {
            xOffset = -1;
        } else if (dir == direction.RIGHT || dir == direction.UP_RIGHT || dir == direction.DOWN_RIGHT) {
            xOffset = 1;
        }
        if (dir == direction.UP || dir == direction.UP_LEFT || dir == direction.UP_RIGHT) {
            yOffset = 1;
        } else if (dir == direction.DOWN || dir == direction.DOWN_LEFT || dir == direction.DOWN_RIGHT) {
            yOffset = -1;
        }
        // check for empty spaces in the direction
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        boolean directionFinished = false;
        for (int offset = 1; offset <= 7 && !directionFinished; offset++) {
            int newRow = position.getRow() + yOffset * offset;
            int newCol = position.getColumn() + xOffset * offset;
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                var endPosition = new ChessPositionImpl(newRow, newCol);
                var pieceFound = board.getPiece(endPosition);
                if (pieceFound != null) {
                    if (pieceFound.getTeamColor() != color) {
                        validMoves.add(new ChessMoveImpl(position, endPosition, null));
                    }
                    directionFinished = true;
                } else {
                    validMoves.add(new ChessMoveImpl(position, endPosition, null));
                }
            } else {
                directionFinished = true;
            }
        }
        return validMoves;
    }

    /**
     * Checks all four diagonal directions and adds spaces to valid moves if the space is empty or contains an enemy piece
     * @param board the chess board
     * @param myPosition the position of this piece
     * @param validMoves the collection of valid moves
     */
    public void assessDiagonals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        // look in all four diagonal directions
        validMoves.addAll(rayTrace(board, myPosition, direction.UP_LEFT));
        validMoves.addAll(rayTrace(board, myPosition, direction.UP_RIGHT));
        validMoves.addAll(rayTrace(board, myPosition, direction.DOWN_LEFT));
        validMoves.addAll(rayTrace(board, myPosition, direction.DOWN_RIGHT));
    }

    /**
     * Checks all four cardinal directions and adds spaces to valid moves if the space is empty or contains an enemy piece
     * @param board the chess board
     * @param myPosition the position of this piece
     * @param validMoves the collection of valid moves
     */
    public void assessCardinals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves) {
        // look in all four diagonal directions
        validMoves.addAll(rayTrace(board, myPosition, direction.UP));
        validMoves.addAll(rayTrace(board, myPosition, direction.DOWN));
        validMoves.addAll(rayTrace(board, myPosition, direction.LEFT));
        validMoves.addAll(rayTrace(board, myPosition, direction.RIGHT));
    }

    /**
     * Checks all potential positions and adds them to valid moves if the space is empty or contains an enemy piece
     * @param board the chess board
     * @param myPosition the position of this piece
     * @param validMoves the collection of valid moves
     * @param potentialPositions the collection of potential positions
     */
    public void assessPositions(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> validMoves, HashSet<ChessPosition> potentialPositions) {
        for (var endPosition : potentialPositions) {
            if (endPosition.getRow() >= 1 && endPosition.getRow() <= 8 && endPosition.getColumn() >= 1 && endPosition.getColumn() <= 8) {
                var pieceFound = board.getPiece(endPosition);
                if (pieceFound != null) {
                    if (pieceFound.getTeamColor() != color) {
                        validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                    }
                } else {
                    validMoves.add(new ChessMoveImpl(myPosition, endPosition, null));
                }
            }
        }
    }

    /**
     * Creates a chess piece based on the type and color
     * @param type the type of chess piece
     * @param color the team color of this piece
     * @return a chess piece
     */
    public static ChessPiece createPiece(PieceType type, ChessGame.TeamColor color) {
        switch (type) {
            case KING -> {
                return new King(color);
            }
            case QUEEN -> {
                return new Queen(color);
            }
            case ROOK -> {
                return new Rook(color);
            }
            case BISHOP -> {
                return new Bishop(color);
            }
            case KNIGHT -> {
                return new Knight(color);
            }
            default -> {
                return new Pawn(color);
            }
        }
    }
}

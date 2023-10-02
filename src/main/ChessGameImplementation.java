import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGameImplementation implements ChessGame {

    ChessGame.TeamColor turn = ChessGame.TeamColor.WHITE;
    ChessBoardImplementation boardInstance = new ChessBoardImplementation();

    /**
     * @return Which team's turn it is
     */
    @Override
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    @Override
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at startPosition
     */
    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece selectedPiece = boardInstance.getPiece(startPosition);
        if (selectedPiece == null) {
            return null;
        }
        return selectedPiece.pieceMoves(boardInstance, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece selectedPiece = boardInstance.getPiece(move.getStartPosition());
        if (selectedPiece == null) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = selectedPiece.pieceMoves(boardInstance, move.getStartPosition());
        boolean isValid = false;
        for (ChessMove validMove : validMoves) {
            if (validMove.getEndPosition() == move.getEndPosition()) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new InvalidMoveException();
        }
        boardInstance.movePiece(move.getStartPosition(), move.getEndPosition());
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    @Override
    public boolean isInCheck(TeamColor teamColor) {
        // TODO: isInCheck
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        // TODO: isInCheckmate
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        // TODO: isInStaleMate
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    @Override
    public void setBoard(ChessBoard board) {
        boardInstance = (ChessBoardImplementation) board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    @Override
    public ChessBoard getBoard() {
        return boardInstance;
    }
}

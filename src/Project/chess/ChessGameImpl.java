package chess;

import chess.Pieces.*;

import java.util.Collection;
import java.util.HashSet;

public class ChessGameImpl implements ChessGame {

    ChessGame.TeamColor turn = ChessGame.TeamColor.WHITE;
    ChessBoardImpl boardInstance = new ChessBoardImpl();

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
        var currentTeamColor = getTeamTurn();
        ChessPiece selectedPiece = boardInstance.getPiece(move.getStartPosition());
        if (selectedPiece == null) {
            throw new InvalidMoveException();
        }
        if (selectedPiece.getTeamColor() != currentTeamColor) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = selectedPiece.pieceMoves(boardInstance, move.getStartPosition());
        boolean isValid = false;
        for (ChessMove validMove : validMoves) {
            if (validMove.getEndPosition().getColumn() == move.getEndPosition().getColumn() && validMove.getEndPosition().getRow() == move.getEndPosition().getRow()) {
                if (validMove.getPromotionPiece() == move.getPromotionPiece()) {
                    isValid = true;
                    break;
                }
            }
        }
        if (!isValid) {
            throw new InvalidMoveException();
        }
        // disable possible moves
        // castling
        var selectedPieceType = selectedPiece.getPieceType();
        if (selectedPieceType == ChessPiece.PieceType.KING) {
            if (selectedPiece.getTeamColor() == TeamColor.WHITE) {
                boardInstance.whiteLeftCastlePossible = false;
                boardInstance.whiteRightCastlePossible = false;
            } else {
                boardInstance.blackLeftCastlePossible = false;
                boardInstance.blackRightCastlePossible = false;
            }
        } else if (selectedPieceType == ChessPiece.PieceType.ROOK) {
            if (selectedPiece.getTeamColor() == TeamColor.WHITE) {
                if (move.getStartPosition().getRow() == 1) {
                    if (move.getStartPosition().getColumn() == 1) {
                        boardInstance.whiteLeftCastlePossible = false;
                    } else if (move.getStartPosition().getColumn() == 8) {
                        boardInstance.whiteRightCastlePossible = false;
                    }
                }
            } else {
                if (move.getStartPosition().getRow() == 8) {
                    if (move.getStartPosition().getColumn() == 1) {
                        boardInstance.blackLeftCastlePossible = false;
                    } else if (move.getStartPosition().getColumn() == 8) {
                        boardInstance.blackRightCastlePossible = false;
                    }
                }
            }
        }

        boardInstance.movePiece(move.getStartPosition(), move.getEndPosition());
        if (isInCheck(currentTeamColor)) {
            // move it back
            boardInstance.movePiece(move.getEndPosition(), move.getStartPosition());
            throw new InvalidMoveException();
        }
        if (move.getPromotionPiece() != null) {
            switch (move.getPromotionPiece()) {
                case QUEEN -> boardInstance.addPiece(move.getEndPosition(), new Queen(currentTeamColor));
                case BISHOP -> boardInstance.addPiece(move.getEndPosition(), new Bishop(currentTeamColor));
                case KNIGHT -> boardInstance.addPiece(move.getEndPosition(), new Knight(currentTeamColor));
                case ROOK -> boardInstance.addPiece(move.getEndPosition(), new Rook(currentTeamColor));
                default -> boardInstance.addPiece(move.getEndPosition(), new Pawn(currentTeamColor));

            }

        }
        setTeamTurn(currentTeamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    @Override
    public boolean isInCheck(TeamColor teamColor) {
        var kingPosition = findKingPosition(teamColor);
        if (kingPosition == null) {
            return false;
        }
        var perpetrators = positionInDanger(kingPosition, teamColor);
        return !perpetrators.isEmpty();
    }

    private Collection<ChessPosition> positionInDanger(ChessPosition testPosition, TeamColor teamColor) {
        HashSet<ChessPosition> perpetrators = new HashSet<>();
        // ensure that the position being tested is attackable
        boolean addedPiece = false;
        ChessPiece replacedPiece = null;
        if (boardInstance.getPiece(testPosition) == null) {
            boardInstance.addPiece(testPosition, new Pawn(teamColor));
            addedPiece = true;
        } else if (boardInstance.getPiece(testPosition).getTeamColor() != teamColor) {
            var oppositeColor = boardInstance.getPiece(testPosition).getTeamColor();
            var existingPiece = boardInstance.getPiece(testPosition);
            switch (existingPiece.getPieceType()) {
                case KING -> replacedPiece = new King(oppositeColor);
                case QUEEN -> replacedPiece = new Queen(oppositeColor);
                case ROOK -> replacedPiece = new Rook(oppositeColor);
                case BISHOP -> replacedPiece = new Bishop(oppositeColor);
                case KNIGHT -> replacedPiece = new Knight(oppositeColor);
                default -> replacedPiece = new Pawn(oppositeColor);
            }
            boardInstance.addPiece(testPosition, new Pawn(teamColor));
        }
        // evaluate potential attacks
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPositionImpl(row, col);
                var foundPiece = boardInstance.getPiece(position);
                if (foundPiece != null) {
                    if (foundPiece.getTeamColor() != teamColor) {
                        var validMoves = foundPiece.pieceMoves(boardInstance, position);
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
            boardInstance.removePiece(testPosition);
        } else if (replacedPiece != null) {
            boardInstance.addPiece(testPosition, replacedPiece);
        }
        return perpetrators;
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPositionImpl(row, col);
                var foundPiece = boardInstance.getPiece(position);
                if (foundPiece != null) {
                    if (foundPiece.getPieceType() == ChessPiece.PieceType.KING && foundPiece.getTeamColor() == teamColor) {
                        return position;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        var kingPosition = findKingPosition(teamColor);
        // check all nine possible spots
        for (int row = Math.max(1, kingPosition.getRow() - 1); row <= Math.min(kingPosition.getRow() + 1, 8); row++) {
            for (int col = Math.max(1, kingPosition.getColumn() - 1); col <= Math.min(kingPosition.getColumn() + 1, 8); col++) {
                var newPosition = new ChessPositionImpl(row, col);
                var foundPiece = boardInstance.getPiece(newPosition);
                if (foundPiece == null || foundPiece.getTeamColor() != teamColor) {
                    var perpetrators = positionInDanger(newPosition, teamColor);
                    if (perpetrators.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        // check for assassination
        var perpetrators = positionInDanger(kingPosition, teamColor);
        if (perpetrators.size() == 1) {
            // escape by assassination might be possible
            for (var perpetrator : perpetrators) {
                var assassins = positionInDanger(perpetrator, teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
                if (!assassins.isEmpty()) {
                    if (assassins.size() > 1) {
                        // assassination is possible!
                        return false;
                    } else {
                        for (var assassin : assassins) {
                            var assassinPiece = boardInstance.getPiece(assassin);
                            if (assassinPiece.getPieceType() != ChessPiece.PieceType.KING) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        // check for blocking
        // a. check each position and see if putting a friendly piece there prevents a check.
        HashSet<ChessPosition> potentialBlockingPositions = new HashSet<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPositionImpl(row, col);
                var foundPiece = boardInstance.getPiece(position);
                if (foundPiece == null || foundPiece.getTeamColor() != teamColor) {
                    ChessGame fakeGame = new ChessGameImpl();
                    ChessBoard boardCopy = boardInstance.duplicate();
                    fakeGame.setBoard(boardCopy);
                    // put a piece there
                    fakeGame.getBoard().addPiece(position, new Pawn(teamColor));
                    // test for checkmate
                    if (!fakeGame.isInCheck(teamColor)) {
                        // success! this is a potential save
                        potentialBlockingPositions.add(position);
                    }
                }
            }
        }
        // b. If a potential blocking position exists, check each friendly piece to see if it can move there
        for (var potentialBlockingPosition : potentialBlockingPositions) {
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    var position = new ChessPositionImpl(row, col);
                    var foundPiece = boardInstance.getPiece(position);
                    if (foundPiece != null && foundPiece.getTeamColor() == teamColor) {
                        var validMoves = foundPiece.pieceMoves(boardInstance, position);
                        for (var validMove : validMoves) {
                            if (validMove.getEndPosition().getRow() == potentialBlockingPosition.getRow() && validMove.getEndPosition().getColumn() == potentialBlockingPosition.getColumn()) {
                                if (foundPiece.getPieceType() != ChessPiece.PieceType.KING) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        // check if any of the king's moves are safe
        var kingPosition = findKingPosition(teamColor);
        for (int row = Math.max(kingPosition.getRow() - 1, 1); row <= Math.min(kingPosition.getRow() + 1, 8); row++) {
            for (int col = Math.max(kingPosition.getColumn() - 1, 1); col <= Math.min(kingPosition.getColumn() + 1, 8); col++) {
                if (kingPosition.getRow() != row || kingPosition.getColumn() != col) {
                    var position = new ChessPositionImpl(row, col);
                    if (positionInDanger(position, teamColor).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        // check if any other of this team's pieces can move
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPositionImpl(row, col);
                var foundPiece = boardInstance.getPiece(position);
                if (foundPiece != null && foundPiece.getTeamColor() == teamColor && foundPiece.getPieceType() != ChessPiece.PieceType.KING) {
                    var potentialMoves = foundPiece.pieceMoves(boardInstance, position);
                    if (!potentialMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    @Override
    public void setBoard(ChessBoard board) {
        boardInstance = (ChessBoardImpl) board;
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

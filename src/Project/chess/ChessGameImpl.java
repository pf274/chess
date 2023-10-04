package chess;

import chess.Pieces.*;

import java.util.Collection;
import java.util.HashSet;

import static chess.ChessPieceImpl.isSafeMove;

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
        var validMoves = selectedPiece.pieceMoves(boardInstance, startPosition);
        validMoves.removeIf(validMove -> !isSafeMove(boardInstance, selectedPiece.getTeamColor(), validMove));
        // remove illegal castle
        if (selectedPiece.getPieceType() == ChessPiece.PieceType.KING) {
            for (var validMove : validMoves) {
                if (Math.abs(validMove.getStartPosition().getColumn() - validMove.getEndPosition().getColumn()) > 1) {
                    // attempting to castle
                    int castleRow = validMove.getStartPosition().getRow();
                    TeamColor selectedPieceColor = selectedPiece.getTeamColor();
                    if (validMove.getEndPosition().getColumn() == 3) {
                        var position3safe = boardInstance.positionInDanger(new ChessPositionImpl(castleRow, 3), selectedPieceColor).isEmpty();
                        var position4safe = boardInstance.positionInDanger(new ChessPositionImpl(castleRow, 4), selectedPieceColor).isEmpty();
                        var position5safe = boardInstance.positionInDanger(new ChessPositionImpl(castleRow, 5), selectedPieceColor).isEmpty();
                        if (!(position3safe && position4safe && position5safe)) {
                            validMoves.remove(validMove);
                        }
                    } else { // column is 7
                        var position5safe = boardInstance.positionInDanger(new ChessPositionImpl(castleRow, 5), selectedPieceColor).isEmpty();
                        var position6safe = boardInstance.positionInDanger(new ChessPositionImpl(castleRow, 6), selectedPieceColor).isEmpty();
                        var position7safe = boardInstance.positionInDanger(new ChessPositionImpl(castleRow, 7), selectedPieceColor).isEmpty();
                        if (!(position5safe && position6safe && position7safe)) {
                            validMoves.remove(validMove);
                        }
                    }
                    if (isInCheck(selectedPiece.getTeamColor())) {
                        validMoves.remove(validMove);
                    }
                }
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var beforeMove = boardInstance.duplicate();
        var currentTeamColor = getTeamTurn();
        ChessPiece selectedPiece = boardInstance.getPiece(move.getStartPosition());
        if (selectedPiece == null) {
            throw new InvalidMoveException();
        }
        if (selectedPiece.getTeamColor() != currentTeamColor) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = selectedPiece.pieceMoves(boardInstance, move.getStartPosition());
        validMoves.removeIf(validMove -> !isSafeMove(boardInstance, currentTeamColor, validMove));
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
        // en passant
        var selectedPieceType = selectedPiece.getPieceType();
        if (selectedPieceType == ChessPiece.PieceType.PAWN) {
            if (Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) > 1) {
                // pawn moving two spaces! set the enPassantMove to the space in between
                boardInstance.enPassantMove = new ChessPositionImpl((move.getStartPosition().getRow() + move.getEndPosition().getRow()) / 2, move.getEndPosition().getColumn());
            } else {
                boardInstance.enPassantMove = null;
                if (move.getEndPosition().getColumn() != move.getStartPosition().getColumn() && boardInstance.getPiece(move.getEndPosition()) == null) {
                    // doing en passant move!
                    boardInstance.removePiece(new ChessPositionImpl(move.getStartPosition().getRow(), move.getEndPosition().getColumn()));
                }
            }
        }
        // make the move
        boardInstance.movePiece(move.getStartPosition(), move.getEndPosition());
        // castle
        boolean castled = false;
        if (selectedPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if (Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) > 1) {
                if (move.getEndPosition().getColumn() == 3) {
                    // move left knight
                    boardInstance.removePiece(new ChessPositionImpl(move.getEndPosition().getRow(), 1));
                    boardInstance.addPiece(new ChessPositionImpl(move.getEndPosition().getRow(), 4), new Rook(selectedPiece.getTeamColor()));
                    castled = true;
                } else {
                    // move right knight
                    boardInstance.removePiece(new ChessPositionImpl(move.getEndPosition().getRow(), 8));
                    boardInstance.addPiece(new ChessPositionImpl(move.getEndPosition().getRow(), 6), new Rook(selectedPiece.getTeamColor()));
                    castled = true;
                }
            }
        }
        if (isInCheck(currentTeamColor)) {
            // move it back
            setBoard(beforeMove);
            throw new InvalidMoveException();
        }
        if (castled) {
            if (currentTeamColor == TeamColor.WHITE) {
                boardInstance.whiteRightCastlePossible = false;
                boardInstance.whiteLeftCastlePossible = false;
            }
        }
        // promotion
        if (move.getPromotionPiece() != null) {
            switch (move.getPromotionPiece()) {
                case QUEEN -> boardInstance.addPiece(move.getEndPosition(), new Queen(currentTeamColor));
                case BISHOP -> boardInstance.addPiece(move.getEndPosition(), new Bishop(currentTeamColor));
                case KNIGHT -> boardInstance.addPiece(move.getEndPosition(), new Knight(currentTeamColor));
                case ROOK -> boardInstance.addPiece(move.getEndPosition(), new Rook(currentTeamColor));
                default -> boardInstance.addPiece(move.getEndPosition(), new Pawn(currentTeamColor));

            }
        }

        // disable possible moves
        // a. disable castling
        if (selectedPieceType == ChessPiece.PieceType.KING) {
            if (currentTeamColor == TeamColor.WHITE) {
                boardInstance.whiteLeftCastlePossible = false;
                boardInstance.whiteRightCastlePossible = false;
            } else {
                boardInstance.blackLeftCastlePossible = false;
                boardInstance.blackRightCastlePossible = false;
            }
        } else if (selectedPieceType == ChessPiece.PieceType.ROOK) {
            if (currentTeamColor == TeamColor.WHITE) {
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
        // switch turns
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
        var perpetrators = boardInstance.positionInDanger(kingPosition, teamColor);
        return !perpetrators.isEmpty();
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
                    var perpetrators = boardInstance.positionInDanger(newPosition, teamColor);
                    if (perpetrators.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        // check for assassination
        var perpetrators = boardInstance.positionInDanger(kingPosition, teamColor);
        if (perpetrators.size() == 1) {
            // escape by assassination might be possible
            for (var perpetrator : perpetrators) {
                var assassins = boardInstance.positionInDanger(perpetrator, teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
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
                    if (boardInstance.positionInDanger(position, teamColor).isEmpty()) {
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

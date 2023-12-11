package chess;

import chess.Pieces.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import static chess.ChessPieceImpl.isSafeMove;

public class ChessGameImpl implements ChessGame {

    public boolean gameOver = false;
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
        if (selectedPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            ChessPositionImpl attackLeft = new ChessPositionImpl(startPosition.getRow() + (selectedPiece.getTeamColor() == TeamColor.WHITE ? 1 : -1), startPosition.getColumn() - 1);
            ChessPositionImpl attackRight = new ChessPositionImpl(startPosition.getRow() + (selectedPiece.getTeamColor() == TeamColor.WHITE ? 1 : -1), startPosition.getColumn() + 1);
            if (attackRight.equals(boardInstance.enPassantMove)) {
                validMoves.add(new ChessMoveImpl(startPosition, attackRight, null));
            } else if (attackLeft.equals(boardInstance.enPassantMove)) {
                validMoves.add(new ChessMoveImpl(startPosition, attackLeft, null));
            }
        }
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
            if (ChessPositionImpl.positionsEqual(validMove.getEndPosition(), move.getEndPosition())) {
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
                    // move left rook
                    boardInstance.movePiece(new ChessPositionImpl(move.getEndPosition().getRow(), 1), new ChessPositionImpl(move.getEndPosition().getRow(), 4));
                } else {
                    // move right rook
                    boardInstance.movePiece(new ChessPositionImpl(move.getEndPosition().getRow(), 8), new ChessPositionImpl(move.getEndPosition().getRow(), 6));
                }
                castled = true;
            }
        }
        if (isInCheck(currentTeamColor)) {
            // move it back
            setBoard(beforeMove);
            throw new InvalidMoveException();
        }
        // disable future castling
        if (castled) {
            if (currentTeamColor == TeamColor.WHITE) {
                boardInstance.whiteRightCastlePossible = false;
                boardInstance.whiteLeftCastlePossible = false;
            } else {
                boardInstance.blackLeftCastlePossible = false;
                boardInstance.blackRightCastlePossible = false;
            }
        }
        // promotion
        if (move.getPromotionPiece() != null) {
            ChessPieceImpl newPiece = (ChessPieceImpl) ChessPieceImpl.createPiece(move.getPromotionPiece(), currentTeamColor);
            boardInstance.addPiece(move.getEndPosition(), newPiece);
        }

        // disable castling if a rook or king moves
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
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        var kingPosition = findKingPosition(teamColor);
        var opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        assert kingPosition != null;
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
                var assassins = boardInstance.positionInDanger(perpetrator, opponentColor);
                if (!assassins.isEmpty()) {
                    if (assassins.size() > 1) {
                        // assassination is possible!
                        return false;
                    } else {
                        // the king cannot assassinate anyone since we already checked that he cannot move to safety.
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
                            if (ChessPositionImpl.positionsEqual(validMove.getEndPosition(), potentialBlockingPosition)) {
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
        assert kingPosition != null;
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
     * @param board the new board to use
     */
    @Override
    public void setBoard(ChessBoard board) {
        boardInstance = (ChessBoardImpl) board;
    }

    /**
     * Gets the current chessboard
     * @return the chessboard
     */
    @Override
    public ChessBoard getBoard() {
        return boardInstance;
    }


    public String getGameAsString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col++) {
                var position = new ChessPositionImpl(row, col);
                var foundPiece = boardInstance.getPiece(position);
                if (foundPiece == null) {
                    sb.append("_");
                } else {
                    var pieceType = foundPiece.getPieceType();
                    var pieceColor = foundPiece.getTeamColor();
                    char character = switch (pieceType) {
                        case ROOK -> 'R';
                        case KNIGHT -> 'N';
                        case BISHOP -> 'B';
                        case QUEEN -> 'Q';
                        case KING -> 'K';
                        default -> 'P';
                    };
                    if (pieceColor == ChessGame.TeamColor.BLACK) {
                        character = Character.toLowerCase(character);
                    }
                    sb.append(character);
                }
            }
//            if (row != 1) {
//                sb.append("/");
//            }
        }
        // add turn
        sb.append(" ");
        sb.append(turn == ChessGame.TeamColor.WHITE ? "w" : "b");
        // add castling
        sb.append(" ");
        if (boardInstance.whiteLeftCastlePossible) {
            sb.append("K");
        }
        if (boardInstance.whiteRightCastlePossible) {
            sb.append("Q");
        }
        if (boardInstance.blackLeftCastlePossible) {
            sb.append("k");
        }
        if (boardInstance.blackRightCastlePossible) {
            sb.append("q");
        }
        // add en passant
        sb.append(" ");
        if (boardInstance.enPassantMove != null) {
            char column = (char) ('a' + boardInstance.enPassantMove.getColumn() - 1);
            sb.append(column);
            sb.append(boardInstance.enPassantMove.getRow());
        } else {
            sb.append("-");
        }
        return sb.toString();
    }

    public void loadGameFromString(String gameState) {
        String boardRegex = "([rnbqkpRNBQKP_]+)";
        String turnRegex = " ([wb])";
        String castlingRegex = " ([KQkq]+)";
        String enPassantRegex = " ([a-h][1-8]|-)";
        String fullRegex = boardRegex + turnRegex + castlingRegex + enPassantRegex;
        if (!gameState.matches(fullRegex)) {
            return;
        }
        // use regex to get the parts as strings
        Pattern pattern = Pattern.compile(fullRegex);
        var matcher = pattern.matcher(gameState);
        if (matcher.matches()) {
            var boardString = matcher.group(1);
            var turnString = matcher.group(2);
            var castlingString = matcher.group(3);
            var enPassantString = matcher.group(4);
//            System.out.println("Board: " + boardString);
//            System.out.println("Current Turn: " + turnString);
//            System.out.println("Castling Rights: " + castlingString);
//            System.out.println("En Passant Target: " + enPassantString);
            // set the board
            var loadedBoard = new ChessBoardImpl();
            var boardIndex = 0;
            for (int row = 8; row >= 1; row--) {
                for (int col = 1; col <= 8; col++) {
                    var position = new ChessPositionImpl(row, col);
                    var character = boardString.charAt(boardIndex);
                    if (character != '_') {
                        var pieceType = switch (Character.toLowerCase(character)) {
                            case 'r' -> ChessPiece.PieceType.ROOK;
                            case 'n' -> ChessPiece.PieceType.KNIGHT;
                            case 'b' -> ChessPiece.PieceType.BISHOP;
                            case 'q' -> ChessPiece.PieceType.QUEEN;
                            case 'k' -> ChessPiece.PieceType.KING;
                            default -> ChessPiece.PieceType.PAWN;
                        };
                        var pieceColor = Character.isLowerCase(character) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                        var newPiece = ChessPieceImpl.createPiece(pieceType, pieceColor);
                        loadedBoard.addPiece(position, newPiece);
                    }
                    boardIndex++;
                }
            }
            // set the turn
            turn = turnString.equals("w") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            // set the castling rights
            loadedBoard.whiteLeftCastlePossible = castlingString.contains("K");
            loadedBoard.whiteRightCastlePossible = castlingString.contains("Q");
            loadedBoard.blackLeftCastlePossible = castlingString.contains("k");
            loadedBoard.blackRightCastlePossible = castlingString.contains("q");
            // set the en passant target
            if (enPassantString.equals("-")) {
                loadedBoard.enPassantMove = null;
            } else {
                var column = enPassantString.charAt(0);
                var row = enPassantString.charAt(1);
                var columnNumber = column - 'a' + 1;
                var rowNumber = row - '0';
                loadedBoard.enPassantMove = new ChessPositionImpl(rowNumber, columnNumber);
            }
            setBoard(loadedBoard);
//            System.out.println("Board loaded");
        }
    }
}

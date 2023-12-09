package ui;

import chess.*;
import ui.menus.MenuBase;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class BoardDisplay {

    private static final String WIDE_SPACE = "\u2003";

    private static final String defaultBackground = "\u001b[0m";
    private static final String defaultTextColor = "\u001b[39m";
    private static final String borderBackgroundColor = SET_BG_COLOR_DARK_GREY;
    private static final String borderTextColor = SET_TEXT_COLOR_WHITE;
    private static final String firstBackgroundColor = SET_BG_COLOR_LIGHT_GREY;
    private static final String secondBackgroundColor = SET_BG_COLOR_BLACK;

    private static final String whitePieceColor = SET_TEXT_COLOR_RED;

    private static final String blackPieceColor = SET_TEXT_COLOR_BLUE;

    private static final String highlightedFirstBackgroundColor = SET_BG_COLOR_GREEN;

    private static final String highlightedSecondBackgroundColor = SET_BG_COLOR_DARK_GREEN;

    private static Collection<ChessMove> validMoves = null;

    public static void displayBoardHighlighted(Collection<ChessMove> validMoves) {
        BoardDisplay.validMoves = validMoves;
        displayBoard();
        BoardDisplay.validMoves = null;
    }
    public static void displayBoard() {
        System.out.print("\n");
        boolean reversed = MenuBase.orientation.equals("black");
        // top border
        printHorizontalBorder(reversed);
        // board rows
        for (int row = reversed ? 1 : 8; reversed ? row <= 8 : row >= 1; row += reversed ? 1 : -1) {
            // left border
            System.out.print(borderBackgroundColor + borderTextColor + WIDE_SPACE + row + " " + defaultBackground + defaultTextColor);
            // board columns
            for (int column = reversed ? 8 : 1; reversed ? column >= 1 : column <= 8; column += reversed ? -1 : 1) {
                ChessPieceImpl piece = (ChessPieceImpl) MenuBase.chessGame.getBoard().getPiece(new ChessPositionImpl(row, column));
                String pieceString = "";
                if (piece == null) {
                    pieceString = EMPTY;
                } else {
                    ChessPiece.PieceType pieceType = piece.getPieceType();
                    pieceString = switch (pieceType) {
                        case PAWN -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield whitePieceColor + WHITE_PAWN + defaultTextColor;
                            } else {
                                yield blackPieceColor + WHITE_PAWN + defaultTextColor;
                            }
                        }
                        case ROOK -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield whitePieceColor + WHITE_ROOK + defaultTextColor;
                            } else {
                                yield blackPieceColor + WHITE_ROOK + defaultTextColor;
                            }
                        }
                        case KNIGHT -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield whitePieceColor + WHITE_KNIGHT + defaultTextColor;
                            } else {
                                yield blackPieceColor + WHITE_KNIGHT + defaultTextColor;
                            }
                        }
                        case BISHOP -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield whitePieceColor + WHITE_BISHOP + defaultTextColor;
                            } else {
                                yield blackPieceColor + WHITE_BISHOP + defaultTextColor;
                            }
                        }
                        case QUEEN -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield whitePieceColor + WHITE_QUEEN + defaultTextColor;
                            } else {
                                yield blackPieceColor + WHITE_QUEEN + defaultTextColor;
                            }
                        }
                        case KING -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield whitePieceColor + WHITE_KING + defaultTextColor;
                            } else {
                                yield blackPieceColor + WHITE_KING + defaultTextColor;
                            }
                        }
                    };
                }
                String backgroundColor = getBackgroundColor(row, column, validMoves);
                if ((row * 8 + column + (row % 2)) % 2 == 0) {
                    pieceString = backgroundColor + pieceString + defaultBackground;
                } else {
                    pieceString = backgroundColor + pieceString + defaultBackground;
                }
                System.out.print(pieceString);
            }
            System.out.print(borderBackgroundColor + borderTextColor + WIDE_SPACE + row + " " + defaultBackground + defaultTextColor);
            System.out.print("\n");
        }
        // bottom border
        printHorizontalBorder(reversed);
    }

    private static String getBackgroundColor(int row, int column, Collection<ChessMove> validMoves) {
        boolean isHighlighted = false;
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                if (move.getEndPosition().getRow() == row && move.getEndPosition().getColumn() == column) {
                    isHighlighted = true;
                    break;
                }
            }
        }
        if ((row * 8 + column + (row % 2)) % 2 == 0) {
            return isHighlighted ? highlightedFirstBackgroundColor : firstBackgroundColor;
        } else {
            return isHighlighted ? highlightedSecondBackgroundColor : secondBackgroundColor;
        }
    }

    private static void printHorizontalBorder(boolean reversed) {
        System.out.print(borderBackgroundColor + borderTextColor + EMPTY);
        for (char column = reversed ? 'h' : 'a'; reversed ? (column >= 'a') : column <= 'h'; column += reversed ? -1 : 1) {
            System.out.print(WIDE_SPACE);
            System.out.print(column + " ");
        }
        System.out.print(EMPTY);
        System.out.print(defaultBackground + defaultTextColor + "\n");
    }
}

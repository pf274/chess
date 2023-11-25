package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class BoardDisplay {

    private static final String WIDE_SPACE = "\u2003";

    private static final String defaultBackground = "\u001b[0m";
    private static final String defaultTextColor = "\u001b[39m";
    private static final String borderBackgroundColor = SET_BG_COLOR_DARK_GREY;
    private static final String borderTextColor = SET_TEXT_COLOR_WHITE;
    private static final String firstBackgroundColor = SET_BG_COLOR_DARK_GREEN;
    private static final String secondBackgroundColor = SET_BG_COLOR_BLACK;
    public static void displayBoard(ChessBoardImpl board, boolean reversed) {
        // top border
        printHorizontalBorder();
        // board rows
        for (int row = reversed ? 8 : 1; reversed ? row >= 1 : row <= 8; row += reversed ? -1 : 1) {
            // left border
            System.out.print(borderBackgroundColor + borderTextColor + WIDE_SPACE + row + " " + defaultBackground + defaultTextColor);
            // board columns
            for (int column = reversed ? 8 : 1; reversed ? column >= 1 : column <= 8; column += reversed ? -1 : 1) {
                ChessPieceImpl piece = (ChessPieceImpl) board.getPiece(new ChessPositionImpl(row, column));
                String pieceString = "";
                if (piece == null) {
                    pieceString = EMPTY;
                } else {
                    ChessPiece.PieceType pieceType = piece.getPieceType();
                    pieceString = switch (pieceType) {
                        case PAWN -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield WHITE_PAWN;
                            } else {
                                yield BLACK_PAWN;
                            }
                        }
                        case ROOK -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield WHITE_ROOK;
                            } else {
                                yield BLACK_ROOK;
                            }
                        }
                        case KNIGHT -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield WHITE_KNIGHT;
                            } else {
                                yield BLACK_KNIGHT;
                            }
                        }
                        case BISHOP -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield WHITE_BISHOP;
                            } else {
                                yield BLACK_BISHOP;
                            }
                        }
                        case QUEEN -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield WHITE_QUEEN;
                            } else {
                                yield BLACK_QUEEN;
                            }
                        }
                        case KING -> {
                            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                                yield WHITE_KING;
                            } else {
                                yield BLACK_KING;
                            }
                        }
                    };
                }
                if ((row * 8 + column + (row % 2)) % 2 == 0) {
                    pieceString = firstBackgroundColor + pieceString + defaultBackground;
                } else {
                    pieceString = secondBackgroundColor + pieceString + defaultBackground;
                }
                System.out.print(pieceString);
            }
            System.out.print(borderBackgroundColor + borderTextColor + WIDE_SPACE + row + " " + defaultBackground + defaultTextColor);
            System.out.print("\n");
        }
        // bottom border
        printHorizontalBorder();
    }

    private static void printHorizontalBorder() {
        System.out.print(borderBackgroundColor + borderTextColor + EMPTY);
        for (char column = 'a'; column <= 'h'; column++) {
            System.out.print(WIDE_SPACE);
            System.out.print(column + " ");
        }
        System.out.print(EMPTY);
        System.out.print(defaultBackground + defaultTextColor + "\n");
    }
}

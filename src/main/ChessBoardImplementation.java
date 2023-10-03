import chess.*;


public class ChessBoardImplementation implements ChessBoard {

    private ChessPieceImplementation[][] board = new ChessPieceImplementation[8][8];

    public ChessBoardImplementation() {
        resetBoard();
    }

    public ChessPieceImplementation[][] getBoard() {
        return board;
    }

    public void setBoard(ChessPiece[][] board) {
        this.board = (ChessPieceImplementation[][]) board;
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
        board[row][column] = (ChessPieceImplementation) piece;
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
        board[endingRow][endingCol] = (ChessPieceImplementation) selectedPiece;
        board[startingRow][startingCol] = null;
    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    @Override
    public void resetBoard() {
        board = new ChessPieceImplementation[8][8];
        // create chess pieces
        ChessPieceImplementation whiteKnight = new ChessPieceImplementation(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE);
        ChessPieceImplementation blackKnight = new ChessPieceImplementation(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK);
        ChessPieceImplementation whitePawn = new ChessPieceImplementation(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE);
        ChessPieceImplementation blackPawn = new ChessPieceImplementation(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK);
        ChessPieceImplementation whiteRook = new ChessPieceImplementation(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE);
        ChessPieceImplementation blackRook = new ChessPieceImplementation(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK);
        ChessPieceImplementation whiteBishop = new ChessPieceImplementation(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE);
        ChessPieceImplementation blackBishop = new ChessPieceImplementation(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK);
        ChessPieceImplementation whiteKing = new ChessPieceImplementation(ChessPiece.PieceType.KING, ChessGame.TeamColor.WHITE);
        ChessPieceImplementation blackKing = new ChessPieceImplementation(ChessPiece.PieceType.KING, ChessGame.TeamColor.BLACK);
        ChessPieceImplementation whiteQueen = new ChessPieceImplementation(ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.WHITE);
        ChessPieceImplementation blackQueen = new ChessPieceImplementation(ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.BLACK);
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
    }

    public void print() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPieceImplementation piece = board[i][j];
                System.out.print("|" + board[i][j]);
            }
            System.out.print("|\n");
        }
    }

    public boolean testMove(ChessMove potentialMove) {
        var selectedPiece = getPiece(potentialMove.getStartPosition());
        if (selectedPiece == null) {
            return false;
        }
        var targetPiece = getPiece(potentialMove.getEndPosition());
        if (targetPiece == null) {
            return true;
        }
        return (targetPiece.getTeamColor() != selectedPiece.getTeamColor());
    }
}

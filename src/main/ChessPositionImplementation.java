import chess.ChessPosition;

public class ChessPositionImplementation implements ChessPosition {

    int row;
    int column;
    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    @Override
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    @Override
    public int getColumn() {
        return column;
    }

    public ChessPositionImplementation(int row, int column) {
        this.row = row;
        this.column = column;
    }
}

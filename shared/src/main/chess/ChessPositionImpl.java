package chess;

import java.util.Objects;

public class ChessPositionImpl implements ChessPosition {

    int row;

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChessPosition) {
            ChessPositionImpl convertedO = (ChessPositionImpl) o;
            return row == convertedO.row && column == convertedO.column;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

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

    /**
     * Constructor for a chess position
     * @param row the row
     * @param column the column
     */
    public ChessPositionImpl(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static boolean positionsEqual(ChessPosition position1, ChessPosition position2) {
        return position1.getRow() == position2.getRow() && position1.getColumn() == position2.getColumn();
    }

}

package baldi;

public class Square implements Comparable<Square> {
	private final int dimension;
    private final int row;
    private final int col;

    public Square(int dimension, int row, int col) {
        this.dimension = dimension;
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean sameRowAs(Square q) {
        return this.row == q.row;
    }

    public boolean sameColAs(Square q) {
        return this.col == q.col;
    }

    public boolean sameDiagonalAs(Square q) {
        return Math.abs(row - q.row) == Math.abs(col - q.col);

    }

    @Override
    public int compareTo(Square o) {
        return (this.col - o.col) * dimension + (this.row - o.row);
    }

    @Override
    public String toString() {
        return "" + ((char) (col + 65)) + (row + 1);
    }
}

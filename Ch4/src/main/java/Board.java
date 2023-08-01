import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author by RacerZ
 * @date 2023/7/26.
 */
public class Board {
    private int dim;
    private int[][] tiles;
    private int blankX;
    private int blankY;

    /**
    * create a board from an n-by-n array of tiles,
    * where tiles[row][col] = tile at (row, col)
    */
    public Board(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException();
        }
        this.dim = tiles.length;
        this.tiles = copyOf(tiles);
        this.blankX = -1;
        this.blankY = -1;

        // check 0
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                if (tiles[row][col] == 0) {
                    this.blankX = row;
                    this.blankY = col;
                    break;
                }
            }
        }
    }

    private int[][] copyOf (int [][] matrix) {
        int[][] clone = new int[matrix.length][];
        for (int row = 0; row < matrix.length; row++) {
            // API for copy; deep copy ?
            clone[row] = Arrays.copyOf(matrix[row], matrix[row].length);
        }

        return clone;
    }

    // string representation of this board
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(dim).append("\n");
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                builder.append(String.format("%2d ", tiles[row][col]));
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    // board dimension n
    public int dimension() {
        return dim;
    }

    // number of tiles out of place
    public int hamming() {
        int sum = 0;
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                // skip the blank square
                if (row == blankX && col == blankY) {
                    continue;
                }

                if (manhattan(row, col) != 0) {
                    sum++;
                }
            }
        }

        return sum;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int sum = 0;
        for (int row = 0; row < dim; row++ ) {
            for (int col = 0; col < dim; col++) {
                // skip the blank square
                if (row == blankX && col == blankY) {
                    continue;
                }

                sum += manhattan(row, col);
            }
        }
        return sum;
    }

    private int manhattan(int row, int col) {
        int realNum = tiles[row][col] - 1;
        int dx = Math.abs(realNum / dim - row);
        int dy = Math.abs(realNum % dim - col);

        return dx + dy;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        return (this.dim == that.dim) && (Arrays.deepEquals(tiles, that.tiles));
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new LinkedList<>();
        int[] dx = {-1, 0, 1, 0}, dy = {0, -1, 0, 1};
        int curX ,curY;
        // bfs
        for (int i = 0; i < 4; i++) {
            curX = blankX + dx[i];
            curY = blankY + dy[i];
            if (curX >= 0 && curX < dim && curY >= 0 && curY < dim) {
                swap(tiles, blankX, blankY, curX, curY);
                neighbors.add(new Board(tiles));
                swap(tiles, blankX, blankY, curX, curY);
            }
        }
        return neighbors;
    }

    private void swap(int [][] v, int rowA, int colA, int rowB, int colB) {
        int swap = v[rowA][colA];
        v[rowA][colA] = v[rowB][colB];
        v[rowB][colB] = swap;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        // choose a row that diff with blank
        int[][] cloned = copyOf(tiles);
        if (blankX != 0) {
            swap(cloned, 0, 0, 0, 1);
        } else {
            swap(cloned, 1, 0, 1, 1);
        }
        return new Board(cloned);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            Board board = new Board(tiles);
            System.out.println(board);
            System.out.println(board.twin());
        }
    }

}

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

/**
 * @author by RacerZ
 * @date 2023/7/26.
 */
public class Solver {
    private MinPQ<SearchNode> pq;
    private int step;
    private boolean solvable;
    // store the final Node
    private SearchNode finalNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        this.step = 0;
        SearchNode ini = new SearchNode(0, initial, null);
        // TODO: need a comparator
        pq = new MinPQ<>();
        pq.insert(ini);

        while (true) {
            SearchNode searchNode = pq.delMin();
            Board minBoard = searchNode.getBoard();
            int curStep = searchNode.getCurStep();
            if (minBoard.isGoal()) {
                finalNode = searchNode;
                solvable = true;
                break;
            }

            if (minBoard.manhattan() == 2 && minBoard.twin().isGoal()) {
                solvable = false;
                break;
            }
            // watch out for bugs: never put same board again!

            SearchNode preBoard = searchNode.getPreBoard();
            for (Board neighbor : minBoard.neighbors()) {
                if (preBoard != null && preBoard.board.equals(neighbor)) {
                    continue;
                }
                pq.insert(new SearchNode(curStep + 1, neighbor, searchNode));
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return isSolvable() ? finalNode.getCurStep() : -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!solvable) {
            return null;
        }
        LinkedList<Board> solution = new LinkedList<>();
        // search back from the finalNode
        SearchNode cur = finalNode;
        while (cur != null) {
            Board board = cur.getBoard();
            // maintain reverse order
            solution.addFirst(board);
            cur = cur.getPreBoard();
        }

        return solution;
    }

    private class SearchNode implements Comparable<SearchNode> {
        // previous search node
        private final SearchNode preBoard;
        private final int curStep;
        private final Board board;
        // optimization1: TODO cache priority
        private final int manhattan;

        public SearchNode(int curStep, Board board, SearchNode pre) {
            this.board = board;
            this.curStep = curStep;
            this.preBoard = pre;
            this.manhattan = board.manhattan();
        }

        @Override
        public int compareTo(SearchNode o) {
            return this.priority() - o.priority();
        }

        public int priority() {
            // hamming
            return manhattan + curStep;
        }

        public SearchNode getPreBoard() {
            return preBoard;
        }

        public int getCurStep() {
            return curStep;
        }

        public Board getBoard() {
            return board;
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}

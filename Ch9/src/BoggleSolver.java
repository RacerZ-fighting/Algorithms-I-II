import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

/**
 * @author by RacerZ
 * @date 2023/9/9.
 */
public class BoggleSolver {
    private final TrieTree dict;
    private HashSet<String> answers;
    private final int[] directX = {-1, 0, 1, 0, 1, -1, 1, -1};
    private final int[] directY = {0, 1, 0, -1, 1, -1, -1, 1};
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException();
        }

        dict = new TrieTree();
        for (String word : dictionary) {
            dict.insert(word);
        }
    }


    private void dfs(BoggleBoard board, boolean[][] isVisited, int x, int y, TrieTree.Node cache) {
        char c = board.getLetter(x, y);
        isVisited[x][y] = true;
        TrieTree.Node node = dict.prefixNode(c, cache);
        // Make sure that you have implemented the critical backtracking optimization
        if (node != null) {
            // 检查当前节点是否为字典树上的单词
            if (node.isEnd() && node.getWord().length() > 2) {
                answers.add(node.getWord());
            }

            //  no need to search iff there's no more child
            if (node.hasChild()) {
                for (int i = 0; i < directX.length; i++) {
                    int nx = x + directX[i], ny = y + directY[i];
                    if (nx >= 0 && nx < board.rows() && ny >= 0 && ny < board.cols() && !isVisited[nx][ny]) {
                        dfs(board, isVisited, nx, ny, node);    // TODO isVisited 设置在这里头可能导致第一个入口点未设置
                    }
                }
            }
        }
        isVisited[x][y] = false;    // TODO 回退
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException();
        }

        answers = new HashSet<>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                boolean[][] isVisited = new boolean[board.rows()][board.cols()];
                dfs(board, isVisited, i, j, null);
            }
        }

        return answers;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dict.search(word)) {
            int len = word.length();
            if (len < 3) {
                return 0;
            } else if (len <= 4) {
                return 1;
            } else if (len == 5) {
                return 2;
            } else if (len == 6) {
                return 3;
            } else if (len == 7) {
                return 5;
            } else {
                return 11;
            }
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}

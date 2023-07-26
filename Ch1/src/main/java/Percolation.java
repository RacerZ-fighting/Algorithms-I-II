import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 *
 * @version 0.1.0
 * @author RacerZ
 * @since 0.1.0
 * @create 2023/7/13 11:19
 **/
public class Percolation {
    private WeightedQuickUnionUF arr;
    private WeightedQuickUnionUF uff;
    private int gridWidth; // 网格宽度
    private int openSites; // 开通站点
    private boolean[][] isOpened; // 是否开通
    private int[] dx = {-1, 0, 1, 0};
    private int[] dy = {0, -1, 0, 1};

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        // corner cases
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        arr = new WeightedQuickUnionUF(n * n + 2); // 测试系统是否 percolated
        uff = new WeightedQuickUnionUF(n * n + 1); // 测试站点是否 full
        gridWidth = n;
        openSites = 0;
        isOpened = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                isOpened[i][j] = false;
            }
        }
    }

    private boolean isInGrid(int row, int col) {
        return row >= 1 && row <= gridWidth && col >= 1 && col <= gridWidth;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!isInGrid(row, col)) {
            throw new IllegalArgumentException();
        }

        int id = calcIndex(row, col);
        if (!isOpened[row - 1][col - 1]) {
            isOpened[row - 1][col - 1] = true;
            // 特殊处理第一行和最后一行元素
            if (row == 1) {
                arr.union(0, id); // 合并到虚拟 0 结点
                uff.union(0, id);
            }
            if (row == gridWidth) {
                arr.union(gridWidth * gridWidth + 1, id); // 合并到虚拟 n^2 + 1 结点
            }
            // 合并邻居结点
            connectNeighbours(row, col);
            openSites++;
        }
    }

    private void connectNeighbours(int row, int col) {
        int currentId = calcIndex(row, col);
        int nRow, nCol;
        int newId;

        for (int i = 0; i < 4; i++) {
            nRow = row + dx[i];
            nCol = col + dy[i];
            if (isInGrid(nRow, nCol) && isOpen(nRow, nCol)) {
                newId = calcIndex(nRow, nCol);
                uff.union(currentId, newId);
                arr.union(currentId, newId);
            }
        }
    }

    private int calcIndex(int row, int col) {
        // 计算标准索引（从 1 开始）
        return (row - 1) * gridWidth + col;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!isInGrid(row, col)) {
            throw new IllegalArgumentException();
        }
        return isOpened[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isInGrid(row, col)) {
            throw new IllegalArgumentException();
        }
        int id = calcIndex(row, col);
        return uff.find(0) == uff.find(id);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return arr.find(0) == arr.find(gridWidth * gridWidth + 1);
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation obj = new Percolation(3);
        obj.open(1, 3);
        obj.open(2, 3);
        obj.open(3, 3);
        obj.open(3, 1);
        System.out.println(obj.numberOfOpenSites());
        System.out.println(obj.percolates());
        System.out.println(obj.isFull(3, 1));
    }
}

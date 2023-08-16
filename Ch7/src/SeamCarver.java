import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.Arrays;

/**
 * @author by RacerZ
 * @date 2023/8/15.
 */
public class SeamCarver {
    private double[][] energy;  // 按行优先排列，即第一维度是列
    private int[][] RGB;
    private boolean VERTICAL = true;   // 表示当前是否放置垂直
    private static final int BORDER_ENERGY = 1000;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }

        this.energy = new double[picture.height()][picture.width()];    // 按行排序
        this.RGB = new int[picture.height()][picture.width()];
        // init energy and RGB array

        for (int i = 0; i < picture.height(); i++) {
            for (int j = 0; j < picture.width(); j++) {
                RGB[i][j] = picture.getRGB(j, i);
            }
        }

        for (int i = 0; i < energy.length; i++) {
            for (int j = 0; j < energy[0].length; j++) {
                energy[i][j] = computeEnergy(i, j);
            }
        }
    }

    // current picture
    public Picture picture() {
        // transpose first iff need
        if (!VERTICAL) {
            transpose();
        }
        // make defensive copies
        Picture copy = new Picture(RGB[0].length, RGB.length);
        for (int i = 0; i < RGB.length; i++) {
            for (int j = 0; j < RGB[0].length; j++) {
                copy.setRGB(j, i, RGB[i][j]);
            }
        }

        return copy;
    }

    // width of current picture
    public int width() {
        return this.VERTICAL ? energy[0].length : energy.length;
    }

    // height of current picture
    public int height() {
        return this.VERTICAL ? energy.length : energy[0].length;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) { // col x row y
        // speical case when horizon
        if (!VERTICAL) {
            int temp = x;
            x = y;
            y = temp;
        }

        if (x < 0 || x >= energy[0].length || y < 0 || y >= energy.length) {
            throw new IllegalArgumentException();
        }

        return energy[y][x];
    }

    private double computeEnergy(int x, int y) {
        // 这里如果仍调用会出错 Todo
        if (x == 0 || y == 0 || x == RGB.length - 1 || y == RGB[0].length - 1) {
            return BORDER_ENERGY;
        }

        int deltaRx = Math.abs(((RGB[x + 1][y] >> 16) & 0xFF) - ((RGB[x - 1][y] >> 16) & 0xFF));
        int deltaGx = Math.abs(((RGB[x + 1][y] >> 8) & 0xFF) - ((RGB[x - 1][y] >> 8) & 0xFF));
        int deltaBx = Math.abs(((RGB[x + 1][y]) & 0xFF) - ((RGB[x - 1][y]) & 0xFF));

        int deltaRy = Math.abs(((RGB[x][y + 1] >> 16) & 0xFF) - ((RGB[x][y - 1] >> 16) & 0xFF));
        int deltaGy = Math.abs(((RGB[x][y + 1] >> 8) & 0xFF) - ((RGB[x][y - 1] >> 8) & 0xFF));
        int deltaBy = Math.abs(((RGB[x][y + 1] ) & 0xFF) - ((RGB[x][y - 1]) & 0xFF));

        double gradientX = Math.pow(deltaBx, 2) + Math.pow(deltaRx, 2) + Math.pow(deltaGx, 2);
        double gradientY = Math.pow(deltaRy, 2) + Math.pow(deltaBy, 2) + Math.pow(deltaGy, 2);

        return Math.sqrt(gradientX + gradientY);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (VERTICAL) transpose();
        // System.out.println("--->" + width() + "x" + height());
        return findSeam();
    }

    private void transpose() {
        double[][] transEnergy = new double[energy[0].length][energy.length];
        int[][] transRGB = new int[RGB[0].length][RGB.length];

        for (int i = 0; i < energy[0].length; i++) {
            for (int j = 0; j < energy.length; j++) {
                transEnergy[i][j] = energy[j][i];
                transRGB[i][j] = RGB[j][i];
            }
        }

        RGB = transRGB;
        energy = transEnergy;
        VERTICAL = !VERTICAL;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // 优化：需要的时候再 transpose
        if (!VERTICAL) transpose();
        return findSeam();
    }

    private int[] findSeam() {

        double[][] distTo = new double[energy.length][energy[0].length];
        int[][] edgeTo = new int[energy.length][energy[0].length];
        // init
        for (int i = 0; i < energy.length; i++) {
            for (int j = 0; j < energy[i].length; j++) {
                distTo[i][j] = Integer.MAX_VALUE;
                edgeTo[i][j] = -1;
            }
        }
        // init first row
        for (int i = 0; i < energy[0].length; i++) {
            distTo[0][i] = energy[0][i];
        }
        // relax each vertice
        for (int y = 0; y < energy.length - 1; y++) {
            for (int x = 0; x < energy[y].length; x++) {
                relaxVertices(x, y, x + 1, y + 1, distTo, edgeTo);
                relaxVertices(x, y, x, y + 1, distTo, edgeTo);
                relaxVertices(x, y, x - 1, y + 1, distTo, edgeTo);
            }
        }

        // 溯源
        int[] verticalSeam = new int[energy.length];
        int index = -1;
        double minDistTo = Integer.MAX_VALUE;

        for (int i = 0; i < distTo[0].length; i++) {
            if (minDistTo > distTo[distTo.length - 1][i]) {
                minDistTo = distTo[distTo.length - 1][i];
                index = i;
            }
        }

        for (int y = verticalSeam.length - 1; y >= 0; y--) {
            verticalSeam[y] = index;
            index = edgeTo[y][index];
        }

        return verticalSeam;
    }

    private void relaxVertices(int x, int y, int neighborX, int neighborY, double[][] distTo, int[][] edgeTo) {
        // 当前节点的 energy
        if (neighborX < 0 || neighborX >= energy[0].length) {
            return;
        }
        if (distTo[neighborY][neighborX] > distTo[y][x] + energy[neighborY][neighborX]) {
            distTo[neighborY][neighborX] = distTo[y][x] + energy[neighborY][neighborX];
            edgeTo[neighborY][neighborX] = x;   // 记录 col 即可
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (!valid(seam, width(), height())) {
            throw new IllegalArgumentException();
        }
        if (VERTICAL) transpose();
        removeSeam(seam);
    }


    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!valid(seam, height(), width())) {
            throw new IllegalArgumentException();
        }
        if (!VERTICAL) transpose();
        removeSeam(seam);
    }

    private void removeSeam(int[] seam) {
        int[][] updateRGB = new int[RGB.length][RGB[0].length - 1];
        double[][] updateEnergy = new double[energy.length][energy[0].length - 1];
        // directly copy by row
        for (int i = 0; i < seam.length; i++) {
            int index = seam[i];

            int leftBound = index - 0;
            int rightBound = (RGB[i].length - 1) - index;

            if (leftBound != 0) {
                System.arraycopy(RGB[i], 0, updateRGB[i], 0, leftBound);
                System.arraycopy(energy[i], 0, updateEnergy[i], 0, leftBound);
            }
            if (rightBound != 0) {
                System.arraycopy(RGB[i], index + 1, updateRGB[i], index, rightBound);
                System.arraycopy(energy[i], index + 1, updateEnergy[i], index, rightBound);
            }
        }
        RGB = updateRGB;
        // update local energy in updateEnergy
        for (int i = 0; i < seam.length; i++) {
            int index = seam[i];
            // 不是左边界
            if (index > 1) {
                updateEnergy[i][index - 1] = computeEnergy(i, index - 1);
            }
            // 不是右边界
            if (index < updateEnergy[i].length) {
                updateEnergy[i][index] = computeEnergy(i, index);
            }
        }

        energy = updateEnergy;
    }

    private boolean valid(int[] seam, int height, int width) {
        if (seam == null) {
            return false;
        }
        if (width <= 1 || seam.length != height) {
            return false;
        }

        if (seam[0] < 0 || seam[0] >= width) {
            return false;
        }

        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width || Math.abs(seam[i] - seam[i - 1]) > 1) {
                return false;
            }
        }
        return true;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());

        SeamCarver sc = new SeamCarver(picture);

        StdOut.printf("Printing energy calculated for each pixel.\n");

        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                StdOut.printf("%9.2f ", sc.energy(col, row));
            StdOut.println();
        }
    }
}

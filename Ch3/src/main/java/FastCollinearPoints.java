import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FastCollinearPoints
 * 
 * @author lenovo
 * @version 2023/07/20 21:09
 **/
public class FastCollinearPoints {

    private List<LineSegment> lineSegments;

    private boolean check(Point[] points) {
        if (points == null) {
            return true;
        }

        for (Point point : points) {
            if (point == null) {
                return true;
            }
        }

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].compareTo(points[j]) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public FastCollinearPoints(Point[] points) { // finds all line segments containing 4 or more points
        if (check(points)) {
            throw new IllegalArgumentException();
        }

        lineSegments = new ArrayList<>();

        int n = points.length;
        if (n < 4) {
            return;
        }

        Arrays.sort(points);

        Point[] tmp = Arrays.copyOf(points, n);
        // bug: 用令一个数组来作为基准排序
        for (Point origin : points) {
            // 按照斜率排序
            Arrays.sort(tmp, origin.slopeOrder());
            // 如果符合共线条件：4点共线的话，找到最大共线点和最小共线点
            // 注意最大共线点和最小共线点可能不止 1 对，既可能有多条共线
            int i, j;
            for (i = 1; i < n;) {
                j = i + 1;
                while (j < n && origin.slopeTo(tmp[i]) == origin.slopeTo(tmp[j])) {
                    j++;
                }
                // 判断是否符合共线条件，以及 origin 是否为最小点（避免线段重复）
                if (j - i >= 3 && tmp[0].compareTo(min(tmp, i, j - 1)) < 0) {
                    lineSegments.add(new LineSegment(tmp[0], max(tmp, i, j - 1))); // 右端点为最大点
                }
                // 检查下一线段
                if (j == n) {
                    break;
                }
                i = j;
            }
        }
    }

    private Point min(Point[] points, int low, int high) {
        Point res;
        res = points[low];
        for (int i = low + 1; i <= high; i++) {
            if (res.compareTo(points[i]) > 0) {
                res = points[i];
            }
        }

        return res;
    }

    private Point max(Point[] points, int low, int high) {
        Point res;
        res = points[low];
        for (int i = low + 1; i <= high; i++) {
            if (res.compareTo(points[i]) < 0) {
                res = points[i];
            }
        }

        return res;
    }

    public int numberOfSegments() { // the number of line segments
        return lineSegments.size();
    }

    public LineSegment[] segments() { // the line segments
        LineSegment[] res = new LineSegment[numberOfSegments()];
        int i = 0;
        for (LineSegment lineSegment : lineSegments) {
            res[i++] = lineSegment;
        }

        return res;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}

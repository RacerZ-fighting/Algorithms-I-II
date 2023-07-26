import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BruteCollinearPoints
 * 
 * @author lenovo
 * @version 2023/07/20 20:27
 **/
public class BruteCollinearPoints {

    private List<LineSegment> res;

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

    public BruteCollinearPoints(Point[] points) { // finds all line segments containing 4 points
        int n = points.length;
        res = new ArrayList<>();
        if (check(points)) {
            throw new IllegalArgumentException();
        }

        // bug
        if (n < 4) {
            return;
        }

        Point[] tmp = Arrays.copyOf(points, n);
        Arrays.sort(tmp);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int z = k + 1; z < n; z++) {
                        Point p, q, r, s;
                        p = tmp[i];
                        q = tmp[j];
                        r = tmp[k];
                        s = tmp[z];
                        double s1 = p.slopeTo(q);
                        double s2 = p.slopeTo(r);
                        double s3 = p.slopeTo(s);
                        if (s1 == s2 && s2 == s3) {
                            res.add(new LineSegment(p, s));
                        }
                    }
                }
            }
        }
    }

    public int numberOfSegments() {        // the number of line segments
        return res.size();
    }

    public LineSegment[] segments() {              // the line segments
        LineSegment[] lineSegments = new LineSegment[numberOfSegments()];
        for (int i = 0; i < numberOfSegments(); i ++) {
            lineSegments[i] = res.get(i);
        }

        return lineSegments;
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author by RacerZ
 * @date 2023/7/31.
 */
public class PointSET {
    private  TreeSet<Point2D> treeSet;
    public PointSET() {                             // construct an empty set of points
        treeSet = new TreeSet<>();
    }

    /* is the set empty? */
    public boolean isEmpty() {
        return treeSet.isEmpty();
    }

    /* number of points in the set */
    public int size() {
       return treeSet.size();
    }

    /* add the point to the set (if it is not already in the set) */
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (!contains(p)) {
            treeSet.add(p);
        }
    }

    /* does the set contain point p? */
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return treeSet.contains(p);
    }

    /* draw all points to standard draw */
    public void draw() {
        for (Point2D point2D : treeSet) {
            point2D.draw();
        }
    }

    /* all points that are inside the rectangle (or on the boundary) */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> res = new LinkedList<Point2D>();
        for (Point2D point2D : treeSet) {
            if (rect.contains(point2D)) {
                res.add(point2D);
            }
        }

        return res;
    }

    /* a nearest neighbor in the set to point p; null if the set is empty */
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return null;
        }
        Point2D res = null;

        double distance = Double.MAX_VALUE;
        for (Point2D point2D : treeSet) {
            double tmp = point2D.distanceTo(p);
            if (tmp < distance) {
                distance = tmp;
                res = point2D;
            }
        }

        return res;
    }

    public static void main(String[] args) {

    }
}

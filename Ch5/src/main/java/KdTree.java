import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author by RacerZ
 * @date 2023/7/31.
 */
public class KdTree {
    private enum SEPARATOR { VERTICAL, HORIZONTAL };

    private Node root;
    private int size;

    private void checkNull(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Point2D q) {
        checkNull(q);
        Node node = root;
        while (node != null) {
            if (node.p.equals(q)) {
                return true;
            }
            // TODO: how to compare ?
            if (node.isTopOrRightOf(q)) {
                node = node.lb;
            } else {
                node = node.rt;
            }
        }

        return false;
    }

    public Point2D nearest(Point2D p) {
        checkNull(p);
        return isEmpty() ? null : nearest(root, p, root.p);
    }

    private Point2D nearest(Node node, Point2D target, Point2D closest) {
        if (node == null) {
            return closest;
        }

        double closestDict = closest.distanceTo(target);
        // if the closest point discovered so far is closer than the distance between
        // the query point and the rectangle corresponding to a node, there is no need to explore
        // that node (or its subtrees)
        if (node.rect.distanceSquaredTo(target) < closestDict) {
            double nodeDict = node.p.distanceTo( target);
            // 判断目标点距离 target 近
            if (nodeDict < closestDict) {
                closest = node.p;
            }
            if (node.isTopOrRightOf(target)) {
                // 优先搜索左子树
                closest = nearest(node.lb, target, closest);
                closest = nearest(node.rt, target, closest);
            } else {
                closest = nearest(node.rt, target, closest);
                closest = nearest(node.lb, target, closest);
            }
        }
        return closest;
    }

    public void insert(Point2D q) {
        checkNull(q);
        if (root == null) {
            root = new Node(SEPARATOR.VERTICAL, q, new RectHV(0, 0, 1, 1));
            size++;
            return;
        }
        Node prev = null;
        Node cur = root;
        do {
            if (cur.p.equals(q)) {
                return;
            }
            prev = cur;
            cur = cur.isTopOrRightOf(q) ? cur.lb : cur.rt;
        } while (cur != null);

        if (prev.isTopOrRightOf(q)) {
            // TODO: how to decide cur div ?
            prev.lb = new Node(prev.nextSp(), q, prev.nextLB());
        } else {
            prev.rt = new Node(prev.nextSp(), q, prev.nextRT());
        }

        size++;
    }

    public void draw() {
        draw(root, null);
    }

    private void draw(Node node, Node parent) {
        if (node == null) {
            return;
        }

        /*if (parent == null) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(node.p.x(), 0, node.p.x(), 1);
        } else */
        if (node.sp == SEPARATOR.VERTICAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }

        // draw the point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.point(node.p.x(), node.p.y());
        // recursively draw
        draw(node.lb, node);
        draw(node.rt, node);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> res = new LinkedList<>();
        addAll(root, rect, res);
        return res;
    }

    private void addAll(Node node, RectHV rect, List<Point2D> result) {
        if (node == null) {
            return;
        }
        // 如果当前节点恰好包含于 rect 中
        if (rect.contains(node.p)) {
            result.add(node.p);
            addAll(node.lb, rect, result);
            addAll(node.rt, rect, result);
            return;
        }

        // 判断 rect 落在了哪一侧
        if (node.isTopOrRightOf(new Point2D(rect.xmin(), rect.ymin()))) {
            addAll(node.lb, rect, result);
        }
        // A subtree is searched only if it might contain a point contained in the query rectangle
        // 只有 contain 的情况才需要去搜索右子树
        if (!node.isTopOrRightOf(new Point2D(rect.xmax(), rect.ymax()))) {
            addAll(node.rt, rect, result);
        }
    }

    private static class Node {
        private SEPARATOR sp;
        private Point2D p;
        private RectHV rect;    // axis-aligned rectangle corresponding to this node
        private Node lb;
        private Node rt;

        private SEPARATOR nextSp() {
            return sp == SEPARATOR.VERTICAL
                    ? SEPARATOR.HORIZONTAL
                    : SEPARATOR.VERTICAL;
        }

        private RectHV nextLB() {
            return (sp == SEPARATOR.VERTICAL
                    ? new RectHV(rect.xmin(), rect.ymin(), p.x(), rect.ymax())
                    : new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), p.y()));
        }

        private RectHV nextRT() {
            return (sp == SEPARATOR.VERTICAL
                    ? new RectHV(p.x(), rect.ymin(), rect.xmax(), rect.ymax())
                    : new RectHV(rect.xmin(), p.y(), rect.xmax(), rect.ymax()));
        }

        public Node(SEPARATOR sp, Point2D p, RectHV rect) {
            this.sp = sp;
            this.p = p;
            this.rect = rect;
        }

        public boolean isTopOrRightOf(Point2D q) {
            return (this.sp == SEPARATOR.VERTICAL && p.x() > q.x())
                    || (this.sp == SEPARATOR.HORIZONTAL && p.y() > q.y());
        }
    }

    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        KdTree kdtree = new KdTree();
        String filename = args[0];
        In in = new In(filename);

        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        // draw the points
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        kdtree.draw();
        StdDraw.show();
    }
}

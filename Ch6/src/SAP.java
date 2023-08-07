import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.In;
/**
 * @author by RacerZ
 * @date 2023/8/7.
 */
public class SAP {
    private final Digraph Graph;
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        Graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v >= Graph.V() || w >= Graph.V()) {
            throw new IllegalArgumentException();
        }

        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(Graph, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(Graph, w);
        // 初始化最大值
        int minPath = Integer.MAX_VALUE;
        // 遍历寻找最短路径
        for (int i = 0; i < Graph.V(); i ++) {
            if (bfdp2.hasPathTo(i) && bfdp1.hasPathTo(i)) {
                minPath = Math.min(bfdp2.distTo(i) + bfdp1.distTo(i), minPath);
            }
        }

        return minPath == Integer.MAX_VALUE ? -1 : minPath;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v >= Graph.V() || w >= Graph.V()) {
            throw new IllegalArgumentException();
        }
        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(Graph, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(Graph, w);
        // 初始化最大值
        int minPath = Integer.MAX_VALUE;
        int common = -1;

        // 遍历寻找最短路径
        for (int i = 0; i < Graph.V(); i ++) {
            if (bfdp2.hasPathTo(i) && bfdp1.hasPathTo(i)) {
                int cur = bfdp1.distTo(i) + bfdp2.distTo(i);
                if (minPath > cur) {
                    minPath = cur;
                    common = i;
                }
            }
        }

        return common;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        // check null
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        // check iterable argument contains a null item
        if (!hasValue(v) || !hasValue(w)) {
            throw new IllegalArgumentException();
        }

        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(Graph, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(Graph, w);
        // 初始化最大值
        int minPath = Integer.MAX_VALUE;
        for (int i = 0; i < Graph.V(); i ++) {
            if (bfdp1.hasPathTo(i) && bfdp2.hasPathTo(i)) {
                minPath = Math.min(minPath, bfdp1.distTo(i) + bfdp2.distTo(i));
            }
        }

        return minPath == Integer.MAX_VALUE ? -1 : minPath;
    }

    private boolean hasValue(Iterable<Integer> integers) {
        for (Integer num : integers) {
            if (num == null) {
                return false;
            }
        }

        return true;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        // check null
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }
        // check iterable argument contains a null item
        if (!hasValue(v) || !hasValue(w)) {
            throw new IllegalArgumentException();
        }

        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(Graph, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(Graph, w);
        // 初始化最大值
        int minPath = Integer.MAX_VALUE;
        int common = -1;

        // 遍历寻找最短路径
        for (int i = 0; i < Graph.V(); i ++) {
            if (bfdp2.hasPathTo(i) && bfdp1.hasPathTo(i)) {
                int cur = bfdp1.distTo(i) + bfdp2.distTo(i);
                if (minPath > cur) {
                    minPath = cur;
                    common = i;
                }
            }
        }

        return common;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

}

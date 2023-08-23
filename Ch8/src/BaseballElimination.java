import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author by RacerZ
 * @date 2023/8/23.
 */
public class BaseballElimination {
    private final HashMap<String, Integer> w;
    private final HashMap<String, Integer> l;
    private final HashMap<String, Integer> r;
    private boolean[] isSolved;
    private HashMap<String, Set<String>> result;
    private final HashMap<String, Integer> nameToId;
    private final String[] idToName;
    private final int num;
    private final int[][] g;

    /* create a baseball division from given filename in format specified below */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        num = in.readInt();
        w = new HashMap<>();
        l = new HashMap<>();
        r = new HashMap<>();
        nameToId = new HashMap<>();
        idToName = new String[num];
        g = new int[num][num];
        isSolved = new boolean[num];
        result = new HashMap<>();

        for (int i = 0; i < num; i++) {
            String name = in.readString();

            w.put(name, in.readInt());
            l.put(name, in.readInt());
            r.put(name, in.readInt());
            nameToId.put(name, i);
            idToName[i] = name;
            // System.out.printf(idToName[i] + " " + w.get(name) + " " + r.get(name) + " ");
            for (int j = 0; j < num; j++) {
                g[i][j] = in.readInt();
                // System.out.printf(g[i][j] + " ");
            }
            // System.out.println();
        }


    }

    /* number of teams */
    public int numberOfTeams() {
        return num;
    }
    public Iterable<String> teams() {
        return w.keySet();
    }
    public int wins(String team) {
        if (!w.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return w.get(team);
    }
    public int losses(String team) {
        if (!l.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return l.get(team);
    }
    public int remaining(String team) {
        if (!r.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        return r.get(team);
    }
    public int against(String team1, String team2) {
        if (!nameToId.containsKey(team1) || !nameToId.containsKey(team2)) {
            throw new IllegalArgumentException();
        }
        int id1 = nameToId.get(team1), id2 = nameToId.get(team2);
        return g[id1][id2];
    }

    /*  is given team eliminated? */
    public boolean isEliminated(String team) {
        if (!nameToId.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        int id = nameToId.get(team);
        if (!isSolved[id]) {
            solve(team);
        }

        return result.get(team) != null;
    }

    private FlowNetwork createNetwork(String team) {
        int s = 0;  // denote for source
        int m = (num - 1) * (num - 2) / 2 + 1;
        int t = num * (num - 1) / 2 + 2;

        int id = nameToId.get(team);
        int temp = 0;
        // what if vertex counts equal to t ? error
        FlowNetwork G = new FlowNetwork(t + 1);
        for (int i = 0; i < num; i++) {
            if (i == id) {
                continue;
            }
            for (int j = i + 1; j < num; j++) {
                if (j == id) {
                    continue;
                }
                temp++;
                G.addEdge(new FlowEdge(s, temp, g[i][j]));
                G.addEdge(new FlowEdge(temp, m + i, Double.POSITIVE_INFINITY));
                G.addEdge(new FlowEdge(temp, m + j, Double.POSITIVE_INFINITY));
            }
        }

        for (int i = 0; i < num; i ++) {
            if (id == i) {
                continue;
            }
            String name = idToName[i];
            G.addEdge(new FlowEdge(m + i, t, w.get(team) + r.get(team) - w.get(name)));
        }

        return G;
    }

    private boolean check(String team) {
        int totalWin = 0, remainWin = 0;
        Iterable<String> strings = certificateOfElimination(team);
        int size = 0;
        for (String other : strings) {
            totalWin += w.get(other);
            remainWin += r.get(other);
            size++;
        }

        return w.get(team) < (totalWin + remainWin) / size;
    }

    private void solve(String team) {
        if (isSolved[nameToId.get(team)]) {
            return;
        }
        isSolved[nameToId.get(team)] = true;

        // Trivial elimination
        for (int i = 0; i < num; i++) {
            if (w.get(team) + r.get(team) < w.get(idToName[i])) {
                HashSet<String> res = new HashSet<>();
                res.add(idToName[i]);
                result.put(team, res);
                return;
            }
        }

        int s = 0;
        int t = (num - 1) * num / 2 + 2;
        int m = (num - 1) * (num - 2) / 2 + 1;
        FlowNetwork network = createNetwork(team);
        FordFulkerson fordFulkerson = new FordFulkerson(network, s, t);
        for (int i = 1; i < m; i++) {
            if (fordFulkerson.inCut(i)) {
                // prove that there exists any augmentingPath
                HashSet<String> ans = new HashSet<>();
                for (int j = 0; j < num; j++) {
                    // 加入所有未比完的比赛对应的队伍
                    if (fordFulkerson.inCut(j + m)) {
                        ans.add(idToName[j]);
                    }
                }
                result.put(team, ans);
                return;
            }
        }
        result.put(team, null);
    }

    /* subset R of teams that eliminates given team; null if not eliminated */
    public Iterable<String> certificateOfElimination(String team) {
        if (!nameToId.containsKey(team)) {
            throw new IllegalArgumentException();
        }
        int id = nameToId.get(team);
        if (!isSolved[id]) {
            solve(team);
        }

        return result.get(team);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            // System.out.println("----> " + division.check(team));
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}

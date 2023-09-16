import java.util.Arrays;
import java.util.HashMap;

/**
 * @author by RacerZ
 * @date 2023/9/16.
 */
public class CircularSuffixArray {
    private final int n;
    private final String ref;
    private final CircularSuffix[] originalSuffix;
    private final CircularSuffix[] sortedSuffix;
    private final HashMap<Integer, Integer> match;    // sorted 项在 original 中的位置

    /* circular suffix array of s */
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        this.n = s.length();
        this.ref = s;
        originalSuffix = new CircularSuffix[n];
        sortedSuffix = new CircularSuffix[n];
        match = new HashMap<>();
        for (int i = 0; i < n; i++) {
            originalSuffix[i] = new CircularSuffix(i, ref);
            sortedSuffix[i] = new CircularSuffix(i, ref);
        }
        Arrays.sort(sortedSuffix);

        for (int i = 0; i < n; i++) {
            CircularSuffix e = originalSuffix[i];
            match.put(calcMatch(e), i);
        }

        // show();
    }

    private int calcMatch(CircularSuffix e) {
        int l = 0, r = n;
        while (l < r) {
            int mid = l + r >> 1;
            if (sortedSuffix[mid].compareTo(e) < 0) {
                l = mid + 1;
            } else if (sortedSuffix[mid].compareTo(e) > 0) {
                r = mid - 1;
            } else {
                return mid;
            }
        }

        return r;
    }

    private void show() {
        for (int i = 0; i < n; i++) {
            System.out.printf("%d\t", i);

            int p1 = originalSuffix[i].point;
            int p2 = sortedSuffix[i].point;
            for (int j = 0; j < n; j++) {
                System.out.printf("%c", ref.charAt(p1));
                p1 = (++p1 == n ? 0 : p1);
            }
            System.out.print("\t");
            for (int j = 0; j < n; j++) {
                System.out.printf("%c", ref.charAt(p2));
                p2 = (++p2 == n ? 0 : p2);
            }
            System.out.printf("\t%d", index(i));
            System.out.println();
        }
    }

    /* length of s */
    public int length() {
        return n;
    }

    /* returns index of ith sorted suffix */
    public int index(int i) {
        if (i < 0 || i >= n) {
            throw new IllegalArgumentException();
        }
        return match.get(i);
    }

    /* unit test */
    public static void main(String[] args) {
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(args[0]);
    }

    private class CircularSuffix implements Comparable<CircularSuffix> {
        public final int point;
        public final String ref;
        public CircularSuffix(int point, String ref) {
            this.point = point;
            this.ref = ref;
        }

        @Override
        public int compareTo(CircularSuffix o) {
            int st1 = point, st2 = o.point;
            for (int i = 0; i < n; i ++) {
                if (ref.charAt(st1) < ref.charAt(st2)) {
                    return -1;
                } else if (ref.charAt(st1) > ref.charAt(st2)) {
                    return 1;
                }

                st1 = (++st1 == n) ? 0 : st1;
                st2 = (++st2 == n) ? 0 : st2;
            }

            return 0;
        }
    }
}

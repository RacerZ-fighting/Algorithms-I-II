import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * @author by RacerZ
 * @date 2023/9/16.
 */
public class BurrowsWheeler {
    private static final int LG_R = 8;
    private static final int R = 256;

    /**
     apply Burrows-Wheeler transform,
     reading from standard input and writing to standard output
     time: O(n + R) (or better)
     */
    public static void transform() {
        while (!BinaryStdIn.isEmpty()) {
            String str = BinaryStdIn.readString();
            CircularSuffixArray circularSuffixArray = new CircularSuffixArray(str);

            int n = circularSuffixArray.length();

            for (int i = 0; i < n; i++) {
                if (circularSuffixArray.index(i) == 0) {
                    BinaryStdOut.write(i, 32);
                    break;
                }
            }

            for (int i = 0; i < n; i++) {
                int id = circularSuffixArray.index(i);
                id = (id + n - 1) % n;
                BinaryStdOut.write(str.charAt(id), LG_R);
            }
        }
        BinaryStdOut.close();
    }

    /**
       apply Burrows-Wheeler inverse transform,
       reading from standard input and writing to standard output
     */
    public static void inverseTransform() {

        while (!BinaryStdIn.isEmpty()) {
            int first = BinaryStdIn.readInt();
            String s = BinaryStdIn.readString();
            int n = s.length();
            int[] next = new int[n];

            // sort and calc the next array
            char[] aux = s.toCharArray();
            int[] count = new int[R + 1];

            for (int i = 0; i < n; i++) {
                count[s.charAt(i) + 1]++;
            }

            for (int r = 0; r < R; r++) {
                count[r + 1] += count[r];
            }

            for (int i = 0; i < n; i++) {
                next[count[s.charAt(i)]] = i;
                aux[count[s.charAt(i)]++] = s.charAt(i);
            }
            // recover the plain
            for (int i = 0; i < n; i++) {
                BinaryStdOut.write(aux[first], LG_R);
                first = next[first];
            }
         }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}

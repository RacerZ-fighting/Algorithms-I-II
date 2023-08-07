import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

/**
 * @author by RacerZ
 * @date 2023/8/7.
 */
public class Outcast {
    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;

    }
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException();
        }

        String res = null;
        int dist = 0;
        for (int i = 0; i < nouns.length; i ++) {
            int tmp = 0;
            for (int j = 0; j < nouns.length; j ++) {
                if (i != j) {
                    int distance = wordNet.distance(nouns[i], nouns[j]);
                    tmp += distance;
                }
            }
            if (tmp > dist) {
                dist = tmp;
                res = nouns[i];
            }
        }

        return res;
    }
    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

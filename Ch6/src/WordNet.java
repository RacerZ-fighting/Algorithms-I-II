import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


/**
 * @author by RacerZ
 * @date 2023/8/7.
 */
public class WordNet {
    // TODO:
    //  1. 同义词本身可能存在于多个词集里头，即携带多个整数编号
    //  2. 数据结构1: 维护同义词集之间上位关系（图边集）
    //  3. 数据结构2: 根据观察1，得到同义词与多个整数编号的关系
    //  4. 数据结构3: 维护同义词集本身

    private final ArrayList<Set<String>> synSets;      // 4
    private final ArrayList<String> originSyn; // index order
    private final Digraph wordNet;  //  2
    private final Map<String, Set<Integer>> nameToSets; // 1
    private final int nouns;    // wordNet nouns
    private final SAP serviceSAP;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        synSets = new ArrayList<>();    // 恰好下标对应集合标号
        nameToSets = new HashMap<>();
        originSyn = new ArrayList<>();

        // process synsets
        readSynsets(synsets);
        nouns = synSets.size();
        wordNet = new Digraph(nouns);
        // process hyoernyms
        readHypernyms(hypernyms);
        // TODO: test
        // System.out.println(wordNet.V());
        // System.out.println(wordNet.E());
        // System.out.println(nameToSets.keySet().size());
        serviceSAP = new SAP(wordNet);
    }

    private void readSynsets(String synsets) {
        In in = new In(synsets);
        String[] fields;
        while (!in.isEmpty()) {
           fields = in.readLine().split(",");
           // TODO origin
            Set<String> tmp = new HashSet<>();
            originSyn.add(fields[1]);
            for (String field : fields[1].split(" ")) {
                tmp.add(field);
                // 判断当前单词是否存在映射关系 string => int
                if (!nameToSets.containsKey(field)) {
                    // 新创建 set
                    Set<Integer> newNoun = new HashSet<>();
                    newNoun.add(Integer.parseInt(fields[0]));
                    nameToSets.put(field, newNoun);
                } else {
                    Set<Integer> integers = nameToSets.get(field);
                    integers.add(Integer.parseInt(fields[0]));
                    nameToSets.put(field, integers);
                }
            }
            // 加入到原始词集当中
            synSets.add(tmp);
        }
    }

    private void readHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        String[] fields;
        while (!in.isEmpty()) {
            fields = in.readLine().split(",");
            int v = Integer.parseInt(fields[0]);
            // 均逗号分割
            for (int i = 1; i < fields.length; i ++) {
                int w = Integer.parseInt(fields[i]);
                wordNet.addEdge(v, w);
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nameToSets.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return nameToSets.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        Set<Integer> v = nameToSets.get(nounA);
        Set<Integer> w = nameToSets.get(nounB);
        /*for (int num : w) {
            System.out.println(num + ": " + originSyn.get(num));
        }*/
        return serviceSAP.length(v, w);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    // TODO: 这里说的是第二字段，因此需要全部读出
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        Set<Integer> v = nameToSets.get(nounA);
        Set<Integer> w = nameToSets.get(nounB);
        int ancestor = serviceSAP.ancestor(v, w);

        return originSyn.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}

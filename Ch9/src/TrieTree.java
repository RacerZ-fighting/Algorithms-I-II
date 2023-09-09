import edu.princeton.cs.algs4.TrieST;

/**
 * @author by RacerZ
 * @date 2023/9/9.
 */
public class TrieTree {
    private final Node root;      // root of trie

    public TrieTree() {
        root = new Node();
    }

    // Consider a nonrecursive implementation of the prefix query operation
    public void insert(String word) {
        Node node = root;
        int i = 0;
        while (i < word.length()) {
            char c = word.charAt(i);
            // 判断当前节点是否已经存在
            if (!node.contains(c)) {
                node.put(c);
            }
            node = node.get(c);
            // TODO: 优化2
            // TODO: 特殊处理字典中的 Q，因为 board 中只可能会出现 Qu, 不会出现单独的 Q 或者 Qx(x != u)
            if (c == 'Q') {
                i++;
                if (i == word.length() || word.charAt(i) != 'U') {
                    return;
                }
            }
            i++;
        }
        node.setEnd(word);
    }

    public boolean search(String word) {
        Node node = root;
        int i = 0;
        while (i < word.length()) {
            char c = word.charAt(i);
            if (node.contains(c)) {
                node = node.get(c);
            } else {
                return false;
            }
            // TODO: 优化2
            if (c == 'Q') {
                i++;
                if (i == word.length() || word.charAt(i) != 'U') {
                    return false;
                }
            }
            i++;
        }

        return node.isEnd();
    }

    // TODO: Exploit that fact that when you perform a prefix query operation,
    //  it is usually almost identical to the previous prefix query, except that it is one letter longer
    public Node prefixNode(char c, Node cache) {
        if (cache == null) {
            cache = root;
        }

        if (cache.contains(c)) {
            // update the cache
            cache = cache.get(c);
        } else {
            cache = null;
        }

        return cache;
    }

    public static class Node {
        // 26-way trie node
        private final Node[] links;
        private boolean hasChild;
        // TODO: optimal operation 6
        private String word;
        public Node() {
            hasChild = false;
            links = new Node[26];
            word = null;
        }

        public boolean isEnd() {
            return word != null;
        }
        public void setEnd(String w) {
            this.word = w;
        }
        public void put(char c) {
            links[c - 'A'] = new Node();
            hasChild = true;
        }

        public Node get(char c) {
            return links[c - 'A'];
        }

        public boolean contains(char c) {
            return links[c - 'A'] != null;
        }

        public boolean hasChild() {
            return hasChild;
        }

        public String getWord() {
            return word;
        }
    }
}

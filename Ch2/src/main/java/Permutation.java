import edu.princeton.cs.algs4.StdIn;

import java.util.Iterator;

/**
 * Permutation Client
 * 
 * @author lenovo
 * @version 2023/07/17 10:52
 **/
public class Permutation {
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: java permutation number");
        }

        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<>();

        while (!StdIn.isEmpty()) {
            queue.enqueue(StdIn.readString());
        }


        for (int i = 0; i < k; i++) {
            System.out.println(queue.dequeue());
        }

    }
}

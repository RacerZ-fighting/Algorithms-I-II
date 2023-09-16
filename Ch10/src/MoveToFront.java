import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

/**
 * @author by RacerZ
 * @date 2023/9/16.
 */
public class MoveToFront {
    private static final int R = 256;
    private static final int RELEVANT_BITS = 8;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList<Character> sequence = generateInitialSequence();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = sequence.indexOf((Object) c);// 强转成引用类型，否则会变为下标
            BinaryStdOut.write(index, RELEVANT_BITS);
            sequence.remove((Object) c);
            sequence.addFirst(c);
        }

        BinaryStdOut.close();
    }

    private static LinkedList<Character> generateInitialSequence() {
        LinkedList<Character> sequence = new LinkedList<>();
        for (int i = 0; i < R; i++) {
            sequence.add((char) i);
        }
        return sequence;
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        LinkedList<Character> sequence = generateInitialSequence();
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readInt(RELEVANT_BITS);
            Character c = sequence.get(index);
            BinaryStdOut.write(c, RELEVANT_BITS);
            sequence.remove((Object) c);
            sequence.addFirst(c);
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * RandomizedQueue the item removed is chosen uniformly at random among items in the data structure
 * 
 * @author RacerZ
 * @version 2023/07/17 10:23
 **/
public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] s;
    private int sizeOfItem;
    private int capacity;
    private int originalSize = 8;
    private double ratio = 0.25;

    // construct an empty randomized queue
    public RandomizedQueue() {
        s = (Item[])new Object[originalSize];
        sizeOfItem = 0;
        capacity = originalSize;
    }

    private void resize(int newCapacity) {
        Item[] array = (Item[])new Object[newCapacity];
        for (int i = 0; i < sizeOfItem; i++) {
            array[i] = s[i];
        }

        s = array;
        capacity = newCapacity;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return sizeOfItem == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return sizeOfItem;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (sizeOfItem == capacity) {
            resize(2 * capacity);
        }

        s[sizeOfItem++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        // 随机取索引
        int index = StdRandom.uniformInt(sizeOfItem);
        Item value = s[index];
        // 交换至队尾
        s[index] = s[sizeOfItem - 1];
        s[sizeOfItem - 1] = null;
        sizeOfItem--;
        // 考虑最后一次缩放
        if (sizeOfItem > 0 && ratio >= (double) sizeOfItem / capacity) {
            resize(capacity / 2);
        }

        return value;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int index = StdRandom.uniformInt(sizeOfItem);
        return s[index];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomIterator<Item>();
    }

    private class RandomIterator<Item> implements Iterator<Item> {
        private int remains;
        private Item[] arrays;

        public RandomIterator() {
            this.remains = sizeOfItem;
            arrays = (Item[])new Object[sizeOfItem];

            for (int i = 0; i < sizeOfItem; i++) {
                arrays[i] = (Item)s[i];
            }

            // shuffle
            StdRandom.shuffle(arrays);
        }

        @Override
        public boolean hasNext() {
            return remains > 0;
        }

        @Override
        public Item next() {
            if (remains == 0) {
                throw new NoSuchElementException();
            }

            return arrays[--remains];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> strings = new RandomizedQueue<>();
        for (int i = 0; i < 9; i ++) {
            char p = (char) ('A' + i);
            strings.enqueue(String.valueOf(p));
        }

        for (String c : strings) {
            System.out.println(c);
        }
        System.out.println("-------------------");
        for (String c : strings) {
            System.out.println(c);
        }
    }

}

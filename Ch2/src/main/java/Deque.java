import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Deque
 * 
 * @author RacerZ
 * @version 2023/07/17 09:34
 **/
public class Deque<Item> implements Iterable<Item> {

    private int sizeOfItem;

    private Node dummy;

    private class Node {
        Item item;
        Node next;
        Node pre;

        public Node(Item item, Node next, Node pre) {
            this.item = item;
            this.next = next;
            this.pre = pre;
        }

        public Node() {
            this.next = null;
            this.item = null;
            this.pre = null;
        }
    }

    // construct an empty deque 构建循环队列
    public Deque() {
        // dummy block
        dummy = new Node();
        dummy.next = dummy;
        dummy.pre = dummy;
        sizeOfItem = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return sizeOfItem == 0;
    }

    // return the number of items on the deque
    public int size() {
        return sizeOfItem;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node t = new Node(item, dummy.next, dummy);
        dummy.next.pre = t;
        dummy.next = t;

        sizeOfItem++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node t = new Node(item, dummy, dummy.pre);
        dummy.pre.next = t;
        dummy.pre = t;

        sizeOfItem++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Item item = dummy.next.item;
        dummy.next.next.pre = dummy;
        dummy.next = dummy.next.next;

        sizeOfItem--;

        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        Item item = dummy.pre.item;
        dummy.pre.pre.next = dummy;
        dummy.pre = dummy.pre.pre;

        sizeOfItem--;

        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new ListIterator<Item>();
    }

    private class ListIterator<Item> implements Iterator<Item> {
        private Node current;
        private int remains;

        public ListIterator() {
            current = dummy.next;
            remains = sizeOfItem;
        }

        @Override
        public boolean hasNext() {
            return remains > 0;
        }

        @Override
        public Item next() {
            // special case: sizeOfItem == 0
            if (remains == 0) {
                throw new NoSuchElementException();
            }
            Item item = (Item) current.item;
            current = current.next;
            remains--;

            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();
        for (int i = 0; i < 5; i++) {
            deque.addFirst("A" + i);
        }

        for (int i = 0; i < 5; i++) {
            deque.addLast("B" + i);
        }

        for (String item : deque) {
            System.out.println(item);
        }

        System.out.println("deque has " + deque.size() + " elements in total");

        for (int i = 1; i <= 10; i++) {
            System.out.println(deque.removeFirst());
            System.out.println(deque.removeLast());
            System.out.println(deque.size());
        }
    }

}

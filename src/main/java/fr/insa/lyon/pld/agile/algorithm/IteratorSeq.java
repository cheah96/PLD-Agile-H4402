package fr.insa.lyon.pld.agile.algorithm;

import java.util.Collection;
import java.util.Iterator;

public class IteratorSeq implements Iterator<Integer> {

    private final Integer[] nodes;
    private int numberOfNodes;

    /**
     * Creates an iterator to iterate over a set of unexplored nodes.
     * 
     * @param unexploredNodes the set of unexplored nodes
     */
    public IteratorSeq(Collection<Integer> unexploredNodes) {
        nodes = new Integer[unexploredNodes.size()];
        numberOfNodes = 0;
        for (Integer s : unexploredNodes){
            nodes[numberOfNodes++] = s;
        }
    }

    @Override
    public boolean hasNext() {
        return numberOfNodes > 0;
    }

    @Override
    public Integer next() {
        return nodes[--numberOfNodes];
    }

    @Override
    public void remove() {}

}

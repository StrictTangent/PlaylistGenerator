// EmotionQueue
// May 2019 - Anna T

package com.example.playlistgenerator;

import java.io.Serializable;
import java.util.ArrayList;

public class EmotionQueue<E extends AttributeComparable<E>> implements Serializable {
    public ArrayList<E> myHeap;
    private int attribute;

    public EmotionQueue(int attribute) {
        this.attribute = attribute; // attribute we wish to prioritize
        myHeap = new ArrayList<E>();
    }


    //index right child
    public int right(int n) {
        return n*2 + 2;
    }

    //index left child
    public int left(int n) {
        return n*2 + 1;
    }

    //index parent
    public int parent(int n) {
        return (n-1)/2;
    }

    //gets the minimum value or the root
    public E getMin() {
        if (myHeap.size() <= 0)
            return null;
        else
            return myHeap.get(0);
    }

    //removes root by filling the hole
    public E remove() {
        if (myHeap.size() <= 0)
            return null;
        else {
            E min = myHeap.get(0);
            myHeap.set(0, myHeap.get(myHeap.size()-1));
            myHeap.remove(myHeap.size()-1);

            percolateDown(myHeap, 0); //recursively
            return min;
        }
    }

    // Swap two locations i and j
    public <E> void swap(ArrayList<E> l, int i, int j) {
        E temp = l.get(i);

        l.set(i, l.get(j));
        l.set(j, temp);
    }

    //insert into the arrayList
    public void insert(E item) {
        myHeap.add(item);
        int position = myHeap.size()-1;

        percolateUp(position);

    }

    //percolates UP to restore heap order property
    public void percolateUp( int i) {
        while (i > 0 && myHeap.get(i).compareTo(myHeap.get(parent(i)),
                attribute) < 0) {
            swap(myHeap, parent(i), i);
            i = parent(i);
        }

    }

    //checks if arraylist is empty
    public boolean isEmpty() {
        return myHeap.size() == 0;
    }



    //percolateDown to restore heap order property
    public  <E extends AttributeComparable<E>> void percolateDown(ArrayList<E> l, int i) {

        int least ;
        int left = left(i);
        int right = right(i);


        if (left <= l.size()-1 && l.get(left).compareTo(l.get(i),attribute) < 0){ //checks index
            //within bounds first
            least = left;
        } else {
            least = i;
        }
        if (right <= l.size()-1 && l.get(right).compareTo(l.get(least),attribute) < 0) {
            //checks index within bounds first

            least = right;
        }


        if (least != i) {
            swap(l, i, least);
            percolateDown(l, least); //check next level recursively
        }
    }


}

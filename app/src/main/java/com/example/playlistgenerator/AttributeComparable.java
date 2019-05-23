// AttributeComparable
// May 2019 - Paul Freeman
//
// A custom Comparable interface for Classes storing text to be analyzed
// for emotions/sentiment with a lexicon.

package com.example.playlistgenerator;

public interface AttributeComparable<T>{

    public int compareTo(T o, int a);

    public int getAttributeScore(int a);

}

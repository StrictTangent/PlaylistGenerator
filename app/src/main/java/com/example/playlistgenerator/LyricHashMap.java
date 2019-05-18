// LyricHashMap
// Paul Freeman - May 2019
// A Generic HashMap class intended to store lyrics for songs.

package com.example.playlistgenerator;

public class LyricHashMap<K,V>{

    //DEBUG FIELDS
    private final boolean DEBUG = false;
    public int collisions;
    public int maxNodes;
    //END DEBUG FIELDS


    // Size of Table
    private final int TABLE_SIZE =  70000; //117811; //6694763;
    // Array of Nodes
    private Node<K,V>[] database;

    // Construct empty LyricHashMap
    public LyricHashMap(){

        this.database = new Node[TABLE_SIZE];

        if (DEBUG){
            this.collisions = 0; //DEBUGGING VALUE
            this.maxNodes = 0; //DEBUGGING VALUE
        }

    }

    // Hash function. Returns an integer
    private int hashFunction(K key){
        return Math.abs(key.hashCode()) % TABLE_SIZE;
    }

    // Store a Value according to a Key
    public void put(K key, V value){

        int index = hashFunction(key);
        if (database[index] == null){
            database[index] = new Node<K,V>(key, value);
        } else {
            database[index] = new Node<K,V>(key, value, database[index]);

            //DEBUG CODE (PLEASE COMMENT OR REMOVE)
            if (DEBUG){
                this.collisions++;
                Node<K,V> current = database[index];
                int numNodes = 0;
                while (current != null){
                    numNodes++;
                    current = current.next;
                }
                if (numNodes > maxNodes) maxNodes = numNodes;
                System.out.println(numNodes);
            }
            //END DEBUG CODE


        }
    }


    // Return a Value according to a Key
    public V get(K key){
        int index = hashFunction(key);

        if (DEBUG)
            System.out.println("index: " + index);

        // Either return null or return the result of findValue();
        if (database[index] == null){
            return null;
        } else {
            return findValue(index, key);
        }
    }

    // Return the appropriate Value from a position in the array
    // by sequentially searching through the linked list of nodes at that position.
    private V findValue(int index, K key){
        Node<K,V> current = database[index];

        int numNodes = 0; //DEBUGGING VALUE

        while (current != null){
            //DEBUGGING CODE PLEASE REMOVE
            numNodes++;
            //END DEBUG CODE
            if (current.key.equals(key)){

                //DEBUGGING CODE PLEASE REMOVE
                if (DEBUG)
                    System.out.println(numNodes);
                //END DEBUG CODE

                return current.value;
            }
            current = current.next;

        }
        return null;
    }

    // Private class defines nodes for storing keys and values.
    // Each node represents a single data element.
    private static class Node<K,V>{

        public K key;
        public V value;
        public Node<K,V> next;

        // Construct with a Key and Value
        public Node(K key, V value){
            this(key, value, null);
        }

        // Construct with a Key, Value, and reference to next Node.
        public Node(K key, V value, Node next){
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

}
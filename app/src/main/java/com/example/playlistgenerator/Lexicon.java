// Lexicon
// Paul Freeman - April 22, 2019

// Reads in a lexicon in the format:
// line 1:     <attribute1> <attribute2> ... <attribute10>
// line 2-n:   <word> <score1> <score2> <score3> ... <score10>

// Holds the lexicon as a map of words to sets of values corresponding to attributes.

package com.example.playlistgenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Lexicon implements Serializable {

    private Map<String, int[]> map;  // a map to store words as keys and arrays of attribute scores as values.
    private List<String> attributes; // a simple list of attributes.

    // Construct Lexicon by passing an InputStream to be read in.
    public Lexicon(InputStream lexicon) {
        this.map = new TreeMap<String, int[]>();
        this.attributes = new ArrayList<String>();
        readStream(lexicon);
    }

    // Reads an InputStream representing a lexicon and builds the map for this lexicon.
    private void readStream(InputStream lexicon) {
        Scanner fileScanner = new Scanner(lexicon);

        // Read and set attributes from file (This corresponds to the first line in the lexicon .txt file).
        // THIS JUST CREATES THE LIST FOR "anger,anticipation,disgust,fear,joy,negative,positive,sadness,surprise,trust", etc...
        String line = fileScanner.nextLine();
        String[] tokens = line.split(",");
        for(int i = 0; i < tokens.length; i++)
            this.attributes.add(tokens[i]);

        // Read and map lexicon from file.
        while (fileScanner.hasNextLine()){
            line = fileScanner.nextLine();
            tokens = line.split(",");

            if (!map.keySet().contains(tokens[0])) {        // If the map does not yet contain this word...
                int[] values = new int[tokens.length-1];     // Set up a new array of appropriate size to add values to map.
                for (int i = 1; i < tokens.length; i++)
                    values[i-1] = Integer.valueOf(tokens[i]); // Dump values from "line" into the array
                map.put(tokens[0], values);
            }
        }
    }

    // Returns an Integer Array of attribute totals for a given File of text.
    public int[] scoreFile(File file) throws FileNotFoundException {
        return scoreLyrics(new Scanner(file));
    }

    // Returns an Integer Array of attribute totals for a given String of text.
    public int[] scoreString(String string){
        return scoreLyrics(new Scanner(string));
    }

    // Returns an Integer Array of attribute totals for a given text input.
    public int[] scoreLyrics(Scanner lyrics){

        int[] scores = new int[attributes.size()];
        while (lyrics.hasNext()){
            String word = lyrics.next();
            if (map.containsKey(word)){
                for (int i = 0; i < scores.length; i++){
                    scores[i] += map.get(word)[i];
                }
            }
        }
        return scores;
    }

    // Returns a List of Strings representing the attributes for the lexicon.
    public List<String> getAttributes(){
        return this.attributes;
    }

    // Prints a word followed by it's attributes & values.
    public void printValues(String key){
        if (map.keySet().contains(key)){

            System.out.print(key.toUpperCase() + ":  ");
            int[] values = map.get(key);
            for(int i = 0; i < 10; i++)
                System.out.print(attributes.get(i) + ": " + values[i] + ", ");
            System.out.println();
        } else System.out.println("Word not found");
    }
}

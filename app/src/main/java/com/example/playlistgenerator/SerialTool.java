// SerialTool
// May 21, 2019
// Paul Freeman
//
// This program is for creating a serialized object file representing
// a LyricHashMap object to be later loaded in the app

// THE FILE WE WANT TO READ SHOULD BE PLACED IN app/java

package com.example.playlistgenerator;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class SerialTool {

    public static void main(String[] args) throws FileNotFoundException {

        File lyricsFile = new File("songdata3.txt");


        LyricHashMap<String, String> lyricdatabase = readFile(lyricsFile);


        try {
            FileOutputStream fileOut = new FileOutputStream("LyricDatabase");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(lyricdatabase);
            objectOut.close();
            System.out.println("success");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static LyricHashMap<String,String> readFile(File input)throws FileNotFoundException{


        Scanner s = new Scanner(input);
        LyricHashMap lyricsDatabase = new LyricHashMap<String,String>();

        // Now Scan the file line by line and add each song to the LyricHashMap
        // We will use a String made from Artist + Title as the Key.
        s.nextLine(); //skip first line.
        int progress = 0;
        while (s.hasNextLine()){
            String singleSong = "";

            // read in lines until quotation mark for next song...
            boolean stop = false;
            while(s.hasNextLine() && !stop){
                String nextLine = s.nextLine();
                if (!nextLine.equals("\""))
                    singleSong += nextLine;
                else
                    stop = true;
            }

            // split up info
            String[] infoAndLyrics = singleSong.split("@");
            String artist = infoAndLyrics[0].replace("\"", "");
            String title = infoAndLyrics[1];
            String lyrics = infoAndLyrics[infoAndLyrics.length - 1];

            lyricsDatabase.put(artist+title,lyrics);

        }
        return lyricsDatabase;
    }

}

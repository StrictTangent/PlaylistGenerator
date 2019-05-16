// Song
// Paul Freeman April 25, 2019

// A Class to represent a Song.
// Must be constructed with Song Title, Lyrics, and a Lexicon to analyze the lyrics.

package com.example.playlistgenerator;
import java.util.*;
import java.io.*;

import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;

public class Song{

    public String title;
    public String artist;
    public String album;

    public String id;
    public AudioFeaturesTrack features;

    private List<String> attributes;
    private int[] scores;

    // Constructs a Song with artist name, song title, and Spotify id
    public Song(String artist, String title, String id){
        this(artist,title,id,null);
    }

    // Add Spotify AudioFeaturesTrack to the Song.
    public void setFeatures(AudioFeaturesTrack features){
        this.features=features;
    }

    // Constructs a Song with artist name, song title, Spotify id, and Spotify AudioFeaturesTrack
    public Song(String artist, String title, String id, AudioFeaturesTrack features){
        this.artist = artist;
        this.title = title;
        this.id = id;
        this.features = features;
    }

    // Assign a score to this song by running the lyrics through the lexicon
    public void scoreSong(Lexicon lexicon, LyricHashMap database) {
        String lyrics = (String) database.get(this.artist+this.title);
        if (lyrics != null){
            this.scores = lexicon.scoreString(lyrics);
            this.attributes = lexicon.getAttributes();
        }
    }

    // Return the emotion score for a given attribute (0-9)
    public int getAttributeScore(int index){
        return scores[index];
    }

    // Prints info about the song.
    public String toString(){
        String result = "Title: " + title;
        result += ", " + "Artist: " + artist;
        result += ", " + "Album: " + album;

        for (int i = 0; i < scores.length; i ++){
            result += ", " + attributes.get(i) + ": " + scores[i];
        }
        return result;
    }

}

package com.example.playlistgenerator;

// Song
// Paul Freeman April 25, 2019

// A Class to represent a Song.
// Must be constructed with Song Title, Lyrics, and a Lexicon to analyze the lyrics.


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

    public Song(String artist, String title, String id){
        this(artist,title,id,null);
    }

    public void setFeatures(AudioFeaturesTrack features){
        this.features=features;
    }

    public Song(String artist, String title, String id, AudioFeaturesTrack features){
        this.artist = artist;
        this.title = title;
        this.id = id;
        this.features = features;
    }

    // assign a score to this song by running the lyrics through the lexicon
    public void scoreSong(Lexicon lexicon, LyricHashMap database) {
        String lyrics = (String) database.get(this.artist+this.title);
        if (lyrics != null){
            this.scores = lexicon.scoreString(lyrics);
            this.attributes = lexicon.getAttributes();
        }
    }

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

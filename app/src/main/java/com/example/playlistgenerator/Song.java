// Song
// Paul Freeman April 25, 2019

// A Class to represent a Song.
// Must be constructed with Song Title, Lyrics, and a Lexicon to analyze the lyrics.

package com.example.playlistgenerator;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.io.*;

import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.Image;

public class Song implements AttributeComparable<Song>, Serializable{

    public String title;
    public String artist;
    public String album;

    public String id;
    public AudioFeaturesTrack features;

    public Drawable art;


    private List<String> attributes;
    private int[] scores;
    private String currentScore;

    // Constructs a Song with artist name, song title, and Spotify id
    public Song(String artist, String title, String id){
        this(artist,title,id,null);
    }

//    public static ArrayList<String> createSongList(List<Song> songsDatabase) {
//        ArrayList<Song> songs = new ArrayList<Song>();
//
//        for (int i = 0; i < songsDatabase.size(); i++) {
//            songs.add(new Contact("Person " + ++lastContactId, i <= numContacts / 2));
//        }
//
//        return contacts;
//    }



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
        currentScore = "Emotion(" + attributes.get(index) + "): " + scores[index];
        return scores[index];
    }


    // Prints info about the song.
    public String toString(){
        String result = title;
        result += " by " + artist;
        //result += ", " + "Album: " + album;
        if (currentScore != null) {
            result += "\n";
            result += currentScore;
            if (features != null) result += " - Danceability: " + features.danceability;
        }

//        for (int i = 0; i < scores.length; i ++){
//            result += ", " + attributes.get(i) + ": " + scores[i];
//        }
        return result;
    }

    public int compareTo(Song song2, int attribute){
        return song2.getAttributeScore(attribute) - this.getAttributeScore(attribute);
    }

    // Takes an Image object and uses its URL to fetch the image
    // to create a drawable object for the album art
    public void setImage(Image image) {

        try{
            InputStream input = (InputStream) new URL(image.url).getContent();
            Drawable draw = Drawable.createFromStream(input, "art");
            art = draw;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

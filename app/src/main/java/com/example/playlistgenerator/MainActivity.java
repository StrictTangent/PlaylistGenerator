// MainActivity
// May 2019 - Paul Freeman
//
// This Activity handles user interaction for the Playlist Generator Project
// Users may select attributes the wish to prioritize for generating a new Spotify Playlist
// using songs taken from a previously created "Master Playlist".

package com.example.playlistgenerator;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private final int TOTAL_LYRICS = 57650;

    // String to hold the Spotify User Name of the person logged in on the device.
    public String USER_NAME;

    // Client ID for the App on the Spotify Developer Dashboard
    public static final String CLIENT_ID = "28f7f6eba23e4919bc72a4a0f418bc92";

    // this is how your redirect URI should look like in the spotify dev dashboard
    public static final String REDIRECT_URI = "playlistgenerator://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 2019;

    private String mAccessToken;    // Our ACCESS TOKEN authorizing calls to Spotify

    public SpotifyService spotify;  // the SpotifyService for making web-api calls with the wraper
    public Playlist masterPlaylist; // the master playlist to pull tracks from

    private SpotifyAppRemote mSpotifyAppRemote; // the app-remote to play music

    private TextView welcome;   // a text view for debugging messages
    private Button createPlaylist;  // view for the button to play the playlist

    private static List<Song> songsDatabase;   // initial list of songs from Master Playlist on Spotify

    private LyricHashMap<String, String> lyricsDatabase;    // database of lyrics built from the big lyrics file

    private Lexicon lexicon; // lexicon used to score lyrcis

    private String[] userChoices; //array for recording the user's UI choices

    private static List<Song> currentPlaylist; // most recent playlist (list of songs)
    private Playlist currentSpotifyPlaylist; // most recent playlist (spotify playlist)
    private static final int MAX_PLAYLIST_SIZE = 5; // The number of tracks added to a new playlist
    private static final double DANCE_THRESHOLD = 0.5; //songs with danceability of at least this much are considered danceable

    public static List<Song> getSongsDatabase() {
        return songsDatabase;
    }

    public static List<Song> getCurrentPlaylist() {
        return currentPlaylist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_menu);

        // Initialize views/buttons
        welcome = findViewById(R.id.textViewWelcome);
        createPlaylist  = findViewById(R.id.buttonCreate);
        userChoices = new String[4];

        // Go ahead and read in the database.
        //readDataBase();
        this.lyricsDatabase = readSerial();

        //Go ahead anc construct the LEXICON
        buildLexicon();

        //Go ahead and generate an ACCESS TOKEN
        setToken();
        

        findViewById(R.id.buttonView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("success");
                if (MainActivity.getCurrentPlaylist() != null) {
                    viewCurrent();
                } else {
                    startActivity(new Intent(MainActivity.this, Pop.class));
                }
            }
        });
    }

    private void viewCurrent() {
        Intent viewCurrent = new Intent(this, Pop.class);
        startActivity(viewCurrent);
    }

    // Method for the Create New Playlist Button
    public void createNewPlaylist(View view) {
        Intent createNewPlaylist = new Intent(this, GoodBad.class);
        createNewPlaylist.putExtra("choices", userChoices);
        startActivityForResult(createNewPlaylist, 1987);
    }

    // RIGHT NOW THE ONSTART METHOD JUST SETS UP THE APP-REMOTE FOR PLAYING MUSIC
    // This is all basically just code copied from the Spotify Tutorial
    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.

        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        // Now you can start interacting with App Remote
                        //connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    // SET TOKEN
    // Initiates getting the Access Token AND makes call to get the Master Playlist
    // Requires getAuthenticationRequest() and onActivityResult()
    public void setToken(){
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, REDIRECT_URI) //getRedirectUri().toString())
                .setShowDialog(false)
                // Might as well get ALL the Scopes!!
                .setScopes(new String[]{"user-read-recently-played","user-top-read","user-library-modify","user-library-read",
                        "playlist-read-private","playlist-modify-public","playlist-modify-private","playlist-read-collaborative",
                       "user-read-email","user-read-birthdate","user-read-private","user-read-playback-state","user-modify-playback-state",
                        "user-read-currently-playing","app-remote-control","streaming","user-follow-read","user-follow-modify"})
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from when user has made choices...
        if (requestCode == 1987 && resultCode == 1987){
            userChoices = data.getStringArrayExtra("choices");
            welcome.setText(userChoices[0] + ", " + userChoices[1] + ", " + userChoices[2]);
            buildPlaylist();


        } else {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
                // if we're successfull, we can initialize the mAccessToken field.
                mAccessToken = response.getAccessToken();
                Log.d("appInfo", "Token Acquired: " + mAccessToken);

                // Set up and initialize our Spotify Service field, 'spotify'
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(mAccessToken);
                this.spotify = api.getService();

                // Set USER_NAME to be the currently logged on user
                this.spotify.getMe(new SpotifyCallback<UserPrivate>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                    }

                    @Override
                    public void success(UserPrivate userPrivate, Response response) {
                        USER_NAME = userPrivate.id;
                    }
                });

                // Go ahead and fetch the masterplaylist
                onGetPlaylist(); // THIS IS THE CODE THAT IS NORMALLY CALLED
            }
        }
    }

    // FETCHES THE MASTER PLAYLIST FROM SPOTIFY AND SETS THE masterPlaylist field.
    public void onGetPlaylist(){
        Log.d("appInfo", mAccessToken);
        if (this.mAccessToken == null) {
            Log.e("MainActivity", "Error: mAccessToken is null! - please sign in first");
            return;
        }

        //spotify.getPlaylist(USER_NAME, "5awS9K0ipGd3xRJR7dM3cy", new SpotifyCallback<Playlist>() {
        //spotify.getPlaylist("drakee", "4kuWlTFYcAMBDBxzEoGIiv", new SpotifyCallback<Playlist>() {
        spotify.getPlaylist("Quintin Sweeney", "6bf29vXaRbKZmi8EXY9drv", new SpotifyCallback<Playlist>() {

            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("appInfo", "Could not get playlist");
                return;
            }

            @Override
            public void success(Playlist playlist, Response response) {
                Log.d("appInfo", "Playlist Acquired: " + playlist.name);
                // Set the masterPlaylist field.
                masterPlaylist = playlist;
                //now get all the songs from the playlist
                getSongsFromMaster();
                return;
            }
        });
    }

    // This builds a List of all the songs in Quintin's Playlist as Song objects.
    // It also builds a String of all the song IDs as it's doing that so it can then fetch
    // all of the AudioFeaturesTracks (objects containing danceability etc..) and
    // add that information to each Song object in the list.
    public void getSongsFromMaster() {
        songsDatabase = new ArrayList<Song>();
        String result = "";
        Log.d("appInfo", String.valueOf(masterPlaylist == null));

        // ADD ALL PLAYLIST TRACKS TO A LIST OF SONG OBJECTS
        for (int i = 0; i < masterPlaylist.tracks.items.size(); i++) {
            PlaylistTrack trackToAdd = masterPlaylist.tracks.items.get(i);
            // Construct a new Song object using the track artist, name, and id
            Song songtoAdd = new Song(trackToAdd.track.artists.get(0).name, trackToAdd.track.name, trackToAdd.track.id);
            // add it to the List
            songsDatabase.add(songtoAdd);
            //build up a string of ids to use for getting all the audio features.
            result += masterPlaylist.tracks.items.get(i).track.id + ",";
        }
        //NOW ADD AUDIO FEATURES FOR ALL SONGS
        //(NOTE: I may want to rework how I do this part, and make it a separate method...)
        spotify.getTracksAudioFeatures(result, new SpotifyCallback<AudioFeaturesTracks>() {
                @Override
                public void failure(SpotifyError spotifyError) {

                }

                @Override
                public void success(AudioFeaturesTracks audioFeaturesTracks, Response response) {
                    // An AudioFeaturesTracks object has been generated. This object contains all the individual AudioFeaturesTrack objects
                    // for each song. (Notice that we created this with a String of ALL the song IDs from the playlist).
                    // So for each AudioFeaturesTrack, we add it to the corresponding Song object in the List of Songs we just created.
                    for (int i = 0; i < audioFeaturesTracks.audio_features.size(); i++){
                        songsDatabase.get(i).setFeatures(audioFeaturesTracks.audio_features.get(i));
                    }
                    // This sets the logViewText to show us some information from one of the Songs.
                    // Should not be used in final version.
                    Log.d("appInfo", songsDatabase.get(0).artist + ", " + songsDatabase.get(0).title + ", " + songsDatabase.get(0).features.danceability);
                }
            });
    }

    // Tell the AppRemote to go ahead and start playing the Master Playlist
    public void onPlayPlaylist(View view) {
        Playlist toPlay = null;
        if(view == findViewById(R.id.buttonView)) toPlay = currentSpotifyPlaylist;
        if(view == findViewById(R.id.buttonViewMaster)) toPlay = masterPlaylist;
        if(toPlay != null) mSpotifyAppRemote.getPlayerApi().play(toPlay.uri);

    }

    public void viewMaster(View view) {
        Intent viewMaster = new Intent(this, ViewMyPlaylists.class);
        startActivity(viewMaster);
    }



    // Loads lexicon file and intializes the "lexicon" field as a new lexicon.
    public void buildLexicon(){
        try {
            InputStream input = getAssets().open("lexicon_Formatted.txt");
            lexicon = new Lexicon(input);
        } catch (IOException e){

        }
    }

    // Builds a new EmotionQueue based on user's choices
    // This should be called once we have information on the user's choices
    public void buildPlaylist(){

        ArrayList<Song> songsForPlaylist = new ArrayList<Song>(); // Create a new list for the songs we will add
        List<String> attributes = lexicon.getAttributes();  // get the list of attributes in the lexicon so we can find the appropriate integer
        int attribute = attributes.indexOf(userChoices[1]); // set emotion attribute we will prioritize based on user's choice
        EmotionQueue<Song> newQueue = new EmotionQueue<Song>(attribute);

        //Go through and add the songs that score correctly of danceability
        for (Song song : songsDatabase){
            if (userChoices[2].equals("dance") && song.features.danceability >= DANCE_THRESHOLD){
                songsForPlaylist.add(song);
            } else if (userChoices[2].equals("chill") && song.features.danceability < DANCE_THRESHOLD ){
                songsForPlaylist.add(song);
            }
        }
        // For each song in the list of songs considered...
        // Score the song for the emotion and add to the EmotionQueue
        for (Song song : songsForPlaylist){
            if (lyricsDatabase.get(song.artist + song.title) != null) {
                song.scoreSong(lexicon, lyricsDatabase);
                newQueue.insert(song);
            }
        }
        currentPlaylist = new ArrayList<Song>();
        while (!newQueue.isEmpty()){
            Log.d("appInfo", newQueue.getMin().toString() + " danceability: " + newQueue.getMin().features.danceability);
            currentPlaylist.add(newQueue.remove());
        }
        //Save the playlist on user's account
        //WE DON'T NECESSARY WANT THIS TO BE CALLED YET
        SaveSpotifyPlaylist("Generated: " + userChoices[1] + " + " + userChoices[2], currentPlaylist);
    }

    // Creates a new empty playlist on user's Spotify account
    public void SaveSpotifyPlaylist(String playlistName, final List<Song> songList){
        Map<String, Object> options = new HashMap<>();
        options.put("name", playlistName);

        spotify.createPlaylist(USER_NAME, options ,new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("appInfo","Well that didn't work...");
            }

            @Override
            public void success(Playlist playlist, Response response) {
                Log.d("appInfo","Success?");
                currentSpotifyPlaylist = playlist;
                addToPlayList(songList, playlist);
            }
        });
    }

    // Add songs from most recently created playlist to the empty playlist on user's spotify account.
    public void addToPlayList(List<Song> songList, Playlist playlist) {

        if (mAccessToken == null) {
            Log.e("MainActivity", "Error: mAccessToken is null - please sign in first");
            return;
        }

        // pull the uri's from the list of songs to add
        String uris = "";
        uris += "spotify:track:" + songList.get(0).id;
        int size = Math.min(songList.size(), MAX_PLAYLIST_SIZE);
        for (int j = 1; j < size; j++){
            uris += ",spotify:track:"+ songList.get(j).id;
        }

        // First map field containing Track Uris to be added..
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("uris", uris);

        // Seems the second map field in addTracksToPlayList can't just be null... so we create this.
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("uris", new JSONArray());

        spotify.addTracksToPlaylist(USER_NAME, playlist.id, map1, map2, new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Log.d("appInfo","Could not add tracks...");
            }

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                Log.d("appInfo","Added the tracks...");
            }
        });
    }


    // Reads a file of a serialized LyricHashMap
    // Deserializes the file and returns a LyricHashMap
    public LyricHashMap<String, String> readSerial(){
        LyricHashMap<String, String> temp = null;
        try {
            Log.d("appInfo", "before getasset");
            InputStream input = getApplicationContext().getAssets().open("LyricDatabase");

            Log.d("appInfo", "before objectinputstream");
            ObjectInputStream objectIn = new ObjectInputStream(input);

            Log.d("appInfo", "before readobject");
            temp = (LyricHashMap) objectIn.readObject();

            Log.d("appInfo", temp.get("ABBA"+ "Andante, Andante"));
            objectIn.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("appInfo", "readobject failed");
        }
        return temp;
    }




    // Loads the big lyrics file and stores it in a LyricHashMap field called "lyrics database"
    // CURRENTLY UNUSED SINCE SERIALIZATION OF DATABASE
    private void readDataBase()  {
        try {
            // Create an InputStream with the lyrics file and construct a new LyricHashMap
            InputStream input = getAssets().open("songdata3.txt");
            Scanner s = new Scanner(input);
            lyricsDatabase = new LyricHashMap<String,String>();

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
                progress++;
            }

        } catch (IOException e){

        }
    }


}

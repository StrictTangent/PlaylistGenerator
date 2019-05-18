// MainActivity
// May 2019 - Paul Freeman
//
// This Activity handles user interaction for the Playlist Generator Project
// Users may select attributes the wish to prioritize for generating a new Spotify Playlist
// using songs taken from a previously created "Master Playlist".

package com.example.playlistgenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    public static final String CLIENT_ID = "544dc767482a47deb564342ed8b710a1";

    // this is how your redirect URI should look like in the spotify dev dashboard
    public static final String REDIRECT_URI = "playlistgenerator://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 2019;

    private String mAccessToken;    // Our ACCESS TOKEN authorizing calls to Spotify

    public SpotifyService spotify;  // the SpotifyService for making web-api calls with the wraper
    public Playlist masterPlaylist; // the master playlist to pull tracks from

    private SpotifyAppRemote mSpotifyAppRemote; // the app-remote to play music

    private TextView logViewText;   // a text view for debugging messages
    private Button playlistButton;  // view for the button to play the playlist
    private Button bottomButton;    // view for the bottom button

    private List<Song> songsDatabase;   // initial list of songs from Master Playlist on Spotify

    private LyricHashMap<String, String> lyricsDatabase;    // database of lyrics built from the big lyrics file

    private Lexicon lexicon; // lexicon used to score lyrcis

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views/buttons
        logViewText = findViewById(R.id.logView);
        playlistButton = findViewById(R.id.playPlayList);
        playlistButton.setVisibility(View.INVISIBLE);
        bottomButton = findViewById(R.id.bottomButton);
        bottomButton.setVisibility(View.INVISIBLE);
        USER_NAME = "drakeee.";
        logViewText.setText("Fetching Playlist...");

        //Go ahead anc construct the LEXICON
        buildLexicon();

        //Go ahead and generate an ACCESS TOKEN
        setToken();
    }

    // Loads the big lyrics file and stores it in a LyricHashMap field called "lyrics database"
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
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            // if we're successfull, we can initialize the mAccessToken field.
            mAccessToken = response.getAccessToken();
            logViewText.setText("Token Acquired: " + mAccessToken);


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
            //onGetUserSongs();
        }
    }

    // FETCHES THE MASTER PLAYLIST FROM SPOTIFY AND SETS THE masterPlaylist field.
    public void onGetPlaylist(){
        logViewText.setText(mAccessToken);
        if (this.mAccessToken == null) {
            Log.e("MainActivity", "Error: mAccessToken is null! - please sign in first");
            return;
        }

        //spotify.getPlaylist(USER_NAME, "5awS9K0ipGd3xRJR7dM3cy", new SpotifyCallback<Playlist>() {
        //spotify.getPlaylist("drakee", "4kuWlTFYcAMBDBxzEoGIiv", new SpotifyCallback<Playlist>() {
        spotify.getPlaylist("Quintin Sweeney", "6bf29vXaRbKZmi8EXY9drv", new SpotifyCallback<Playlist>() {

            @Override
            public void failure(SpotifyError spotifyError) {
                logViewText.setText("Could not get playlist");
                return;
            }

            @Override
            public void success(Playlist playlist, Response response) {
                logViewText.setText("Playlist Acquired: " + playlist.name);
                // Set the masterPlaylist field.
                masterPlaylist = playlist;
                return;
            }
        });
    }

    // THIS CODE RUNS WHEN YOU HIT THE TOP BUTTON
    // This builds a List of all the songs in Quintin's Playlist as Song objects.
    // It also builds a String of all the song IDs as it's doing that so it can then fetch
    // all of the AudioFeaturesTracks (objects containing danceability etc..) and
    // add that information to each Song object in the list.
    public void onTestButton(View view) {
        songsDatabase = new ArrayList<Song>();
        String result = "";
        logViewText.setText(String.valueOf(masterPlaylist == null));
        logViewText.setText("");

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
                    logViewText.setText(songsDatabase.get(0).artist + ", " + songsDatabase.get(0).title + ", " + songsDatabase.get(0).features.danceability);
                }
            });

        // Changes visibility of buttons.
        // Should not necessarily do this in final version...
        playlistButton.setVisibility(View.VISIBLE);
        bottomButton.setVisibility(View.VISIBLE);
    }

    // THIS CODE RUNS WHEN YOU HIT THE BUTTON TO PLAY THE PLAYLIST
    // Tell sthe AppRemote to go ahead and start playing Quintin's Playlist
    public void onPlayPlaylist(View view) {
        mSpotifyAppRemote.getPlayerApi().play(masterPlaylist.uri);
    }

    // Currently this method iterates over all the tracks in the first 50 playlists of the user
    // CURRENTLY THIS METHOD IS NOT USED
    public void onGetUserSongs(){
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, 0);
        options.put(SpotifyService.LIMIT, 50);

        // Grab a list of the first 50 playlists
        spotify.getMyPlaylists(options, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {

            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                //String result;
                //iterate over list of playlists
                for(int i = 0; i < playlistSimplePager.items.size(); i++) {
                    String pListId = playlistSimplePager.items.get(i).id;

                    // Get the current playlist in the list
                    spotify.getPlaylist(USER_NAME, pListId, new SpotifyCallback<Playlist>() {
                        @Override
                        public void failure(SpotifyError spotifyError) {

                        }

                        @Override
                        public void success(Playlist playlist, Response response) {
                            // iterate over the tracks in the playlist
                            final Playlist pList = playlist;
                            String result = "";
                            for (int j = 0; j < pList.tracks.items.size(); j++) {
                                String tName = pList.tracks.items.get(j).track.name;
                                result += tName;
                                logViewText.setText(result);
                            }
                        }
                    });
                }
            }
        });
    }

    // Loads lexicon file and intializes the "lexicon" field ans a new lexicon.
    public void buildLexicon(){
        try {
            InputStream input = getAssets().open("lexicon_Formatted.txt");
            lexicon = new Lexicon(input);
        } catch (IOException e){

        }
    }

    // calls the readDataBase() method to load in the big lyrics file.
    public void loadLyrics(View view) {
            readDataBase();
            // Prints the lyrics from a song to show that it worked
            // should not be in final version
            logViewText.setText(lyricsDatabase.get("Gary Numan"+"The Tick Tock Man"));
    }
}

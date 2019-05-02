package com.example.playlistgenerator;

import android.content.Intent;
import android.net.Uri;
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

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    // change this to be your own username.
    public String USER_NAME;

    // change this and add your client ID here
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logViewText = findViewById(R.id.logView);
        playlistButton = findViewById(R.id.playPlayList);
        playlistButton.setVisibility(View.INVISIBLE);
        USER_NAME = "ducttape_87";
        logViewText.setText("Fetching Playlist...");


        //Go ahead and generate an ACCESS TOKEN
        setToken();


        // So, yeah, this is a stupid place to try and put some flow code here... Should build a little main method or something

    }

    // RIGHT NOW THE ONSTART METHOD JUST SETS UP THE APP-REMOTE
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



    // Initiates getting the Access Token. Requires getAuthenticationRequest(), getRedirectUri(), and onActivityResult()
    public void setToken(){
        final AuthenticationRequest request = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, REDIRECT_URI) //getRedirectUri().toString())
                .setShowDialog(false)
                //.setScopes(new String[]{"playlist-modify-public"})
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
            mAccessToken = response.getAccessToken();
            logViewText.setText("Token Acquired: " + mAccessToken);


            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(mAccessToken);
            this.spotify = api.getService();
            logViewText.setText("Nuffin");

            // Go ahead and fetch the masterplaylist
            onGetPlaylist();


        }
    }

    // FETCHES THE PLAYLIST
    public void onGetPlaylist(){

        logViewText.setText("Nuffin");
        logViewText.setText(mAccessToken);
        if (this.mAccessToken == null) {
            Log.e("MainActivity", "Error: mAccessToken is null! - please sign in first");
            return;
        }

        //spotify.getPlaylist(USER_NAME, "5awS9K0ipGd3xRJR7dM3cy", new SpotifyCallback<Playlist>() {
        spotify.getPlaylist("drakee", "4kuWlTFYcAMBDBxzEoGIiv", new SpotifyCallback<Playlist>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                logViewText.setText("Could not get playlist");
                return;
            }

            @Override
            public void success(Playlist playlist, Response response) {
                logViewText.setText("Playlist Acquired: " + playlist.name);
                masterPlaylist = playlist;
                return;
            }

        });

    }

    // THIS CODE RUNS WHEN YOU HIT THE TOP BUTTON
    public void onTestButton(View view) {
        String result = "";
        logViewText.setText(String.valueOf(masterPlaylist == null));

        for (int i = 0; i < masterPlaylist.tracks.items.size(); i++){
            result += masterPlaylist.tracks.items.get(i).track.name + "/n";
        }
        playlistButton.setVisibility(View.VISIBLE);
        logViewText.setText(result);



    }

    // THIS CODE RUNS WHEN YOU HIT THE BUTTON TO PLAY THE PLAYLIST
    public void onPlayPlaylist(View view) {
        // Then we will write some more code here.
        mSpotifyAppRemote.getPlayerApi().play(masterPlaylist.uri);
    }
}

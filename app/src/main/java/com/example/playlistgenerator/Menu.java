package com.example.playlistgenerator;

import androidx.appcompat.app.AppCompatActivity;

//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
 import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends AppCompatActivity {

    private TextView welcome;   // a text view for debugging messages
    private Button createPlaylist;  // view for the button to play the playlist
    private Button viewMaster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        // Initialize views/buttons
        welcome = findViewById(R.id.textViewWelcome);
        createPlaylist  = findViewById(R.id.buttonCreate);
        viewMaster = findViewById(R.id.buttonViewMaster);

    }

    public void createNewPlaylist(View view) {
        Intent createNewPlaylist = new Intent(this, GoodBad.class);
        startActivity(createNewPlaylist);
    }

    public void viewMaster(View view) {
        Intent viewMaster = new Intent(this, ViewMyPlaylists.class);
        startActivity(viewMaster);
    }
}

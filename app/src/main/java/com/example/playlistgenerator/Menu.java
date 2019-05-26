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
    private Button viewCurrent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        // Initialize views/buttons
        welcome = findViewById(R.id.textViewWelcome);
        createPlaylist  = findViewById(R.id.buttonCreate);
        viewMaster = findViewById(R.id.buttonViewMaster);
        viewCurrent = findViewById(R.id.buttonView);

        viewCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("success");
               if (MainActivity.getCurrentPlaylist() == null) {
                    startActivity(new Intent(Menu.this, Pop.class));
                } else {
                    viewCurrent();
                }
            }
        });
    }

        public void createNewPlaylist(View view) {
                Intent createNewPlaylist = new Intent(Menu.this, GoodBad.class);
                startActivity(createNewPlaylist);
        }

        public void viewMaster(View view){
            Intent viewMaster = new Intent(Menu.this, ViewMyPlaylists.class);
            startActivity(viewMaster);
        }

        public void viewCurrent(){
            Intent viewCurrent = new Intent(Menu.this, Pop.class);
            startActivity(viewCurrent);
        }


}





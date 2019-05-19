package com.example.playlistgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GoodBad extends AppCompatActivity {
    private TextView goodBad;   // a text view for debugging messages
    private Button good;  // view for the button to play the playlist
    private Button bad;  // view for the button to play the playlist


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_good_bad);
        // Initialize views/buttons
        goodBad = findViewById(R.id.textViewQuestion);
          good = findViewById(R.id.buttonGood);
          bad = findViewById(R.id.buttonBad);

    }

    public void onGoodClick(View view) {
        Intent onGoodClick = new Intent(this, GoodKeywords.class);
        startActivity(onGoodClick);
    }

    public void onBadClick(View view) {
        Intent onBadClick = new Intent(this, BadKeywords.class);
        startActivity(onBadClick);
    }

//    public void onBadClick(View view) {
//        Intent onBadClick = new Intent(this, GoodKeywords.class);
//        startActivity(onBadClick);
//    }

}

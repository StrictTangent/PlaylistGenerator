package com.example.playlistgenerator;

import androidx.annotation.Nullable;
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

    private String[] userChoices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_good_bad);
        // Initialize views/buttons
        goodBad = findViewById(R.id.textViewQuestion);
          good = findViewById(R.id.buttonGood);
          bad = findViewById(R.id.buttonBad);

          userChoices = getIntent().getExtras().getStringArray("choices");

    }

    public void onGoodClick(View view) {
        userChoices[0] = "good";
        Intent onGoodClick = new Intent(this, GoodKeywords.class);
        onGoodClick.putExtra("choices", userChoices);
        startActivityForResult(onGoodClick, 1987);
    }

    public void onBadClick(View view) {
        userChoices[0] = "bad";
        Intent onBadClick = new Intent(this, BadKeywords.class);
        onBadClick.putExtra("choices", userChoices);
        //startActivity(onBadClick);
        startActivityForResult(onBadClick, 1987);
    }

//    public void onBadClick(View view) {
//        Intent onBadClick = new Intent(this, GoodKeywords.class);
//        startActivity(onBadClick);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1987 && resultCode == 1987){
            setResult(1987, data);
            //goodBad.setText("it worked: " + resultCode);
            finish();
        }
    }
}

package com.example.playlistgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BadKeywords extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad_keywords);
    }

    public void onClick(View view) {
        Intent onClick = new Intent(this, MyPlaylists.class);
        startActivity(onClick);
    }
}

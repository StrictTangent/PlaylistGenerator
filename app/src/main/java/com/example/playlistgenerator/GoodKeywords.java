package com.example.playlistgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GoodKeywords extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_keywords);
    }

    public void onClick(View view) {
        Intent onClick = new Intent(this, MyPlaylists.class);
        startActivity(onClick);
    }
}

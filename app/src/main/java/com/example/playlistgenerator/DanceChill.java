package com.example.playlistgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DanceChill extends AppCompatActivity {

    private String[] userChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance_chill);
        userChoices = getIntent().getStringArrayExtra("choices");
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        if (view == findViewById(R.id.buttonDance)) userChoices[2] = "dance";
        if (view == findViewById(R.id.buttonChill)) userChoices[2] = "chill";
        intent.putExtra("choices", userChoices);
        setResult(1987, intent);
        finish();
    }
}

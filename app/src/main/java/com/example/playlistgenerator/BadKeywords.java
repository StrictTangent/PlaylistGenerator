package com.example.playlistgenerator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BadKeywords extends AppCompatActivity {

    private String[] userChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad_keywords);
        userChoices = getIntent().getStringArrayExtra("choices");
    }

    public void onClick(View view) {
        Intent onClick = new Intent(this, DanceChill.class);
        if (view == findViewById(R.id.buttonAnger)) userChoices[1] = "anger";
        if (view == findViewById(R.id.buttonDisgust)) userChoices[1] = "disgust";
        if (view == findViewById(R.id.buttonFear)) userChoices[1] = "fear";
        if (view == findViewById(R.id.buttonSadness)) userChoices[1]= "sadness";

        onClick.putExtra("choices", userChoices);

        startActivityForResult(onClick, 1987);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1987 && resultCode == 1987) {
            setResult(1987, data);
            finish();
        }
    }
}

package com.example.playlistgenerator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GoodKeywords extends AppCompatActivity {

    private String[] userChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_keywords);
        userChoices = getIntent().getStringArrayExtra("choices");
    }

    public void onClick(View view) {
        Intent onClick = new Intent(this, DanceChill.class);
        if (view == findViewById(R.id.buttonJoy)) userChoices[1] = "joy";
        if (view == findViewById(R.id.buttonTrust)) userChoices[1] = "trust";
        if (view == findViewById(R.id.buttonSurprise)) userChoices[1] = "surprise";

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

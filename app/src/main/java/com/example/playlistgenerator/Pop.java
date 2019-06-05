// Anna T
// package com.example.playlistgenerator;

package com.example.playlistgenerator;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;




public class Pop extends AppCompatActivity  {

    private Button buttonOkay;
    private View textViewNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup);

        buttonOkay = findViewById(R.id.buttonOk);
        textViewNull = findViewById(R.id.textViewNull);

        textViewNull.setVisibility(View.VISIBLE);

        buttonOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //startActivity(new Intent(Pop.this, Menu.class));
                finish();

            }
        });

        textViewNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        textViewNull.setVisibility(View.VISIBLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int) (height*.6));
    }

//    public void setButtonOkay(View view) {
//        Intent setButtonOkay = new Intent(Pop.this, Menu.class);
//        startActivity(setButtonOkay);
//    }
}

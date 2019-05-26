package com.example.playlistgenerator;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

    public class ViewMyPlaylists2 extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

        MyRecyclerViewAdapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_my_playlists);

            // data to populate the RecyclerView with
            List<Song> songList = MainActivity.getCurrentPlaylist();


            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MyRecyclerViewAdapter(this, songList);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        }
    }



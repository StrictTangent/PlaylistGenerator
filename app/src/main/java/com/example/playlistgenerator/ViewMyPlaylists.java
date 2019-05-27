package com.example.playlistgenerator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class ViewMyPlaylists extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private boolean paused;

    MyRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_playlists);

        paused = true;

        TextView playlistTitle = findViewById(R.id.PlayListTitle);
        playlistTitle.setText("Master Playlist");

        // data to populate the RecyclerView with
        List<Song> songList = MainActivity.getSongsDatabase();


        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, songList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    // Plays or pauses a track when you click it.
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        final String id = adapter.getItem(position).id;
        paused = true;
        Button button = findViewById(R.id.playSpotify);
        button.setText("PLAY");
        MainActivity.playURI("spotify:track:" + id);



    }

    // Plays or pauses the Master Playlist
    public void playSpotify(View view) {

        paused = !paused;
        Button button = (Button) view;
        if (!paused) {
            MainActivity.playMaster();
            button.setText("PAUSE");
        } else {
            MainActivity.mSpotifyAppRemote.getPlayerApi().pause();
            button.setText("PLAY");
        }

    }
}

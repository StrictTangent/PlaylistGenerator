package com.example.playlistgenerator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

    public class ViewMyPlaylists2 extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

        MyRecyclerViewAdapter adapter;

        private boolean paused;
        private boolean created;
        private String title;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_my_playlists);

            title = "Generated: " + MainActivity.userChoices[1] + " + " + MainActivity.userChoices[2];
            paused = true;
            created = MainActivity.createdPlaylistNames.contains(title);

            Button button = findViewById(R.id.playSpotify);
            if (!created) button.setText("SAVE");
            TextView playlistTitle = findViewById(R.id.PlayListTitle);
            playlistTitle.setText(title);

            // data to populate the RecyclerView with
            List<Song> songList = MainActivity.getCurrentPlaylist();


            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MyRecyclerViewAdapter(this, songList);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);


        }

        // Creates Playlist on User account or Plays/Pauses if created.
        public void playSpotify(View view) {

            Button button = (Button) view;
            if (!created) {
                created = true;
                button.setText("PLAY");

                MainActivity.SaveSpotifyPlaylist(title, MainActivity.getCurrentPlaylist());
                Toast.makeText(this, "Playlist \"" + title + "\" saved to Library", Toast.LENGTH_LONG).show();
            } else {
                paused = !paused;
                if (!paused) {
                    MainActivity.playCurrent();
                    button.setText("PAUSE");
                } else {
                    MainActivity.mSpotifyAppRemote.getPlayerApi().pause();
                    button.setText("PLAY");
                }
            }
        }

        // Plays or pauses a track when you click it.
        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
            String id = adapter.getItem(position).id;
            paused = true;
            Button button = findViewById(R.id.playSpotify);
            if (created) button.setText("PLAY");
            MainActivity.playURI("spotify:track:" + id);
        }
    }



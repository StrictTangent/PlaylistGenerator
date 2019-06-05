# PlaylistGenerator
Generates Spotify Playlists by analyzing the emotion/sentiment of song lyrics.

TO USE:

Clone this repository with Android Studio: https://developer.android.com/studio

Create a new Client ID on your Spotify Developer Dashboard: https://developer.spotify.com/dashboard/login

On the page for your app on your Spotify Developer Dashboard, go to EDIT SETTINGS.
Set the Redirect URI to: playlistgenerator://callback
Add an Android Package with your package name (com.example.playlistgenerator)
AND SHA1 Fingerprint (can be obtained running the signingReport in Android Studio: Click on Gradle (top right) > app > Tasks > android > signingReport

In MainActivity.java you must change the field: public static final String CLIENT_ID = "544dc767482a47deb564342ed8b710a1";
to be the client ID in your own Spotify Developer Dashboard.



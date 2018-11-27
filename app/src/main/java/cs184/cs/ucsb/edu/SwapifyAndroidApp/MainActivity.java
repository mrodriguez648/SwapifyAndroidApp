package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID ="49561555a6fd4897912fddebb7bf7da8";
    private static final String REDIRECT_URI = "testSpotify://callback";
    private static ArrayList<PlaylistSimple> userPlaylists;
    private static  ArrayList<PlaylistTrack> playlistSongs;
    SpotifyApi api;
    static SpotifyService  spotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = new SpotifyApi();

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        userPlaylists = new ArrayList<>();
        playlistSongs = new ArrayList<>();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Log.d("user","success");
                    api.setAccessToken(response.getAccessToken());
                    spotify = api.getService();
                    //set welcome
                    spotify.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate userPrivate, Response response) {
                            TextView welcome = findViewById(R.id.Welcome);
                            welcome.setText("WELCOME TO SWAPIFY " +userPrivate.display_name);

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                    //activate button
                    Button launchPlaylistViewer = findViewById(R.id.button);
                    getPlaylists();
                    launchPlaylistViewer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //LAUNCH PLAYLIST RECYCLERVIEW ACTIVITY HERE
                            //PASS THROUGH THE ARRAYLIST PLAYLIST VARAIBLE HERE
                            Log.d("user",Integer.toString(userPlaylists.size()));

                        }
                    });
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("user",response.getError());

                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Log.d("user","error");
            }
        }

    }

    public void getPlaylists(){
        spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                userPlaylists.addAll(playlistSimplePager.items);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    //Call playlist tracks and "Wait" for playlistSongs variable to be populated before using
    //Call playlistSongs static variable from other classes by calling MainActivity.playlistSongs

    public static ArrayList<PlaylistTrack> getPlaylistTracks(String userId, String playlistId){
        if(playlistSongs.size() > 0){
            playlistSongs.clear();
        }

        spotify.getPlaylistTracks(userId, playlistId, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                playlistSongs.addAll(playlistTrackPager.items);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("user", "fdfs");
            }
        });
        ArrayList<PlaylistTrack> tracks = new ArrayList<>(playlistSongs);
        return tracks;
    }
}

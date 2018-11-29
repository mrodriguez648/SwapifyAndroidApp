package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.AEADBadTagException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "49561555a6fd4897912fddebb7bf7da8";
    private static final String REDIRECT_URI = "testspotify://callback";
    private static ArrayList<PlaylistSimple> userPlaylists;
    private static ArrayList<PlaylistTrack> playlistSongs;
    private static String artistString;
    SpotifyApi api;
    static SpotifyService spotify;

    //Recycler View Variables: Playlists
    private ArrayList<String> mPlaylistName = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private static HashMap<String,String> PlaylistToArtists = new HashMap<>();


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
                    Log.d("user", "success");
                    api.setAccessToken(response.getAccessToken());
                    spotify = api.getService();
                    //set welcome
                    spotify.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate userPrivate, Response response) {
                            TextView welcome = findViewById(R.id.Welcome);
                            welcome.setText("WELCOME TO SWAPIFY " + userPrivate.display_name);

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

                            Log.d("user", Integer.toString(userPlaylists.size()));
                            setContentView(R.layout.playlist_layout);
                            initImageBitmaps(userPlaylists, playlistSongs);

                        }
                    });
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("user", response.getError());

                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Log.d("user", "error");
            }
        }

    }

    public void getPlaylists() {
        spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                userPlaylists.addAll(playlistSimplePager.items);
                for(PlaylistSimple simple: userPlaylists){
                    getArtistsFromPlaylist(simple.owner.id,simple.id);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


    public void getArtistsFromPlaylist(String userId, final String playlistId) {
        spotify.getPlaylist(userId, playlistId, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {

                List<PlaylistTrack> tracks = playlist.tracks.items;
                String artists = "";
                int count = 0;
                ArrayList<String> repeats = new ArrayList<>();
                for(PlaylistTrack track : tracks){
                    if(count < 3) {
                        if(!repeats.contains(track.track.artists.get(0).name)){
                            repeats.add(track.track.artists.get(0).name);
                            artists += track.track.artists.get(0).name + " , ";
                            count++;
                        }
                    }else{
                        break;
                    }
                }
                PlaylistToArtists.put(playlistId,artists);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    //Call playlist tracks and "Wait" for playlistSongs variable to be populated before using
    //Call playlistSongs static variable from other classes by calling MainActivity.playlistSongs

    public static ArrayList<PlaylistTrack> getPlaylistTracks(String userId, String playlistId) {
        if (playlistSongs.size() > 0) {
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

    private void initImageBitmaps(ArrayList<PlaylistSimple> userPlaylist, ArrayList<PlaylistTrack> playlistSongs) {
        Log.d("user", Integer.toString(userPlaylist.size()));
        for (int i = 0; i < userPlaylist.size() ; i++) {
            if (userPlaylist.get(i).images.size() > 0){
                mImageUrls.add(userPlaylist.get(i).images.get(0).url);
            }else{
                mImageUrls.add(null);
            }
            mPlaylistName.add(userPlaylist.get(i).name);
            //THIS IS THE CORRECT LINE THAT SHOULD BE USED TO POPULATE ARTIST NAMES
            mNames.add(PlaylistToArtists.get(userPlaylist.get(i).id));
        }


        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mPlaylistName, mNames, mImageUrls, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



/*
    public  static Track swapifySong(final Track original){
        String albumId = original.album.id;
        //check if album has another song available
        final ArrayList<Track> result = new ArrayList<>();
        spotify.getAlbumTracks(albumId, new Callback<Pager<Track>>() {
            @Override
            public void success(Pager<Track> trackPager, Response response) {
                List<Track> tracks = trackPager.items;
                for(Track track : tracks){
                    if(track.id != original.id){
                        result.add(track);                   }
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        if(result.size() > 0){
            return result.get(0);
        }
        else{
            //find another song from the same artist

        }
    }
    */

}


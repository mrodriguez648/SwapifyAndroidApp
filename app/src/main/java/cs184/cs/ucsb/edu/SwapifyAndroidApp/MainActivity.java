package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements
        WelcomeFragment.OnWelcomeFragmentInteractionListener,
        PlaylistFragment.OnPlaylistFragmentInteractionListener,
        TracksFragment.OnTrackFragmentInteractionListener {
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "49561555a6fd4897912fddebb7bf7da8";
    private static final String REDIRECT_URI = "testspotify://callback";
    public static final int MAX_ALBUM_DIMENSIONS = 200;

    private SpotifyApi api;
    public static SpotifyService spotify;

    public static ArrayList<PlaylistSimple> userPlaylists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = new SpotifyApi();
        userPlaylists = new ArrayList<>();

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
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
                    // Populate our user playlist data
                    getPlaylists();
                    // Display welcome fragment with given username
                    spotify.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate userPrivate, Response response) {
                            WelcomeFragment welcomeFrag = WelcomeFragment.newInstance(userPrivate.display_name);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.fragment_container, welcomeFrag)
                                    .commit();
                        }
                        @Override
                        public void failure(RetrofitError error) {
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
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

    }

    public void initPlaylistFragment() {
        Log.d("methodCall", "initPlaylistFragment");
        PlaylistFragment playlistFrag = PlaylistFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, playlistFrag)
                .commit();
    }

    public void initTrackFragment(final PlaylistSimple playlist) {
        Log.d("methodCall", "initTrackFragment");
        spotify.getPlaylistTracks(playlist.owner.id, playlist.id, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                ArrayList<PlaylistTrack> playlistTracks = new ArrayList<>(playlistTrackPager.items);
                TracksFragment tracksFrag = TracksFragment.newInstance(playlistTracks);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, tracksFrag)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void swapifySong(final Track track) {}

//    public void getArtistsFromPlaylist(String userId, final String playlistId) {
//        spotify.getPlaylist(userId, playlistId, new Callback<Playlist>() {
//            @Override
//            public void success(Playlist playlist, Response response) {
//
//                List<PlaylistTrack> tracks = playlist.tracks.items;
//                String artists = "";
//                int count = 0;
//                ArrayList<String> repeats = new ArrayList<>();
//                for(PlaylistTrack track : tracks){
//                    if(count < 3) {
//                        if(!repeats.contains(track.track.artists.get(0).name)){
//                            repeats.add(track.track.artists.get(0).name);
//                            artists += track.track.artists.get(0).name + " , ";
//                            count++;
//                        }
//                    }else{
//                        break;
//                    }
//                }
//                PlaylistToArtists.put(playlistId,artists);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//            }
//        });
//    }

//    public  static Track swapifySong(final Track original){
//        String albumId = original.album.id;
//        //check if album has another song available
//        final ArrayList<Track> result = new ArrayList<>();
//        spotify.getAlbumTracks(albumId, new Callback<Pager<Track>>() {
//            @Override
//            public void success(Pager<Track> trackPager, Response response) {
//                List<Track> tracks = trackPager.items;
//                for(Track track : tracks){
//                    if(track.id != original.id){
//                        result.add(track);                   }
//                }
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
//        if(result.size() > 0){
//            return result.get(0);
//        }
//        else{
//            //find another song from the same artist
//
//        }
//    }
}


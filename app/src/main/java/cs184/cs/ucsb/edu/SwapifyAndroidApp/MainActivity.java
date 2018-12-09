package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.signature.ObjectKey;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.UserPrivate;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class MainActivity extends AppCompatActivity implements
        WelcomeFragment.OnWelcomeFragmentInteractionListener,
        PlaylistFragment.OnPlaylistFragmentInteractionListener,
        TracksFragment.OnTracksFragmentInteractionListener {
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "49561555a6fd4897912fddebb7bf7da8";
    private static final String REDIRECT_URI = "testspotify://callback";
    public static final int MAX_ALBUM_DIMENSIONS = 200;
    private SpotifyApi api;
    public static SpotifyService spotify;
    public static ArrayList<PlaylistSimple> userPlaylists;
    public static String userid;
    public static  HashMap<String,String> swappedSongs = new HashMap<>();


    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = new SpotifyApi();
        fragmentManager = getSupportFragmentManager();
        userPlaylists = new ArrayList<>();

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming,playlist-modify-public," +
                "playlist-read-private,playlist-read-collaborative"});
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
                            userid = userPrivate.id;
                            WelcomeFragment welcomeFrag = WelcomeFragment.newInstance(userPrivate.display_name);
                            fragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(0,
                                            android.R.anim.slide_out_right,
                                            android.R.anim.slide_in_left,
                                            android.R.anim.slide_out_right)
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
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, playlistFrag)
                .addToBackStack(null)
                .commit();
    }

    public void initTrackFragment(final PlaylistSimple playlist) {
        Log.d("methodCall", "initTrackFragment");
        spotify.getPlaylistTracks(playlist.owner.id, playlist.id, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                ArrayList<PlaylistTrack> playlistTracks = new ArrayList<>(playlistTrackPager.items);
                final TracksFragment tracksFrag = TracksFragment.newInstance(playlistTracks, playlist.name, playlist.id);
                Log.d("recyclerView", playlist.name);
                //tracksFrag.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right,
                                android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, tracksFrag)
                        .addToBackStack(null)
                        .commit();

            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }


    public static void CreatePlaylist(String name, String description, final String playlistId) {
        Map<String, Object> optionMap = new HashMap<>();
        optionMap.put("name", name);
        optionMap.put("description", description);
        optionMap.put("public", true);

        if (swappedSongs.size() > 0) {
            Log.d("recyclerView", "Has some songs");

        } else {
            Log.d("recyclerView", "Has no songs");

        }
        spotify.createPlaylist(userid, optionMap, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                //add to playlist
                Log.d("recyclerView", "Created Playlist");
                Map<String,Object> queryParameters = new HashMap<>();
                StringBuilder listOfSongUris = new StringBuilder();

                int i = 0;
                for(String key: swappedSongs.keySet()){
                    if(i < swappedSongs.size() - 1 ){
                        listOfSongUris.append(swappedSongs.get(key));
                        listOfSongUris.append(",");
                    }
                    else{
                        listOfSongUris.append(swappedSongs.get(key));
                    }
                    i++;
                }
                Log.d("recyclerView", listOfSongUris.toString());
                queryParameters.put("uris",listOfSongUris.toString());
                Map<String,Object> body = new HashMap<>();
                spotify.addTracksToPlaylist(playlist.owner.id, playlist.id, queryParameters, body, new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        Log.d("recyclerView","Add swaped songs worked");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("recyclerView","Add swaped songs didnt work");
                        Log.d("recyclerView",error.getMessage());

                    }
                });

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("recyclerView", error.getMessage());
                Log.d("recyclerView", userid);
            }
        });
    }

    public static void getSwappedTrackUris(final String originalPlaylistId, final String name,final  String description) {
        if(swappedSongs.size() > 0){
            swappedSongs.clear();
        }
        //CHANGE USERID TO ACCEPT THE PLAYLIST OWNER ID
        spotify.getPlaylistTracks(userid, originalPlaylistId, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(final Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    final ArrayList<String> ids = new ArrayList<>();
                    for(PlaylistTrack t : playlistTrackPager.items ) {
                        ids.add(t.track.id);
                        spotify.getArtistTopTrack(t.track.artists.get(0).id, "US", new Callback<Tracks>() {
                            boolean addone =  false;
                            @Override
                            public void success(Tracks artistTopTracks, Response response) {
                                for(Track temp: artistTopTracks.tracks){
                                    if(!swappedSongs.containsKey(temp.id) && !ids.contains(temp.id) &&
                                            swappedSongs.size() < playlistTrackPager.items.size() && !addone) {
                                        swappedSongs.put(temp.id,temp.uri);
                                        addone = true;
                                    }
                                }
                            }
                            @Override
                            public void failure(RetrofitError error) {
                                Log.d("recyclerView", "Add getArtistTopTrack didnt work");

                            }
                        });
                    }
                CreatePlaylist(name, description, originalPlaylistId);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d("recyclerView", "Add getSwappedTrackUris didnt work");
            }
        });
    }

}

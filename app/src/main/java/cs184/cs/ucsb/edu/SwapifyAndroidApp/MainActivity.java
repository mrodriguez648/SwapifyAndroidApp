package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements
        WelcomeFragment.OnWelcomeFragmentInteractionListener,
        PlaylistFragment.OnPlaylistFragmentInteractionListener,
        TracksFragment.OnTracksFragmentInteractionListener {
    private static final String TAG = "MAIN_ACTIVITY";
    private final int REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "49561555a6fd4897912fddebb7bf7da8";
    private static final String REDIRECT_URI = "testspotify://callback";
    private static final String WELC0ME_FRAG_TAG = "WELCOME_FRAG";
    private static final String PLAYLIST_FRAG_TAG = "PLAYLIST_FRAG_TAG";
    private static final String TRACKS_FRAG_TAG = "TRACKS_FRAG_TAG";
    private final String[] scopes = new String[]{"streaming,playlist-modify-public," +
            "playlist-read-private,playlist-read-collaborative"};
    public static final int MAX_ALBUM_DIMENSIONS = 200;
    private SpotifyApi api;
    public static SpotifyService spotify;
    public AuthenticationRequest request;
    public static ArrayList<PlaylistSimple> userPlaylists;
    public static String userid;
    public static  HashMap<String,String> swappedSongs = new HashMap<>();
    public static boolean playlistSwapified;

    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = new SpotifyApi();
        fragmentManager = getSupportFragmentManager();
        userPlaylists = new ArrayList<>();
        playlistSwapified = false;
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                        .setShowDialog(true)
                        .setScopes(scopes);

        request = builder.build();

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
                    Log.d(TAG, " user auth success");
                    api.setAccessToken(response.getAccessToken());
                    spotify = api.getService();
                    // Populate our user playlist data
                    getPlaylists();
                    // Display welcome fragment with given username
                    spotify.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate userPrivate, Response response) {
                            userid = userPrivate.id;
                            WelcomeFragment welcomeFrag =
                                    (WelcomeFragment) fragmentManager.findFragmentByTag(WELC0ME_FRAG_TAG);
                            if (welcomeFrag != null) { welcomeFrag.setNewUsername(userPrivate.display_name); }
                            else {
                                welcomeFrag = WelcomeFragment.newInstance(userPrivate.display_name);
                                fragmentManager
                                        .beginTransaction()
                                        .setCustomAnimations(0,
                                                android.R.anim.slide_out_right,
                                                android.R.anim.slide_in_left,
                                                android.R.anim.slide_out_right)
                                        .add(R.id.fragment_container, welcomeFrag, WELC0ME_FRAG_TAG)
                                        .commit();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                    break;
                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d(TAG, "user auth: " + response.getError());
                    if(fragmentManager.findFragmentByTag(WELC0ME_FRAG_TAG) == null) { finish(); }
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    Log.d(TAG, "user auth default error");
                    if(fragmentManager.findFragmentByTag(WELC0ME_FRAG_TAG) == null) { finish(); }
            }
        }
    }

    public static void getPlaylists() {
        spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                userPlaylists.addAll(playlistSimplePager.items);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "get playlist: " + error.toString());
            }
        });
    }

    public static void addNewlyCreatedPlaylist() {
        spotify.getMyPlaylists(new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                userPlaylists.add(0, playlistSimplePager.items.get(0));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "add new playlist: " + error.toString());
            }
        });
    }

    // interface OnPlaylistFragmentInteractionListener method
    public void initPlaylistFragment() {
        PlaylistFragment playlistFrag = PlaylistFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .replace(R.id.fragment_container, playlistFrag, PLAYLIST_FRAG_TAG)
                .addToBackStack(null)
                .commit();
    }

    //interface OnTracklistFragmentInteraction Listener method
    public void initTrackFragment(final PlaylistSimple playlist) {
        Log.d("methodCall", "initTrackFragment");
        spotify.getPlaylistTracks(playlist.owner.id, playlist.id, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                ArrayList<PlaylistTrack> playlistTracks = new ArrayList<>(playlistTrackPager.items);
                TracksFragment tracksFrag = TracksFragment.newInstance(playlistTracks, playlist.name, playlist.id);
                Log.d("recyclerView", playlist.name);
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right,
                                android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, tracksFrag, TRACKS_FRAG_TAG)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("initTrackFragment", error.toString());
            }
        });
    }

    // interface OnWelcomeFragmentInteractionListener method
    public void logout() {
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    public static void createPlaylist(String name, String description) {
        Map<String, Object> optionMap = new HashMap<>();
        optionMap.put("name", name);
        optionMap.put("description", description);
        optionMap.put("public", true);
        if (swappedSongs.isEmpty()) {
            Log.d("createPlaylist", "no new song data");
        }
        spotify.createPlaylist(userid, optionMap, new Callback<Playlist>() {
            @Override
            public void success(final Playlist playlist, Response response) {
                //add to playlist
                Log.d("createPlaylist", "Created Playlist");
                Map<String,Object> queryParameters = new HashMap<>();
                StringBuilder listOfSongUris = new StringBuilder();
                int i = 0;
                for (String key : swappedSongs.keySet()) {
                    if(i < swappedSongs.size() - 1 ){
                        listOfSongUris.append(swappedSongs.get(key));
                        listOfSongUris.append(",");
                    }
                    else{
                        listOfSongUris.append(swappedSongs.get(key));
                    }
                    i++;
                }
                Log.d("createPlaylist", listOfSongUris.toString());
                queryParameters.put("uris",listOfSongUris.toString());
                Map<String,Object> body = new HashMap<>();
                spotify.addTracksToPlaylist(playlist.owner.id, playlist.id, queryParameters, body, new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        Log.d("createPlaylist","Swapped songs successfully added to playlist");
                        playlistSwapified = true;
                        PlaylistFragment playlistFragment =
                                (PlaylistFragment) fragmentManager.findFragmentByTag(PLAYLIST_FRAG_TAG);
                        addNewlyCreatedPlaylist();
                        playlistFragment.adapter.notifyItemInserted(0);
                        TracksFragment tracksFragment =
                                (TracksFragment) fragmentManager.findFragmentByTag(TRACKS_FRAG_TAG);
                        tracksFragment.displaySwapifyProgress(playlistSwapified);
                        playlistSwapified = false;
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("createPlaylist","Swapped songs unabled to be added to playlist");
                        Log.d("createPlaylist",error.getMessage());
                        TracksFragment tracksFragment =
                                (TracksFragment) fragmentManager.findFragmentByTag(TRACKS_FRAG_TAG);
                        tracksFragment.displaySwapifyProgress(!playlistSwapified);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("createPlaylist", error.getMessage());
                Log.d("createPlaylist", userid);
            }
        });
    }

    public static void createPlaylistTest(HashMap<String, String> newSongData, String pName, String pDesc) {
        Map<String, Object> optionMap = new HashMap<>();
        optionMap.put("name", pName);
        optionMap.put("description", pDesc);
        optionMap.put("public", true);
        if (newSongData.isEmpty()) { Log.d("createPlaylist", "no new song data"); }
        CompletableFuture.supplyAsync(() -> spotify.createPlaylist(userid, optionMap))
                .thenCompose(newPlaylist -> {
                    Log.d("createPlaylist", "Created Playlist");
                    Map<String, Object> queryParameters = new HashMap<>();
                    StringBuilder listOfSongUris = new StringBuilder();
                    Iterator<String> newSongDataKeySetItr = newSongData.keySet().iterator();
                    listOfSongUris.append(newSongData.get(newSongDataKeySetItr.next()));
                    while (newSongDataKeySetItr.hasNext()) {
                        listOfSongUris.append(",");
                        listOfSongUris.append(newSongData.get(newSongDataKeySetItr.next()));
                    }
                    Log.d("createPlaylist", "MYSongUris: " + listOfSongUris.toString());
                    queryParameters.put("uris", listOfSongUris.toString());
                    return CompletableFuture.supplyAsync(() -> {
                        Map<String,Object> body = new HashMap<>();
                        return spotify.addTracksToPlaylist(
                                newPlaylist.owner.id,
                                newPlaylist.id,
                                queryParameters,
                                body
                        );
                    });
                })
                .thenRun(() -> {
                    Log.d("createPlaylist","Add swapped songs worked");
                    playlistSwapified = true;
                    PlaylistFragment playlistFragment =
                            (PlaylistFragment) fragmentManager.findFragmentByTag(PLAYLIST_FRAG_TAG);
                    addNewlyCreatedPlaylist();
                    playlistFragment.adapter.notifyItemInserted(0);
                    TracksFragment tracksFragment =
                            (TracksFragment) fragmentManager.findFragmentByTag(TRACKS_FRAG_TAG);
                    tracksFragment.displaySwapifyProgress(playlistSwapified);
                    playlistSwapified = false;
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
                            Log.d("getSwappedTrackUris", "Add getArtistTopTrack didnt work");
                        }
                    });
                }
                createPlaylist(name, description);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d("getSwappedTrackUris", "Add getSwappedTrackUris didnt work");
            }
        });
    }

//    // TEST HELPER FUNCTIONS FOR NEW SWAPPING ALG
//    public static HashMap<String, String> generateNewAlbumTracks(ArrayList<PlaylistTrack> oldtracks) {
//        HashMap<String, String> swappedSongsData = new HashMap<>();
//        ArrayList<String> oldTrackIDs = new ArrayList<>();
//        Random random = new Random();
//        for (PlaylistTrack track : oldtracks) {
//            oldTrackIDs.add(track.track.id);
//        }
//        ArrayList<CompletableFuture<Void>> completableFuturesTracks = new ArrayList<>();
//        for (PlaylistTrack track : oldtracks) {
//            completableFuturesTracks.add(CompletableFuture.supplyAsync(() -> spotify.getArtistAlbums(track.track.artists.get(0).id))
//                    .thenCompose(albumPager -> {
//                        Album newTrackAlbum = albumPager.items.get(random.nextInt(albumPager.items.size()));
//                        return CompletableFuture.supplyAsync(() -> spotify.getAlbumTracks(newTrackAlbum.id));
//                    })
//                    .thenAccept(trackPager -> {
//                        boolean trackAdded = false;
//                            while (!trackAdded) {
//                            Track potentialNewTrack = trackPager.items.get(random.nextInt(trackPager.items.size()));
//                            if (!oldTrackIDs.contains(potentialNewTrack.id) || (trackPager.items.size() == 1)) {
//                                swappedSongsData.put(potentialNewTrack.id, potentialNewTrack.uri);
//                                trackAdded = true;
//                                Log.d("SWAPPING", "track added");
//                            }
//                        }
//                    }));
//        }
//        completableFuturesTracks.forEach(CompletableFuture::join);
//        Log.d("newAlbumTracks", "Newly generated tracks: " + swappedSongsData.toString());
//        return swappedSongsData;
//    }

    /*
    getSwappedTrackUrisFromAlbums
    Generates new, unique tracks for the swapified playlist
    Tracks are randomly generated based on Artist
    eg. Get old track's artist -> Get all albums from that artist ->
        Choose album from random -> Get all tracks from that album ->
        Choose track from that album that doesn't exist in original playlist ->
        Add that track to our swappedSongs data structure
        */
    public static void getSwappedTrackUrisFromAlbums(ArrayList<PlaylistTrack> oldtracks, String newname, String newdesc) {
        if(swappedSongs.size() > 0) { swappedSongs.clear(); }
        HashMap<String, String> newSongData = new HashMap<>();
        ArrayList<String> oldTrackIDs = new ArrayList<>();
        Random random = new Random();
        for (PlaylistTrack track : oldtracks) {
            oldTrackIDs.add(track.track.id);
        }
        ArrayList<CompletableFuture<Void>> completableFuturesTracks = new ArrayList<>();
        for (PlaylistTrack track : oldtracks) {
            completableFuturesTracks.add(CompletableFuture.supplyAsync(() -> spotify.getArtistAlbums(track.track.artists.get(0).id))
                    .thenCompose(albumPager -> {
                        Album newTrackAlbum = albumPager.items.get(random.nextInt(albumPager.items.size()));
                        return CompletableFuture.supplyAsync(() -> spotify.getAlbumTracks(newTrackAlbum.id));
                    })
                    .thenAccept(trackPager -> {
                        boolean trackAdded = false;
                        while (!trackAdded) {
                            Track potentialNewTrack = trackPager.items.get(random.nextInt(trackPager.items.size()));
                            if ((!oldTrackIDs.contains(potentialNewTrack.id) && !newSongData.containsKey(potentialNewTrack.id)) || (trackPager.items.size() == 1)) {
                                newSongData.put(potentialNewTrack.id, potentialNewTrack.uri);
                                trackAdded = true;
                                Log.d("SWAPPING", "track added");
                            }
                        }
                    })
            );
        }

        completableFuturesTracks.forEach(CompletableFuture::join);
        Log.d("newAlbumTracks", "Newly generated tracks: " + newSongData.toString());
        createPlaylistTest(newSongData, newname, newdesc);
//        CompletableFuture.runAsync(() -> {
//            for (PlaylistTrack track : oldtracks) {
//                Log.d("asyncTask", "asyncTask started for: " + track.track.name);
//                CompletableFuture.supplyAsync(() -> spotify.getArtistAlbums(track.track.artists.get(0).id))
//                        .thenCompose(albumPager -> {
//                            Album newTrackAlbum = albumPager.items.get(random.nextInt(albumPager.items.size()));
//                            Log.d("asyncTask", "Track: " + track.track.name + " Album: " + newTrackAlbum.name );
//                            return CompletableFuture.supplyAsync(() -> spotify.getAlbumTracks(newTrackAlbum.id));
//                        })
//                        .thenAccept(trackPager -> {
//                            boolean trackAdded = false;
//                            while (!trackAdded) {
//                                Track potentialNewTrack = trackPager.items.get(random.nextInt(trackPager.items.size()));
//                                Log.d("asyncTask", "Potential new track: " + potentialNewTrack.name);
//                                if (!oldTrackIDs.contains(potentialNewTrack.id) || (trackPager.items.size() == 1)) {
//                                    newSongData.put(potentialNewTrack.id, potentialNewTrack.uri);
//                                    trackAdded = true;
//                                    Log.d("asyncTask", "TRACK ADDED");
//                                }
//                            }
//                        });
//            }
//        }).thenRun(() ->  {
//            Log.d("newSongData info", newSongData.toString());
//            swappedSongs.putAll(newSongData);
//            createPlaylist(newname, newdesc);
//        });
    }

//    public static void getSwappedTrackUrisFromAlbumsTEST(String playlistid, ArrayList<PlaylistTrack> oldtracks, final String newname, final String newdesc) {
//        HashMap<String, String> swappedTracks;
//
//        ArrayList<String> oldTrackIDs = new ArrayList<>();
//        final Random random = new Random();
//        for (PlaylistTrack track : oldtracks) {
//            oldTrackIDs.add(track.track.id);
//        }
//        for (PlaylistTrack track : oldtracks) {
//                    spotify.getArtistAlbums(track.track.artists.get(0).id, new Callback<Pager<Album>>() {
//                        boolean trackAdded = false;
//
//                        @Override
//                        public void success(Pager<Album> albumPager, Response response) {
//                            Album newTrackAlbum = albumPager.items.get(random.nextInt(albumPager.items.size()));
//                            spotify.getAlbumTracks(newTrackAlbum.id, new Callback<Pager<Track>>() {
//                                @Override
//                                public void success(Pager<Track> trackPager, Response response) {
//                                    while (!trackAdded) {
//                                        Log.d("SWAPPING", "track adding");
//                                        Track potentialNewTrack = trackPager.items.get(random.nextInt(trackPager.items.size()));
//                                        if (!oldTrackIDs.contains(potentialNewTrack.id) || (trackPager.items.size() == 1)) {
//                                            swappedSongs.put(potentialNewTrack.id, potentialNewTrack.uri);
//                                            trackAdded = true;
//                                        }
//                                    }
//                                }
//                                @Override
//                                public void failure(RetrofitError error) {
//                                    Log.d("retrofit", error.toString());
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            Log.d("retrofit", error.toString());
//                        }
//                    });
//        }
//    }
}

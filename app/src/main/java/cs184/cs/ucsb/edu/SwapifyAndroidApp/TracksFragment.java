package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

/**
 * A fragment representing a list of Tracks.
 * Activities containing this fragment MUST implement the {@link OnTracksFragmentInteractionListener}
 * interface.
 */

public class TracksFragment extends Fragment {

    private static final String PLAYLIST_TAG = "PLAYLIST_TAG";
    private static final String PLAYLIST_ID_TAG = "PLAYLIST_ID_TAG";
    private static final String PLAYLIST_NAME_TAG = "PLAYLIST_NAME_TAG";
    OnTracksFragmentInteractionListener mListener;
    private ArrayList<PlaylistTrack> mTrackList;
    public TracksRecyclerViewAdapter adapter;
    private static String  playlistId;
    private static String playlistName;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TracksFragment() {
    }

    @SuppressWarnings("unused")
    public static TracksFragment newInstance(ArrayList<PlaylistTrack> tracks, String name, String id) {
        TracksFragment fragment = new TracksFragment();
        Bundle data = new Bundle();
        data.putParcelableArrayList(PLAYLIST_TAG, tracks);
        data.putString(PLAYLIST_ID_TAG, id);
        data.putString(PLAYLIST_NAME_TAG, name);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrackList = getArguments().getParcelableArrayList(PLAYLIST_TAG);
            playlistId = getArguments().getString(PLAYLIST_ID_TAG);
            playlistName = getArguments().getString(PLAYLIST_NAME_TAG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);
        View rc = view.findViewById(R.id.track_list);
        // Set the adapter with a linear layout manager
        if (rc instanceof RecyclerView) {
            Log.d("recyclerView", "RecyclerView instantiated");
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) rc;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            adapter = new TracksRecyclerViewAdapter(mTrackList, mListener);
            recyclerView.setAdapter(adapter);
        }
        Button swapPlaylistButton = view.findViewById(R.id.b_swapify);
        swapPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recyclerView", "Swapped button");
                Log.d("trackRecyclerView", "Total playlists before swap: " + Integer.toString(MainActivity.userPlaylists.size()));
                MainActivity.getSwappedTrackUris(playlistId,playlistName + " Swapped ;) ","Created by Swapify");
            }
        });
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTracksFragmentInteractionListener) {
            mListener = (OnTracksFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTracksFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mTrackList = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTracksFragmentInteractionListener {

    }
}

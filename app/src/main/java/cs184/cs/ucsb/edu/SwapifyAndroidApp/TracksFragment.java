package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * A fragment representing a list of Tracks.
 * Activities containing this fragment MUST implement the {@link OnTracksFragmentInteractionListener}
 * interface.
 */

public class TracksFragment extends Fragment {

    public static final String PLAYLIST_TAG = "PLAYLIST_TAG";
    private OnTracksFragmentInteractionListener mListener;
    private ArrayList<PlaylistTrack> mTrackList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TracksFragment() {
    }

    @SuppressWarnings("unused")
    public static TracksFragment newInstance(ArrayList<PlaylistTrack> tracks) {
        TracksFragment fragment = new TracksFragment();
        Bundle data = new Bundle();
        data.putParcelableArrayList(PLAYLIST_TAG, tracks);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTrackList = getArguments().getParcelableArrayList(PLAYLIST_TAG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        // Set the adapter with a linear layout manager
        if (view instanceof RecyclerView) {
            Log.d("recyclerView", "RecyclerView instantiated");
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new TracksRecyclerViewAdapter(mTrackList, mListener));
        }
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
        void swapifySong(final Track original);
    }
}

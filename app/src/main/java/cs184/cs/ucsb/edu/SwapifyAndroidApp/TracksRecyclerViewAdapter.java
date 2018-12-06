package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cs184.cs.ucsb.edu.SwapifyAndroidApp.TracksFragment.OnTracksFragmentInteractionListener;

import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaylistTrack}
 */

public class TracksRecyclerViewAdapter extends RecyclerView.Adapter<TracksRecyclerViewAdapter.TrackViewHolder> {

    private final ArrayList<PlaylistTrack> mTracks;
    private final OnTracksFragmentInteractionListener mListener;

    public TracksRecyclerViewAdapter(ArrayList<PlaylistTrack> items, TracksFragment.OnTracksFragmentInteractionListener listener) {
        mTracks = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("methodCall", "onCreateViewHolder called, viewType: " + Integer.toString(viewType));
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_track_item, parent, false);
        view.findViewById(R.id.track_title).setSelected(true);
        view.findViewById(R.id.track_artist).setSelected(true);
        view.findViewById(R.id.track_albumname).setSelected(true);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TrackViewHolder holder, int position) {

        // Code below to be used when a working list of tracks is init from the main activity
        holder.mTrack = mTracks.get(position);
        final Track cTrack = holder.mTrack.track;
        Picasso.get()
                .load(cTrack.album.images.get(0).url)
                .resize(MainActivity.MAX_ALBUM_DIMENSIONS, MainActivity.MAX_ALBUM_DIMENSIONS)
                .into(holder.mAlbumImgView);
        holder.mTrackTitleView.setText(cTrack.name);
        // Logic for create a string of artists for tracks with more than 1
//        StringBuilder allTrackArtist = new StringBuilder();
//        allTrackArtist.append(cTrack.artists.get(0).name);
//
//        if(cTrack.artists.size() > 1) {
//            for(int i = 1; i < cTrack.artists.size(); i++) {
//               allTrackArtist.append(", ");
//               allTrackArtist.append(cTrack.artists.get(i));
//            }
//        }

        holder.mArtistView.setText(cTrack.artists.get(0).name);
        holder.mAlbumNameView.setText(cTrack.album.name);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Log.d("onClickListener", "Track " + cTrack.name + " selected");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView mAlbumImgView;
        public TextView mTrackTitleView;
        public TextView mArtistView;
        public TextView mAlbumNameView;
        public PlaylistTrack mTrack;

        public TrackViewHolder(View view) {
            super(view);
            mView = view;
            mAlbumImgView = (ImageView) view.findViewById(R.id.track_album_img);
            mTrackTitleView = (TextView) view.findViewById(R.id.track_title);
            mArtistView = (TextView) view.findViewById(R.id.track_artist);
            mAlbumNameView = (TextView) view.findViewById(R.id.track_albumname);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mTrackTitleView.getText() + "'";
        }
    }
}

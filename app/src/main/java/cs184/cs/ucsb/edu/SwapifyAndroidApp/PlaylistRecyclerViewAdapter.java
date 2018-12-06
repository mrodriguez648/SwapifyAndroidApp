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

import cs184.cs.ucsb.edu.SwapifyAndroidApp.PlaylistFragment.OnPlaylistFragmentInteractionListener;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaylistSimple} item and makes a call to the
 * specified {@link OnPlaylistFragmentInteractionListener}.
 */

public class PlaylistRecyclerViewAdapter extends RecyclerView.Adapter<PlaylistRecyclerViewAdapter.PlaylistViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private final ArrayList<PlaylistSimple> mPlaylists;
    private final OnPlaylistFragmentInteractionListener mListener;

    public PlaylistRecyclerViewAdapter(ArrayList<PlaylistSimple> playlists, OnPlaylistFragmentInteractionListener listener) {
        mPlaylists = playlists;
        mListener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_playlist_item, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaylistViewHolder holder, int position) {
        holder.playlistItem = mPlaylists.get(position);
        Picasso.get()
                .load(holder.playlistItem.images.get(0).url)
                .resize(MainActivity.MAX_ALBUM_DIMENSIONS, MainActivity.MAX_ALBUM_DIMENSIONS)
                .into(holder.albumImgView);
        holder.playlistTitleView.setText(holder.playlistItem.name);
        holder.playlistAuthorView.setText(holder.playlistItem.owner.display_name);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Log.d("onClickListener", "Playlist " + holder.playlistItem.name + " selected");
                    mListener.initTrackFragment(holder.playlistItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView albumImgView;
        public TextView playlistTitleView;
        public TextView playlistAuthorView;
        public PlaylistSimple playlistItem;
        public PlaylistViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            albumImgView = view.findViewById(R.id.track_album_img);
            playlistAuthorView = view.findViewById(R.id.playlist_author);
            playlistTitleView = view.findViewById(R.id.track_title);
        }
    }
}

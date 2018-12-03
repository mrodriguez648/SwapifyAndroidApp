package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaylistTrack}
 */
public class TrackRecyclerViewAdapter extends RecyclerView.Adapter<TrackRecyclerViewAdapter.TrackViewHolder> {

//    private final ArrayList<PlaylistTrack> mTracks;
    private ArrayList<String> mImages;
    private Context mContext;

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView mAlbumImgView;
        public TextView mTrackTitleView;
        public TextView mArtistView;
        public TextView mAlbumNameView;
        // Track needed for real implementation for onclick viewmodel logic
        // public PlaylistTrack mTrack;

        public TrackViewHolder(View view) {
            super(view);
            mView = view;
            mAlbumImgView = (ImageView) view.findViewById(R.id.track_album);
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

    public TrackRecyclerViewAdapter(ArrayList<String> Images, Context context) {
        dummyData = new ArrayList<>();
        mImages = Images;
        mContext = context;
        // For test purposes
        createDummyData();
    }

    // Dummy data for testing below

    private ArrayList<DummyTrack> dummyData;

    public class DummyTrack {
        public String dAlbumImgURL;
        public String dTrackTitle;
        public String dArtistName;
        public String dAlbumName;
        public DummyTrack(String dAlbumImgURL, String dTrackTitle,
                          String dArtistName, String dAlbumName) {
            this.dAlbumImgURL = dAlbumImgURL;
            this.dTrackTitle = dTrackTitle;
            this.dArtistName = dArtistName;
            this.dAlbumName = dAlbumName;
        }
    }


    public void createDummyData() {
        DummyTrack trackOne = new DummyTrack(
                "https://media.pitchfork.com/photos/5929c572c0084474cd0c3740/1:1/w_320/1ad39bb4.jpg",
                "Amnesia",
                "Freddie Gibbs",
                "You Only Live 2wice"
        );
        DummyTrack trackTwo = new DummyTrack(
                "https://media.pitchfork.com/photos/5929bc47abf31b7dc7155c9b/1:1/w_320/2a8b2311.jpg",
                "goosebumps",
                "Travis Scott",
                "Birds in the Trap sing McKnight"
        );
        dummyData.add(trackOne);
        dummyData.add(trackTwo);
        Log.d("methodCall", "createDummyData called, dummyData populated");
    }
    // end dummy data

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("methodCall", "onCreateViewHolder called, viewType: " + Integer.toString(viewType));
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracklist_item, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        // Dummy data populated below
        Log.d("methodCall", "onBindViewHolder called for " + Integer.toString(position));
        DummyTrack cTrack = dummyData.get(position);
        holder.mAlbumNameView.setText(cTrack.dAlbumName);
        holder.mArtistView.setText(cTrack.dArtistName);
        Picasso.get()
                .load(cTrack.dAlbumImgURL)
                .resize(80,80)
                .into(holder.mAlbumImgView);

        holder.mTrackTitleView.setText(cTrack.dTrackTitle);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Code below to be used when a working list of tracks is init from the main activity
        // and passed to ctor of this RecyclerView
//        Track cTrack = mTracks.get(position).track;
//        Picasso.get().load(cTrack.album.images.get(0).url).into(holder.mAlbumImgView);
//        holder.mTrackTitleView.setText(cTrack.name);
//        StringBuilder allTrackArtist = new StringBuilder();
//        allTrackArtist.append(cTrack.artists.get(0).name);
//        if(cTrack.artists.size() > 1) {
//            for(int i = 1; i < cTrack.artists.size(); i++) {
//               allTrackArtist.append(", ");
//               allTrackArtist.append(cTrack.artists.get(i));
//            }
//        }
//        holder.mArtistView.setText(allTrackArtist.toString());
//        holder.mAlbumNameView.setText(cTrack.album.name);
//        holder.mTrack = mTracks.get(position);
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onSongListFragmentInteraction(holder.mTrack);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return dummyData.size();
    }
}

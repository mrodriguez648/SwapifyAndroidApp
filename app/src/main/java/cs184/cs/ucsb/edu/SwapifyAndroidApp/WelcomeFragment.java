package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnWelcomeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class WelcomeFragment extends Fragment {

    public static final String USERNAME_TAG = "USERNAME_TAG";
    private ImageView mSpotifyIcon;
    private TextView mWelcomeView;
    private TextView mUsernameView;
    private TextView mWelcomeDescView;
    private Button mSwapify;
    private Button mLogout;
    private String username;
    private int textViewAnimeTime;
    private OnWelcomeFragmentInteractionListener mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance(String username) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle data = new Bundle();
        data.putString(USERNAME_TAG, username);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        mSpotifyIcon = (ImageView) rootView.findViewById(R.id.imgv_spotify);
        mWelcomeView = (TextView) rootView.findViewById(R.id.welcome_txt);
        mWelcomeDescView = (TextView) rootView.findViewById(R.id.welcome_desc);
        mUsernameView = (TextView) rootView.findViewById(R.id.username);
        mSwapify = (Button) rootView.findViewById(R.id.b_swapify);
        mLogout = (Button) rootView.findViewById(R.id.b_logout);
        mSpotifyIcon.setVisibility(View.GONE);
        mWelcomeView.setVisibility(View.GONE);
        mWelcomeDescView.setVisibility(View.GONE);
        mUsernameView.setVisibility(View.GONE);
        mSwapify.setVisibility(View.GONE);
        mLogout.setVisibility(View.GONE);
        mUsernameView.setText(username);
        textViewAnimeTime = getResources().getInteger(android.R.integer.config_longAnimTime);
        fadeViewIn(mSpotifyIcon);
        fadeViewIn(mWelcomeView);
        fadeViewIn(mUsernameView);
        fadeViewIn(mWelcomeDescView);
        fadeViewIn(mSwapify);
        fadeViewIn(mLogout);

        mSwapify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.initPlaylistFragment();
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeFragmentInteractionListener) {
            mListener = (OnWelcomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWelcomeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fadeViewIn(View v) {
        v.setAlpha(0f);
        v.setVisibility(View.VISIBLE);
        v.animate()
                .alpha(1f)
                .setDuration(textViewAnimeTime)
                .setListener(null);
    }

    public void setNewUsername(String username) {
        mUsernameView.setAlpha(1f);
        mUsernameView.animate()
                .alpha(0f)
                .setDuration(textViewAnimeTime)
                .setListener(null);
        mUsernameView.setVisibility(View.GONE);
        mUsernameView.setText(username);
        fadeViewIn(mUsernameView);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnWelcomeFragmentInteractionListener {
        void initPlaylistFragment();
        void logout();
    }
}

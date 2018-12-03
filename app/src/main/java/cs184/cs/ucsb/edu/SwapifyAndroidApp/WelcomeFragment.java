package cs184.cs.ucsb.edu.SwapifyAndroidApp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


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
    private String username;
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

        TextView usernameView = rootView.findViewById(R.id.username);
        usernameView.setText(username);

        Button bSwapify = rootView.findViewById(R.id.b_swapify);
        bSwapify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.initPlaylistFragment();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnWelcomeFragmentInteractionListener {
        void initPlaylistFragment();
    }
}

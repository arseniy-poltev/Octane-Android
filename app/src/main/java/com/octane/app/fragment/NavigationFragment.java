package com.octane.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octane.app.Component.CircularNavigationView;
import com.octane.app.R;
import com.octane.app.Component.CircularActiveView;
import com.octane.app.activity.HomeActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment implements
        CircularNavigationView.OnCircularItemClickListener,
        CircularNavigationView.OnProfileItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    CircularNavigationView circularNavigationView;
    TextView txtProfileName;
    TextView txtProfileDetail;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(String param1, String param2) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        circularNavigationView = view.findViewById(R.id.circular_layout);
        circularNavigationView.setOnCircularItemClickListener(this);
        circularNavigationView.setOnProfileItemClickListener(this);

        //circularNavigationView.setOnCircularItemClickListener((HomeActivity)getActivity());
        int currerntProfile = ((HomeActivity) Objects.requireNonNull(getActivity())).getCurProfileIndex();
        circularNavigationView.setM_curIndex(currerntProfile);

        txtProfileName = view.findViewById(R.id.profileName);
        txtProfileDetail = view.findViewById(R.id.profDesc);
        changeProfileText(currerntProfile);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCircularItemClick(int index) {
        circularNavigationView.rotateEffect(index);
        ((HomeActivity) Objects.requireNonNull(getActivity())).onCircularItemClick(index);
        //if(index < 0 || index > 5)
        //    return;
    }

    @Override
    public void onProfileItemClick(int index) {
        changeProfileText(index);
    }

    public interface OnProfileListener{
        void onProfileChanged(int index);
    }

    public void changeProfileText(int profileIndex){
        if(profileIndex >= 0){
            final String profileName = ((HomeActivity) Objects.requireNonNull(getActivity())).profiles.get(profileIndex).getProfileName();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtProfileName.setText(profileName);
                    txtProfileDetail.setText("Puts your vehicle into " + profileName);
                }
            });

        }
    }
}

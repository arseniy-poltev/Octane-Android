package com.octane.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octane.app.Component.CircularActiveView;
import com.octane.app.Component.CircularNavigationView;
import com.octane.app.R;
import com.octane.app.activity.HomeActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link ActiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    CircularActiveView circularActiveView;
    TextView txtProfileName;
    TextView txtProfileDetail;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ActiveFragment() {
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
    public static ActiveFragment newInstance(String param1, String param2) {
        ActiveFragment fragment = new ActiveFragment();
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
        Log.d("debug","create view");
        View view = inflater.inflate(R.layout.fragment_active, container, false);
        circularActiveView = view.findViewById(R.id.circular_layout);
        circularActiveView.setOnCircularItemClickListener((HomeActivity)getActivity());
        int currerntProfile = ((HomeActivity) Objects.requireNonNull(getActivity())).getCurProfileIndex();
        circularActiveView.setM_curIndex(currerntProfile);
        txtProfileName = view.findViewById(R.id.profileName);
        txtProfileDetail = view.findViewById(R.id.profDesc);
        changeProfileText(currerntProfile);

        circularActiveView.invalidate();
        return view;
    }

    public void setProfile(int index){
        circularActiveView.setM_curIndex(index);
        changeProfileText(index);
        circularActiveView.invalidate();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("debug","resume");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("debug","attach");

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

    public void changeProfileText(int profileIndex){
        if(profileIndex >= 0){
            String profileName = ((HomeActivity) Objects.requireNonNull(getActivity())).profiles.get(profileIndex).getProfileName();
            txtProfileName.setText(profileName);
            txtProfileDetail.setText("Your vehicle is in " + profileName);
        }
    }

}

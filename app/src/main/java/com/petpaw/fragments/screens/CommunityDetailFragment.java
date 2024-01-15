package com.petpaw.fragments.screens;

import static com.petpaw.fragments.screens.ProfileFragment.PREVIOUS_FRAGMENT;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.petpaw.databinding.FragmentCommunityDetailBinding;


import com.petpaw.R;

import java.time.LocalDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FragmentCommunityDetailBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommunityDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param communityId Parameter 1.
     * @param previousFragment Parameter 2.
     * @return A new instance of fragment CommunityDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityDetailFragment newInstance(String communityId, int previousFragment) {
        CommunityDetailFragment fragment = new CommunityDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, communityId);
        args.putInt(PREVIOUS_FRAGMENT, previousFragment);
        Log.d("CommunityDetailFragment", "newInstance: " + communityId + ", " + previousFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Log.d("CommunityDetailFragment", "onCreate: " + mParam1 + " " + mParam2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CommunityDetailFragment", "onCreateView: " + mParam1 + " " + mParam2);
        binding = FragmentCommunityDetailBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}
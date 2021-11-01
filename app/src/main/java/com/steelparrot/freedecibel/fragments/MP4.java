package com.steelparrot.freedecibel.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.network.YoutubeDLFactory;
import com.yausername.youtubedl_android.DownloadProgressCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MP4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MP4 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters

    public MP4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MP4.
     */
    // TODO: Rename and change types and number of parameters
    public static MP4 newInstance(String url) {
        MP4 fragment = new MP4();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // receive args
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_mp4, container, false);
        return binding.getRootView();
    }
}
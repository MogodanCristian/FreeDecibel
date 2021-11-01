package com.steelparrot.freedecibel.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.activities.YTItemActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MP4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MP4 extends Fragment {

    public interface OnMP4DataPass {
        public void onVideoQualityPass(YTItemActivity.VideoQuality videoQuality);
    }

    OnMP4DataPass mOnMP4DataPass;

    private AutoCompleteTextView mAutoCompleteTextView;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mOnMP4DataPass = (OnMP4DataPass) context;
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
        String[] resolutions = getResources().getStringArray(R.array.Resolutions);
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, resolutions);

        mAutoCompleteTextView = binding.findViewById(R.id.autoCompleteTextViewMP4);
        mAutoCompleteTextView.setAdapter(arrayAdapter);
        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    mOnMP4DataPass.onVideoQualityPass(YTItemActivity.VideoQuality.V640x360);
                }
                else {
                    mOnMP4DataPass.onVideoQualityPass(YTItemActivity.VideoQuality.V1280x720);
                }
            }
        });

        return binding.getRootView();
    }

}
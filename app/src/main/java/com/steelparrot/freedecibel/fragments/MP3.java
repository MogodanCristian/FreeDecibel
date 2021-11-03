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

public class MP3 extends Fragment{

    public interface OnMP3DataPass {
        public void onFormatPass(YTItemActivity.Format format);
        public void onBitrateQualityPass(YTItemActivity.BitrateAudioQuality bitrateAudioQuality);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private AutoCompleteTextView mAutoCompleteTextView;
    OnMP3DataPass mOnDataPass;

    public MP3() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MP3.
     */
    // TODO: Rename and change types and number of parameters
    public static MP3 newInstance(String url) {
        MP3 fragment = new MP3();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mOnDataPass = (OnMP3DataPass) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // implement on need
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View binding = inflater.inflate(R.layout.fragment_mp3, container, false);
        String[] qualities = getResources().getStringArray(R.array.Qualities);
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, qualities);

        mAutoCompleteTextView = binding.findViewById(R.id.autoCompleteTextView);
        mAutoCompleteTextView.setAdapter(arrayAdapter);
        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    mOnDataPass.onFormatPass(YTItemActivity.Format.M4A);
                    mOnDataPass.onBitrateQualityPass(YTItemActivity.BitrateAudioQuality.B128K);
                }
                else {
                    mOnDataPass.onFormatPass(YTItemActivity.Format.MP3);
                    mOnDataPass.onBitrateQualityPass(MapDropdownPositionToBitrateQuality(i));
                }
            }
        });

        return binding.getRootView();
    }

    private YTItemActivity.BitrateAudioQuality MapDropdownPositionToBitrateQuality(int pos) {
        if(pos == 1) {
            return YTItemActivity.BitrateAudioQuality.B64K;
        }
        else if(pos == 2) {
            return YTItemActivity.BitrateAudioQuality.B128K;
        }
        else if(pos == 3) {
            return YTItemActivity.BitrateAudioQuality.B192K;
        }
        else if(pos == 4) {
            return YTItemActivity.BitrateAudioQuality.B256K;
        }
        else if(pos == 5) {
            return YTItemActivity.BitrateAudioQuality.B320K;
        }
        return null;
    }
}
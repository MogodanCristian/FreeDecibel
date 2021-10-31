package com.steelparrot.freedecibel.fragments;

import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.Toast;


import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.network.YoutubeDLFactory;
import com.yausername.youtubedl_android.DownloadProgressCallback;

public class MP3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AutoCompleteTextView mAutoCompleteTextView;
    private Button mDownloadMP3;
    private YoutubeDLFactory mYoutubeDLFactory = null;
    private YoutubeDLFactory.Format mFormat = YoutubeDLFactory.Format.MP3;
    private YoutubeDLFactory.BitrateAudioQuality mBitrateAudioQuality = YoutubeDLFactory.BitrateAudioQuality.B128K;


    private final DownloadProgressCallback mCallback = new DownloadProgressCallback() {
        @Override
        public void onProgressUpdate(float progress, long etaInSeconds) {
            getActivity().runOnUiThread(() -> {
                  Toast.makeText(getActivity(), String.valueOf(progress), Toast.LENGTH_SHORT).show();
//                mProgressBar.setProgress((int) progress);
//                tvDownloadStatus.setText(String.valueOf(progress) + "% (ETA " + String.valueOf(etaInSeconds) + " seconds)");
            });
        }
    };

    public MP3() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MP3.
     */
    // TODO: Rename and change types and number of parameters
    public static MP3 newInstance(String param1, String param2) {
        MP3 fragment = new MP3();
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
    public void onDestroy() {
        super.onDestroy();
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
                    mFormat = YoutubeDLFactory.Format.M4A;
                }
                else {
                    mFormat = YoutubeDLFactory.Format.MP3;
                    MapDropdownPositionToBitrateQuality(i);
                }
            }
        });
        mDownloadMP3 = binding.findViewById(R.id.button_mp3);
        mDownloadMP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mYoutubeDLFactory = YoutubeDLFactory.getInstance(mFormat,"https://www.youtube.com/watch?v=L397TWLwrUU");
                if(mYoutubeDLFactory.isDownloading()) {
                    Toast.makeText(getActivity(),"cannot start downloading. a download is already in progress", Toast.LENGTH_LONG).show();
                    return;
                }
                if(mFormat == YoutubeDLFactory.Format.MP3) {
                    mYoutubeDLFactory.setBitrateAudioQuality(mBitrateAudioQuality);
                }
                mYoutubeDLFactory.startDownload(getActivity(),mCallback);
            }
        });

        return binding.getRootView();
    }

    private void MapDropdownPositionToBitrateQuality(int pos) {
        if(pos == 1) {
            mBitrateAudioQuality = YoutubeDLFactory.BitrateAudioQuality.B64K;
        }
        else if(pos == 2) {
            mBitrateAudioQuality = YoutubeDLFactory.BitrateAudioQuality.B128K;
        }
        else if(pos == 3) {
            mBitrateAudioQuality = YoutubeDLFactory.BitrateAudioQuality.B192K;
        }
        else if(pos == 4) {
            mBitrateAudioQuality = YoutubeDLFactory.BitrateAudioQuality.B256K;
        }
        else if(pos == 5) {
            mBitrateAudioQuality = YoutubeDLFactory.BitrateAudioQuality.B320K;
        }
    }
}
package com.steelparrot.freedecibel.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.steelparrot.freedecibel.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MP3#newInstance} factory method to
 * create an instance of this fragment.
 */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View binding = inflater.inflate(R.layout.fragment_mp3, container, false);
        String[] qualities = getResources().getStringArray(R.array.Qualities);
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, qualities);

        mAutoCompleteTextView = binding.findViewById(R.id.autoCompleteTextView);
        mAutoCompleteTextView.setAdapter(arrayAdapter);

        mDownloadMP3 = binding.findViewById(R.id.button_mp3);
        mDownloadMP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Se descarca imediat, boss! Te pup!", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRootView();
    }
}
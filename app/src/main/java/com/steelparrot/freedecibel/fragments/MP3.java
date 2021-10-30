package com.steelparrot.freedecibel.fragments;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.steelparrot.freedecibel.BuildConfig;
import com.steelparrot.freedecibel.R;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    private Switch useConfigFile;
    private ProgressBar mProgressBar;
    private TextView tvDownloadStatus;
    private TextView tvCommandOutput;
    private ProgressBar mProgressBarLoading;

    private boolean downloading = false;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

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

    private static final String TAG = "DownloadingExample";

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
                startDownload();
            }
        });

        return binding.getRootView();
    }

    private void startDownload() {

//        try {
//
//            File youtubeDLDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "youtubedl-android");
//            YoutubeDLRequest request = new YoutubeDLRequest("https://vimeo.com/22439234");
//            request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/%(title)s.%(ext)s");
//            YoutubeDL.getInstance().execute(request, (progress, etaInSeconds) -> {
//                System.out.println(String.valueOf(progress) + "% (ETA " + String.valueOf(etaInSeconds) + " seconds)");
//            });
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        if(downloading) {
            Toast.makeText(getActivity(),"cannot start downloading. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }

        if(!isStoragePermissionGranted()) {
            Toast.makeText(getActivity(), "grant storage permission and retry", Toast.LENGTH_LONG).show();
            return;
        }

//        String url = "https://vimeo.com/22439234";
        String url = "https://www.youtube.com/watch?v=4ewrSgDf50I";
        if(TextUtils.isEmpty(url)) {
            return;
        }

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        File youtubeDlDir = getDownloadLocation();
        File config = new File(youtubeDlDir, "config.txt");

        if(config.exists()) {
            request.addOption("--config-location", config.getAbsolutePath());
        }
        else {
            request.addOption("-o", youtubeDlDir.getAbsolutePath() + "/%(title)s.%(ext)s");
        }
        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, mCallback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    Toast.makeText(getActivity(), "download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                }, e -> {
                   if(BuildConfig.DEBUG) {
                       Log.e(TAG, "failed to download", e);
                    }
                   Toast.makeText(getActivity(),"download failed", Toast.LENGTH_LONG).show();
                   downloading = false;
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, "FreeDecibel");
        if(!youtubeDLDir.exists()) {
            youtubeDLDir.mkdir();
        }
        return youtubeDLDir;
    }

    private void showStart() {
        tvDownloadStatus.setText("Download started");
        mProgressBar.setProgress(0);
        mProgressBarLoading.setVisibility(View.VISIBLE);
    }

    public boolean isStoragePermissionGranted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
}
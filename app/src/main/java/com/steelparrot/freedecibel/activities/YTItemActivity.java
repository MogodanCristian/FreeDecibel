package com.steelparrot.freedecibel.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.steelparrot.freedecibel.BuildConfig;
import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.adapter.CustomAdapter;
import com.steelparrot.freedecibel.adapter.FragmentAdapter;
import com.steelparrot.freedecibel.fragments.MP3;
import com.steelparrot.freedecibel.fragments.MP4;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;


import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class YTItemActivity extends AppCompatActivity implements MP3.OnMP3DataPass, MP4.OnMP4DataPass {


    public enum Format {
        MP3,
        M4A,
        MP4
    }

    public enum BitrateAudioQuality {
        B64K,
        B128K,
        B192K,
        B256K,
        B320K
    }

    public enum VideoQuality {
        V640x360,
        V1280x720,
    }

    Format mFormat = Format.MP3;
    BitrateAudioQuality mBitrateAudioQuality = BitrateAudioQuality.B128K;
    VideoQuality mVideoQuality = VideoQuality.V640x360;

    @Override
    public void onFormatPass(Format format) {
        mFormat = format;
    }

    @Override
    public void onBitrateQualityPass(BitrateAudioQuality bitrateAudioQuality) {
        mBitrateAudioQuality = bitrateAudioQuality;
    }

    @Override
    public void onVideoQualityPass(VideoQuality videoQuality) {
        mVideoQuality = videoQuality;
    }

    private static final String TAG = "YTItemActivity";

    private ImageView mImageView;
    private TextView mTitle, mUploader, mDuration, mViews, mTimeUpload;

    private String url, title, duration, uploader, time_upload, thumbnail;
    private Long views;

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;
    Button downloadLater;


    private Button mButtonDownload;
    private ProgressBar progressBar;
    private TextView tvDownloadStatus;
    private ProgressBar pbLoading;

    private boolean downloading = false;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final DownloadProgressCallback mCallback = new DownloadProgressCallback() {
        @Override
        public void onProgressUpdate(float progress, long etaInSeconds) {
              runOnUiThread(() -> {
                  progressBar.setProgress((int) progress);
                  tvDownloadStatus.setText(String.valueOf(progress) + "% (ETA " + String.valueOf(etaInSeconds) + " seconds)");
            });
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytitem);

        mImageView = findViewById(R.id.Image);
        mTitle = findViewById(R.id.Title);
        mUploader = findViewById(R.id.Uploader);
        mDuration = findViewById(R.id.Duration);
        mViews = findViewById(R.id.Views);
        mTimeUpload = findViewById(R.id.Time_upload);


        getData();
        bindDataLayout();

        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager);

        adapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle());
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_music_note_24).setText("MP3/M4A"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_music_video_24).setText("MP4"));


        //move to the download later activity
        downloadLater=findViewById(R.id.downloadLater);
        downloadLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), DownloadLaterActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", title);
                intent.putExtra("thumbnail", thumbnail);
                intent.putExtra("duration", duration);
                intent.putExtra("views", views);
                intent.putExtra("uploader", uploader);
                intent.putExtra("time_upload", time_upload);
                startActivity(intent);
            }
        });
        //end of moving to download later activity
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 1) {
                    mFormat = Format.MP4;
                }
                else {
                    mFormat = Format.MP3;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        progressBar = findViewById(R.id.progress_bar);
        tvDownloadStatus = findViewById(R.id.tv_status);
        pbLoading = findViewById(R.id.pb_status);
        mButtonDownload = findViewById(R.id.button_download);
        mButtonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload();
            }
        });

    }

    private void startDownload() {
        if(downloading) {
            Toast.makeText(this,"cannot start downloading. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isStoragePermissionGranted()) {
            Toast.makeText(this, "grant storage permission and retry", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(url)) {
            return;
        }

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        File youtubeDlDir = getDownloadLocation();
        request.addOption("--no-mtime");
        request.addOption("-o", youtubeDlDir.getAbsolutePath() + "/%(title)s.%(ext)s");

        switch (mFormat) {
            case MP3:
                request.addOption("-f", "bestaudio");
                request.addOption("--extract-audio");
                request.addOption("--audio-format", "mp3");
                request.addOption("--audio-quality", mapBitRateQuality(mBitrateAudioQuality));
                break;
            case M4A:
                request.addOption("-f", 140); // STILL THE BEST OPTION TO GO M4A
                break;
            case MP4:
                request.addOption("-f", mapVideoQuality(mVideoQuality));
                break;

            default:
                break;
        }

        showStart();

        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, mCallback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    pbLoading.setVisibility(View.GONE);
                    progressBar.setProgress(100);
                    tvDownloadStatus.setText(getString(R.string.download_complete));
                    Toast.makeText(YTItemActivity.this, "download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                }, e -> {
                    if(BuildConfig.DEBUG) {
                        Log.e(TAG, "failed to download", e);
                    }
                    pbLoading.setVisibility(View.GONE);
                    tvDownloadStatus.setText(getString(R.string.download_failed));
                    Toast.makeText(YTItemActivity.this,"download failed", Toast.LENGTH_LONG).show();
                    downloading = false;
                });

        mCompositeDisposable.add(disposable);
    }

    private void showStart() {
        tvDownloadStatus.setText(getString(R.string.download_start));
        progressBar.setProgress(0);
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    private File getDownloadLocation() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public boolean isStoragePermissionGranted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    private String mapBitRateQuality(BitrateAudioQuality bitrateAudioQuality) {
        switch (bitrateAudioQuality) {
            case B64K:
                return "64K";
            case B128K:
                return "128K";
            case B192K:
                return "192K";
            case B256K:
                return "256K";
            case B320K:
                return "320K";

            default:
                return "320K";
        }
    }

    private int mapVideoQuality(VideoQuality videoQuality) {
        int optionCode = 18;
        switch (videoQuality) {
            case V640x360:
                optionCode = 18;
                break;
            case V1280x720:
                optionCode = 22;
                break;
            default:
                break;
        }
        return optionCode;
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("url") && intent.hasExtra("title") && intent.hasExtra("thumbnail") && intent.hasExtra("duration")
         && intent.hasExtra("views") && intent.hasExtra("uploader") && intent.hasExtra("time_upload")) {
            url = intent.getStringExtra("url");
            title = intent.getStringExtra("title");
            duration = intent.getStringExtra("duration");
            uploader = intent.getStringExtra("uploader");
            time_upload = intent.getStringExtra("time_upload");
            thumbnail = intent.getStringExtra("thumbnail");
            views = intent.getLongExtra("views", 1);
        } else {
            Toast.makeText(this, "No data sent", Toast.LENGTH_SHORT).show();
        }
    }
    private void bindDataLayout() {
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this));
        builder.build()
                .load(thumbnail)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(mImageView);
        mTitle.setText(title);
        mUploader.setText(uploader);
        mDuration.setText(duration);
        mTimeUpload.setText(time_upload);
        mViews.setText(CustomAdapter.updateViews((float)views));
    }
}
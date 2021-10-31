package com.steelparrot.freedecibel.network;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.steelparrot.freedecibel.BuildConfig;
import com.yausername.youtubedl_android.DownloadProgressCallback;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class YoutubeDLFactory {

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
    private static final String TAG = "YoutubeFactoryInstance";
    private static YoutubeDLFactory instance = null;

    private boolean downloading = false;
    private Format mFormat;
    private BitrateAudioQuality mBitrateAudioQuality=null;
    private String url;
    private CompositeDisposable mCompositeDisposable;

    private YoutubeDLFactory() { }

    public static YoutubeDLFactory getInstance(Format format, String url) {
        if(instance == null) {
            instance = new YoutubeDLFactory();
            instance.mFormat = format;
            instance.url = url;
            instance.downloading = false;
            instance.mCompositeDisposable = new CompositeDisposable();
        }
        return instance;
    }

    public void setFormat(Format newFormat) {
        instance.mFormat = newFormat;
    }

    public void setBitrateAudioQuality(BitrateAudioQuality newBitrateAudioQuality) {
        instance.mBitrateAudioQuality = newBitrateAudioQuality;
    }

    public static void destroyInstance() {
        if(instance != null) {
            instance.mCompositeDisposable.dispose();
            instance = null;
        }
    }

    private void addDisposable(Disposable disposable) {
        instance.mCompositeDisposable.add(disposable);
    }

    public boolean isDownloading() {
        return instance.downloading;
    }

    public void startDownload(Activity activity, DownloadProgressCallback callback) {
        if(isDownloading()) {
            Toast.makeText(activity,"cannot start downloading. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }
        if(!isStoragePermissionGranted(activity)) {
            Toast.makeText(activity, "grant storage permission and retry", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(url)) {
            return;
        }

        YoutubeDLRequest request = new YoutubeDLRequest(url);
        File youtubeDlDir = getDownloadLocation();
        request.addOption("--no-mtime");
        request.addOption("-o", youtubeDlDir.getAbsolutePath() + "/%(title)s.%(ext)s");
        if(instance.mFormat == Format.MP3) {
            request.addOption("-f", "bestaudio");
            request.addOption("--extract-audio");
            request.addOption("--audio-format", "mp3");
            request.addOption("--audio-quality", mapBitRateQuality(mBitrateAudioQuality));
        }
        else if(instance.mFormat == Format.M4A) {
            request.addOption("-f", 140); // STILL THE BEST OPTION TO GO M4A
        }
        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    Toast.makeText(activity, "download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                    destroyInstance();
                }, e -> {
                    if(BuildConfig.DEBUG) {
                        Log.e(TAG, "failed to download", e);
                    }
                    Toast.makeText(activity,"download failed", Toast.LENGTH_LONG).show();
                    downloading = false;
                });

        addDisposable(disposable);
    }

    @NonNull
    private File getDownloadLocation() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public boolean isStoragePermissionGranted(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

}

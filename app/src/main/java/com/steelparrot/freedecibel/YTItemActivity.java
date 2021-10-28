package com.steelparrot.freedecibel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class YTItemActivity extends AppCompatActivity {

//    Intent intent = new Intent(mContext, YTItemActivity.class);
//                intent.putExtra("url", currItem.getM_url());
//                intent.putExtra("title", currItem.getM_title());
//                intent.putExtra("thumbnail", R.id.thumbnailImage);
//                intent.putExtra("duration", currItem.getM_duration());
//                intent.putExtra("views", currItem.getM_views());
//                intent.putExtra("uploader", currItem.getM_uploader());
//                intent.putExtra("time_upload", currItem.getM_time_upload());

    private ImageView mImageView;
    private TextView mTitle, mUploader, mDuration, mViews, mTimeUpload;

    private String url, title, duration, uploader, time_upload, thumbnail;
    private int views;

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
            views = intent.getIntExtra("views", 1);
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
        mViews.setText(String.valueOf(views));
    }
}
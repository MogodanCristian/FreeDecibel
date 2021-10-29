package com.steelparrot.freedecibel.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.adapter.CustomAdapter;
import com.steelparrot.freedecibel.adapter.FragmentAdapter;

public class YTItemActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mTitle, mUploader, mDuration, mViews, mTimeUpload;

    private String url, title, duration, uploader, time_upload, thumbnail;
    private Long views;

    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;

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

        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("MP3"));
        tabLayout.addTab(tabLayout.newTab().setText("MP4"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
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
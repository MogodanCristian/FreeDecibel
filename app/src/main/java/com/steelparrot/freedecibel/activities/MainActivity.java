package com.steelparrot.freedecibel.activities;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.util.Log;
import android.widget.Toast;

import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.adapter.CustomAdapter;
import com.steelparrot.freedecibel.databinding.ActivityMainBinding;
import com.steelparrot.freedecibel.model.YTItem;
import com.steelparrot.freedecibel.network.GetDataService;
import com.steelparrot.freedecibel.network.RetrofitClientInstance;
import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;

import java.util.HashMap;
import java.util.List;

import io.reactivex.internal.operators.single.SingleDoAfterTerminate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private CustomAdapter mAdapter;
    ProgressDialog mProgressDialog;
    ActivityMainBinding binding;
    static int dark_light = 0;
    static int themeIconResId = R.drawable.ic_baseline_dark_mode_24;

    private static final String TAG = "YTItem_ytdl_init";

    private void searchYT(String query)
    {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

//                // create handle for RetrofitInstance interface
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("query", query);
        Call<List<YTItem>> call = service.getYTItems(map);
        call.enqueue(new Callback<List<YTItem>>() {
            @Override
            public void onResponse(Call<List<YTItem>> call, Response<List<YTItem>> response) {
                switch (response.code()) {
                    case 200:
                        binding.setIsData(true);
                        mProgressDialog.dismiss();
                        generateDataList(response.body());
                        break;
                    case 404:
                        binding.setIsData(false);
                        Toast.makeText(getApplicationContext(),"Couldn't find nothing on YT with these keywords!",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        binding.setIsData(false);
                        Toast.makeText(getApplicationContext(),"Server problem... Try again!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<YTItem>> call, Throwable t) {
                Log.d("retrofit1", t.toString());
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            YoutubeDL.getInstance().init(getApplication());
            FFmpeg.getInstance().init(getApplication());
        } catch (YoutubeDLException e) {
            Log.e(TAG, "failed to initialize youtubedl-android", e);
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setIsData(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (binding.getIsData()) {
            binding.setIsData(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu,menu);
        MenuItem searchItem=menu.findItem(R.id.nav_search);
        MenuItem darkTheme = menu.findItem(R.id.nav_dark);
        MenuItem downloadLater=menu.findItem(R.id.nav_download_later);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Write keywords here...");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setMaxWidth(android.R.attr.width);
            }
        });

        downloadLater.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent=new Intent(getApplicationContext(),DownloadLaterActivity.class);
                startActivity(intent);
                return true;
            }
        });
        darkTheme.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (dark_light % 2 == 0) {
                    dark_light++;
                    themeIconResId=R.drawable.ic_baseline_light_mode_24;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    dark_light++;
                    themeIconResId=R.drawable.ic_baseline_dark_mode_24;
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchYT(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.nav_search);
        MenuItem darkTheme = menu.findItem(R.id.nav_dark);
        darkTheme.setIcon(themeIconResId);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Write keywords here...");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setMaxWidth(android.R.attr.width);
            }

        });
        return super.onPrepareOptionsMenu(menu);
    }
    private void setItemsVisibility(Menu menu, MenuItem exception,boolean visibility)
    {
        for(int i=0; i<menu.size();i++)
        {
            MenuItem item=menu.getItem(i);
            if(item!=exception)
            {
                item.setVisible(visibility);
            }
        }
    }

    // method to generate list of data using RecyclerView with custom adapter
    private void generateDataList(List<YTItem> dataList) {
        mAdapter = new CustomAdapter(this, dataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        binding.customRecyclerView.setLayoutManager(layoutManager);
        binding.customRecyclerView.setAdapter(mAdapter);
    }
}
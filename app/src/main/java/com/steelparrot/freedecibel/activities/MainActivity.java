package com.steelparrot.freedecibel.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private CustomAdapter mAdapter;
    ProgressDialog mProgressDialog;
    ActivityMainBinding binding;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setIsData(false);
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu,menu);
        MenuItem searchItem=menu.findItem(R.id.nav_search);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Write keywords here...");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setMaxWidth(android.R.attr.width);
                setItemsVisibility(menu,searchItem,false);
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
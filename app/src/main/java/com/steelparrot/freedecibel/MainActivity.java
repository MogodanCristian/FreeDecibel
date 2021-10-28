package com.steelparrot.freedecibel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setIsData(false);
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.searchText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Insert your keywords first",Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.show();

//                // create handle for RetrofitInstance interface
                GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                HashMap<String, String> map = new HashMap<>();
                map.put("query", binding.searchText.getText().toString());
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
        });


    }

    // method to generate list of data using RecyclerView with custom adapter
    private void generateDataList(List<YTItem> dataList) {
        mAdapter = new CustomAdapter(this, dataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        binding.customRecyclerView.setLayoutManager(layoutManager);
        binding.customRecyclerView.setAdapter(mAdapter);
    }

}
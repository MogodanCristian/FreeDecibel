package com.steelparrot.freedecibel.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.steelparrot.freedecibel.R;
import com.steelparrot.freedecibel.adapter.DownloadLaterAdapter;
import com.steelparrot.freedecibel.database.DatabaseHelper;
import com.steelparrot.freedecibel.model.YTItem;

import java.util.ArrayList;

public class DownloadLaterActivity extends AppCompatActivity implements DownloadLaterAdapter.onMenuItemDataPass {

    RecyclerView mRecyclerView;
    DatabaseHelper mDatabaseHelper;
    ArrayList<YTItem> mItems;
    DownloadLaterAdapter mAdapter;
    public static MenuItem deleteOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_later);
        mRecyclerView=findViewById(R.id.downloadLaterRecycler);
        mDatabaseHelper=new DatabaseHelper(getApplicationContext());
        mItems=new ArrayList<>();
        getData();
        storeData();

        mAdapter=new DownloadLaterAdapter(this,mItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_later_menu,menu);
        MenuItem deleteAll=menu.findItem(R.id.deleteAll);
        deleteOne=menu.findItem(R.id.deleteOne);
        deleteAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mDatabaseHelper.deleteAll();
               /* Intent intent=new Intent(getApplicationContext(), DownloadLaterActivity.class);
                startActivity(intent);*/
                mItems.clear();
                mAdapter.setItemsList(mItems);
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void storeData()
    {
        Cursor cursor=mDatabaseHelper.selectAllData();
        if(cursor.getCount() == 0)
        {
            Toast.makeText(this,"No data my boi",Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(cursor.moveToNext())
            {
                YTItem aux=new YTItem();
                aux.setM_title(cursor.getString(1));
                aux.setM_uploader(cursor.getString(2));
                aux.setM_duration(cursor.getString(3));
                aux.setM_views(cursor.getLong(4));
                aux.setM_time_upload(cursor.getString(5));
                aux.setM_url(cursor.getString(6));
                aux.setM_thumbnail(cursor.getString(7));
                mItems.add(aux);
            }
        }
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("url") && intent.hasExtra("title") && intent.hasExtra("thumbnail") && intent.hasExtra("duration")
                && intent.hasExtra("views") && intent.hasExtra("uploader") && intent.hasExtra("time_upload")) {
            DatabaseHelper db=new DatabaseHelper(getApplicationContext());
            ArrayList<String> url_aux=new ArrayList<>();
            url_aux=db.selectAllURLS();
            boolean is_duplicate=false;
            for(int i=0;i<url_aux.size();i++)
            {
                if(url_aux.get(i).equals(intent.getStringExtra("url")))
                {
                    is_duplicate=true;
                }
            }
            if(is_duplicate==false)
            {
                db.insertYTItem(intent.getStringExtra("title"),
                    intent.getStringExtra("uploader"),
                    intent.getStringExtra("duration"),
                    intent.getLongExtra("views",1),
                    intent.getStringExtra("time_upload"),
                    intent.getStringExtra("url"),
                    intent.getStringExtra("thumbnail"));
        }}
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    @Override
    public void onMenuItemsDeleted(ArrayList<Integer> positions) {
        for(int i=0;i<positions.size();i++)
        {
            int pos = positions.get(i);
            mDatabaseHelper.deleteItem(mItems.get(pos));
            mItems.remove(pos);
        }
        mAdapter.setItemsList(mItems);
        mAdapter.notifyDataSetChanged();
    }
}
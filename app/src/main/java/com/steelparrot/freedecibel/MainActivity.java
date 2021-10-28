package com.steelparrot.freedecibel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
                searchView.clearFocus();
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
}
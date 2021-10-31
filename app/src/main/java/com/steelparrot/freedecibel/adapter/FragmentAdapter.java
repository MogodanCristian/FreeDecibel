package com.steelparrot.freedecibel.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.steelparrot.freedecibel.fragments.MP3;
import com.steelparrot.freedecibel.fragments.MP4;

public class FragmentAdapter extends FragmentStateAdapter {

    private String url;

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, String url) {
        super(fragmentActivity);
        this.url = url;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return MP4.newInstance(url);
        } else {
            return MP3.newInstance(url);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

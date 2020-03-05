package com.example.acadboost.Adapter;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CourseViewPagerAdapter extends FragmentPagerAdapter {

    private  List<Fragment> fragmentList = new ArrayList<>();
    private  List<String> tabTitles = new ArrayList<>();

    public CourseViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment f, String title) {
        fragmentList.add(f);
        tabTitles.add(title);
    }
}

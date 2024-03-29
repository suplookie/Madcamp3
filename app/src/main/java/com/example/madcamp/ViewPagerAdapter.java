package com.example.madcamp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) { // 표시할 Fragment
        switch (position) {
            case 0:
                return FirstFragment.newInstance(); // contact
            case 1:
                return SecondFragment.newInstance(); // images
            case 2:
                return ThirdFragment.newInstance(); // map
            default:
                return null;
        }
    }

    @Override
    public int getCount() { // Tab 의 갯수
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Contact";
            case 1:
                return "Images";
            case 2:
                return "Map";
        }
        return null;
    }
}
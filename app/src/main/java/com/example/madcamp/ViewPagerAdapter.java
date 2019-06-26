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
                return FirstFragment.newInstance(); // 빨
            case 1:
                return SecondFragment.newInstance(); // 초
            case 2:
                return ThirdFragment.newInstance(); // 파
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
                return "Mon";
            case 1:
                return "Tue";
            case 2:
                return "Wed";
        }
        return null;
    }
}
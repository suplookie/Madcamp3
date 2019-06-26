package com.example.madcamp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class SecondFragment extends Fragment {

    public static SecondFragment newInstance() {
        Bundle args = new Bundle();
        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(args);
        return new SecondFragment();
    }
}
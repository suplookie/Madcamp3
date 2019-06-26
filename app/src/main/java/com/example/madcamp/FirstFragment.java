package com.example.madcamp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class FirstFragment extends Fragment {

    public static FirstFragment newInstance() {
        Bundle args = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return new FirstFragment();
    }
}
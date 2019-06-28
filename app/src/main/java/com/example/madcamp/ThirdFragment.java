package com.example.madcamp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class ThirdFragment extends Fragment {

    public static ThirdFragment newInstance() {
        Bundle args = new Bundle();
        ThirdFragment fragment = new ThirdFragment();
        fragment.setArguments(args);
        return new ThirdFragment();
    }


}
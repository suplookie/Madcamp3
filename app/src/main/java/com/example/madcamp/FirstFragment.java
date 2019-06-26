package com.example.madcamp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;

public class FirstFragment extends Fragment {

    public static FirstFragment newInstance() {
        Bundle args = new Bundle();
        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(args);
        return new FirstFragment();
    }


    private static final String TAG = "MainActivity";

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:started.");

        View rootView = inflater.inflate(R.layout.firstfragment, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerv_view);

        initImageBitmaps();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mImageUrls);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    private void initImageBitmaps(){
        Log.d(TAG, "iniImageBitmaps : preparing bitmaps.");


        mImageUrls.add("https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
        mNames.add("Kevin");

        mImageUrls.add("https://i.redd.it/228zdgyagn631.jpg");
        mNames.add("Sam");

        mImageUrls.add("https://i.redd.it/t7eaiwb0xl631.jpg");
        mNames.add("Bojun");

        mImageUrls.add("https://i.redd.it/raiqbbac1h631.jpg");
        mNames.add("Joseph");

        mImageUrls.add("https://i.redd.it/t7eaiwb0xl631.jpg");
        mNames.add("James");

        mImageUrls.add("https://i.redd.it/raiqbbac1h631.jpg");
        mNames.add("James");

        mImageUrls.add("https://i.redd.it/t7eaiwb0xl631.jpg");
        mNames.add("James");

        mImageUrls.add("https://i.redd.it/t7eaiwb0xl631.jpg");
        mNames.add("James");


    }
}



package com.example.madcamp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

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
    private ArrayList<Bitmap> mImage = new ArrayList<Bitmap>();
    private ArrayList<String> mPhoneNo = new ArrayList<>();

    FloatingActionButton menu;
    FloatingActionButton read_contacts;
    FloatingActionButton add_contacts;
    boolean isMenuOpen = false;
    boolean isContactRead = false;

    private String StopClickingReadContact = "Contact has been already loaded";



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreate:started.");

        final View rootView = inflater.inflate(R.layout.firstfragment, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerv_view);

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mImage, mPhoneNo);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        menu = rootView.findViewById(R.id.menu);
        read_contacts = rootView.findViewById(R.id.read_contact);
        add_contacts = rootView.findViewById(R.id.add_contact);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOpen();
            }
        });

        read_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isContactRead) {
                    (adapter).notifyDataSetChanged();
                    LoadContacts();
                    isContactRead = true;
                } else {
                    Toast.makeText(getActivity(), StopClickingReadContact, Toast.LENGTH_SHORT).show();
                }
            }
        });

        add_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                startActivity(intent);
                isContactRead = false;

                (adapter).notifyDataSetChanged();
            }
        });

        return rootView;
    }


    private void LoadContacts() {
        Log.d(TAG, "iniImageBitmaps : preparing bitmaps.");

        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            mNames.add(name);

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mPhoneNo.add(phoneNumber);
            }

            Bitmap photo = BitmapFactory.decodeResource(getActivity().getResources(), R.id.image);

            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }
            mImage.add(photo);
        }
    }


    private void menuOpen() {
        if (!isMenuOpen) {
            add_contacts.animate().translationY(-getResources().getDimension(R.dimen.add_contacts));
            read_contacts.animate().translationY(-getResources().getDimension(R.dimen.read_contacts));

            isMenuOpen = true;
        } else {
            add_contacts.animate().translationY(0);
            read_contacts.animate().translationY(0);

            isMenuOpen = false;
        }
    }






}



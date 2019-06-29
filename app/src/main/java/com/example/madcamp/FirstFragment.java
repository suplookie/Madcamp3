package com.example.madcamp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
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
    private ArrayList<Bitmap> mImage = new ArrayList<Bitmap>();
    private ArrayList<String> mPhoneNo = new ArrayList<>();
    private ArrayList<String> mLocation = new ArrayList<>();

    FloatingActionButton menu;
    FloatingActionButton read_contacts;
    FloatingActionButton add_contacts;
    FloatingActionButton refresh;
    boolean isMenuOpen = false;
    boolean isContactRead = false;

    private String StopClickingReadContact = "Contact has been already loaded";

    private ImageView image;
    private Button buttonImage;
    private EditText intentName, intentPhoneNo;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreate:started.");

        final View rootView = inflater.inflate(R.layout.firstfragment, container, false);

        final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerv_view);

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mImage, mPhoneNo, mLocation);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        isContactRead = false;

        menu = rootView.findViewById(R.id.menu);
        read_contacts = rootView.findViewById(R.id.read_contact);
        add_contacts = rootView.findViewById(R.id.add_contact);
        refresh = rootView.findViewById(R.id.refresh);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOpen();
            }
        });

        read_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(!isContactRead){
                    LoadContacts();

                    final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerv_view);

                    final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mImage, mPhoneNo, mLocation);

                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    isContactRead = true;

                }else{
                    Toast.makeText(getActivity(),StopClickingReadContact,Toast.LENGTH_SHORT).show();
                }
            }
        });

        add_contacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                startActivity(intent);

                isContactRead = false;
            }
        });

        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mImage.clear();
                mPhoneNo.clear();
                mNames.clear();

                final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerv_view);

                final RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), mNames, mImage, mPhoneNo, mLocation);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                isContactRead = false;
            }
        });


        return rootView;
    }


    private void LoadContacts(){

        mNames.clear();
        mPhoneNo.clear();
        mImage.clear();
        mLocation.clear();

        Log.d(TAG, "iniImageBitmaps : preparing bitmaps.");

        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);

        while(cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            mNames.add(name);

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id},null);

            while (phoneCursor.moveToNext()){
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


            Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            Cursor postal_cursor  = getActivity().getContentResolver().query(postal_uri,null,  ContactsContract.Data.CONTACT_ID + "="+ id.toString(), null,null);
            while(postal_cursor.moveToNext())
            {
                String Strt = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String Cty = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String cntry = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                mLocation.add(Strt+Cty+cntry);
            }


        }
    }


    private void menuOpen(){
        if(!isMenuOpen){
            add_contacts.animate().translationY(-getResources().getDimension(R.dimen.add_contact));
            read_contacts.animate().translationY(-getResources().getDimension(R.dimen.read_contacts));
            refresh.animate().translationX(-getResources().getDimension(R.dimen.refresh));

            isMenuOpen = true;
        } else {
            add_contacts.animate().translationY(0);
            read_contacts.animate().translationY(0);
            refresh.animate().translationX(0);

            isMenuOpen = false;
        }
    }



}



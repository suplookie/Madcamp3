package com.example.madcamp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ThirdFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    public static GoogleMap map;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Bitmap> mImage = new ArrayList<Bitmap>();
    private ArrayList<String> mPhoneNo = new ArrayList<>();
    private ArrayList<String> mLocation = new ArrayList<>();

    public static ThirdFragment newInstance() {
        Bundle args = new Bundle();
        ThirdFragment fragment = new ThirdFragment();
        fragment.setArguments(args);
        return new ThirdFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
    }

    FloatingActionButton fab;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.thirdfragment, container, false);

        fab = rootView.findViewById(R.id.fab_refresh);
        LoadContacts();




        MapsInitializer.initialize(getActivity());
        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(final GoogleMap map) {

        LatLng SEOUL = new LatLng(37.56, 126.97);


        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.list != null && MainActivity.coords != null && MainActivity.coords.size() != 0) {
                    for (int i = 0; i < MainActivity.list.size(); i++) {
                        if (MainActivity.coords.get(i).valid) {
                            LatLng latLng = MainActivity.coords.get(i).getLatLng();
                            Uri uri = MainActivity.list.get(i);
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 150, 150, false);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                    .position(latLng);

                            map.addMarker(markerOptions);
                        }

                    }
                }
                else Toast.makeText(getActivity(), "bundle null", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
/*
    private void getLocate(){
        String searchString = ;

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TedPermission.TAG, "geoLocate : IOEXCEPTION : "+e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TedPermission.TAG, "geoLocate: found a location : " + address.toString());
        }
    }
*/
    private void LoadContacts() {

        mNames.clear();
        mPhoneNo.clear();
        mImage.clear();
        mLocation.clear();

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

            Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            Cursor postal_cursor = getActivity().getContentResolver().query(postal_uri, null, ContactsContract.Data.CONTACT_ID + "=" + id.toString(), null, null);
            while (postal_cursor.moveToNext()) {
                String Strt = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String Cty = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String cntry = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                mLocation.add(Strt + Cty + cntry);
            }
        }
    }
}
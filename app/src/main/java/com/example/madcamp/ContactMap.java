package com.example.madcamp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gun0912.tedpermission.TedPermission.TAG;

public class ContactMap extends Activity implements OnMapReadyCallback {



    public void onDetach() {
        onDetach();
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
    }

    private MapView mapView;
    private GoogleMap map;
    private EditText mSearchText;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contactmap);
        mSearchText = (EditText) findViewById(R.id.input_search);

        String contactLocation;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                contactLocation= null;
            } else {
                contactLocation= extras.getString("CONTACT_LOCATION");
            }
        } else {
            contactLocation = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }
        contactLocation = contactLocation.substring(0,contactLocation.length()-8);
        mSearchText.setText(contactLocation);


        MapsInitializer.initialize(this);
        mapView = findViewById(R.id.contactMap);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        init();
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);

        map.addMarker(options);
    }

    private void init(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    getLocate();

                }
                return false;
            }
        });
    }

    private void getLocate(){
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(ContactMap.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate : IOEXCEPTION : "+e.getMessage());
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location : " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 10, address.getAddressLine(0));
        }
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
}

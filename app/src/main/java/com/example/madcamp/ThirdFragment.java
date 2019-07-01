package com.example.madcamp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
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
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();

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

                            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                    .position(latLng);

                            map.addMarker(markerOptions);
                        }
                    }
                }

                if (mImage != null && mLocation != null && mLocation.size() != 0) {
                    for (int i = 0; i < mImage.size(); i++) {
                        String locationName = mLocation.get(i);
                        LatLng latLng = new LatLng(getLocate(locationName).getLatitude(), getLocate(locationName).getLongitude());
                        Bitmap bitmap = null;
                        bitmap = mImage.get(i);
                        Bitmap circleBitmap = getCroppedBitmap(bitmap);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions
                                .icon(BitmapDescriptorFactory.fromBitmap(circleBitmap))
                                .position(latLng);
                        map.addMarker(markerOptions);
                    }
                }
            }
        });
    }

    private Address getLocate(String locationName){
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(locationName, 1);
        }catch (IOException e){
            Log.e(TedPermission.TAG, "geoLocate : IOEXCEPTION : "+e.getMessage());
        }
        Address address = list.get(0);
        return address;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
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


            if (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mPhoneNo.add(phoneNumber);
            }else {
                mPhoneNo.add("NO PHONE NUMBER ADDED");
            }



            Bitmap photo = BitmapFactory.decodeResource(getActivity().getResources(), R.id.image);

            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                mImage.add(photo);
            }else{
                photo = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sharp_person_outline_black_48dp);
                mImage.add(Bitmap.createScaledBitmap(photo,100,100,false));
            }



            Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            Cursor postal_cursor  = getActivity().getContentResolver().query(postal_uri,null,  ContactsContract.Data.CONTACT_ID + "="+ id.toString(), null,null);

            if (postal_cursor.moveToNext()) {
                String Strt = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                String Cty = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                String cntry = postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                mLocation.add(Strt + Cty + cntry);
            } else {
                mLocation.add("Antarctica");
            }
        }

    }
}
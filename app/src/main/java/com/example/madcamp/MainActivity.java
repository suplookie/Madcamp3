package com.example.madcamp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Uri> list;
    public static ArrayList<MapCoord> coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabs= findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                changeView(pos);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    private void changeView(int index) {
        FloatingActionButton fab = findViewById(R.id.fab);
        switch (index) {
            case 0 :
                fab.hide();
                break ;
            case 1 :
                fab.show();
                break ;
            case 2 :
                fab.hide();

                if (MainActivity.list != null && MainActivity.coords != null && MainActivity.coords.size() != 0 && ThirdFragment.map != null) {
                    for (int i = 0; i < MainActivity.list.size(); i++) {
                        if (MainActivity.coords.get(i).valid) {
                            LatLng latLng = MainActivity.coords.get(i).getLatLng();
                            Uri uri = MainActivity.list.get(i);
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 150, 150, false);

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions
                                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                    .position(latLng);

                            ThirdFragment.map.addMarker(markerOptions);
                        }

                    }
                    //}
                }



                break ;
        }
    }

}

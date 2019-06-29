package com.example.madcamp;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImgActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.img_activity);


        ImageView imageview = findViewById(R.id.imageview);



        Uri uri = getIntent().getParcelableExtra("imgUri");

        imageview.setImageURI(uri);
    }
}


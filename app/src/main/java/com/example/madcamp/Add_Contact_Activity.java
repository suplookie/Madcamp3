package com.example.madcamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class Add_Contact_Activity extends AppCompatActivity {

    private ImageView image;
    private Button uploadPhoto;
    private Button addContract;
    private EditText intentName, intentPhoneNo;

    private Spinner spinner_money_min, spinner_money_max;
    private ImageView intentPhoto;
    private Uri imgUri, photoURI, albumURI;
    private String mCurrentPhotoPath;
    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_REQUEST_CODE = 1;

    private Context mContext;

    private Activity activity;

    public void onAttach(Context context) {
        onAttach(context);
        mContext = context;

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__contact_);
/*
        uploadPhoto = findViewById(R.id.uploadPhoto);
        uploadPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tedPermission();
                makeDialog();
            }
        });
*/
        addContract = findViewById(R.id.add_contact);
        addContract.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                intentName = findViewById(R.id.intentName);
                intentPhoneNo =findViewById(R.id.intentPhoneNo);

                intent
                        .putExtra(ContactsContract.Intents.Insert.NAME, intentName.getText())
                        .putExtra(ContactsContract.Intents.Insert.PHONE, intentPhoneNo.getText());

                startActivity(intent);
            }
        });
    }

    private void tedPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                Toast.makeText(Add_Contact_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(Add_Contact_Activity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission] ")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    private void makeDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        alt_bld.setTitle("사진 업로드").setIcon(R.drawable.baseline_camera_black_18dp).setCancelable(
                false).setPositiveButton("사진촬영",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 사진촬영 선택");
                        // 사진 촬영 클릭
                        takePhoto();
                    }
                }).setNeutralButton("앨범선택",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Log.v("알림", "다이얼로그 > 앨범선택 선택");
                        //앨범에서 선택
                        selectAlbum();
                    }
                }).setNegativeButton("취소   ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 취소 선택");
                        // 취소 클릭. dialog 닫기.
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    public void selectAlbum(){
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    //사진 찍기 클릭
    public void takePhoto(){
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(this.getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile= createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    imgUri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case GALLERY_INTENT : {
                //앨범에서 가져오기
                if(data.getData()!=null){
                    try{
                        File albumFile = null;
                        albumFile = createImageFile();
                        Uri photoURI = data.getData();
                        Uri albumURI = Uri.fromFile(albumFile);
                        galleryAddPic();
//이미지뷰에 이미지 셋팅
                        intentPhoto.setImageURI(photoURI);
                        //cropImage();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.v("알림","앨범에서 가져오기 에러");
                    }
                }
                break;
            }
            case CAMERA_REQUEST_CODE : {
                //촬영
                try{
                    Log.v("알림", "FROM_CAMERA 처리");
                    galleryAddPic();
//이미지뷰에 이미지셋팅
                    intentPhoto.setImageURI(imgUri);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public File createImageFile() throws IOException{
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile= null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ireh");
        if(!storageDir.exists()){
            Log.v("알림","storageDir 존재 x " + storageDir.toString());
            storageDir.mkdirs();
        }
        Log.v("알림","storageDir 존재함 " + storageDir.toString());
        imageFile = new File(storageDir,imgFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    public void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Toast.makeText(this,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }
}

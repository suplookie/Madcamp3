package com.example.madcamp;

        import android.Manifest;
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.media.ExifInterface;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.core.content.FileProvider;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;
        import androidx.viewpager.widget.ViewPager;

        import com.google.android.material.appbar.AppBarLayout;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;
        import com.gun0912.tedpermission.PermissionListener;
        import com.gun0912.tedpermission.TedPermission;

        import java.io.File;
        import java.io.IOException;
        import java.io.InputStream;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Objects;

public class SecondFragment extends Fragment {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA= 2;
    private ArrayList<Uri> list;
    private ArrayList<MapCoord> coords;
    private String mCurrentPhotoPath;
    private FloatingActionButton fab_img;
    private FloatingActionButton fab_cam;
    private CardAdapter adapter;
    private boolean isMenuOpen = false;
    private Uri imgUri;

    public static SecondFragment newInstance() {
        Bundle args = new Bundle();
        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private Context mContext;

    private Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        tedPermission();

        list = new ArrayList<>();
        coords = new ArrayList<>();


        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        final RecyclerView recyclerView = view.findViewById(R.id.recycle2) ;
        //recyclerView.setLayoutManager(new LinearLayoutManager(activity)) ;


        GridLayoutManager mGridLayoutManager;
        int cols = 3;
        mGridLayoutManager = new GridLayoutManager(mContext, cols);
        recyclerView.setLayoutManager(mGridLayoutManager);

        // 리사이클러뷰에 CardAdapter 객체 지정.
        adapter = new CardAdapter(list, coords) ;

        AppBarLayout appBar = activity.findViewById(R.id.appbar);
        appBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClose();
            }
        });




        //fab click시 앨범에서 이미지 가져와 리스트에 추가
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab_img = view.findViewById(R.id.fab_img);
        fab_cam = view.findViewById(R.id.fab_cam);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuOpen();
            }
        });

        fab_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAlbum();
                (adapter).notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
                menuClose();
            }
        });

        fab_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                (adapter).notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
                menuClose();
            }
        });

        //리사이클러뷰에 CardAdapter 객체 지정
        recyclerView.setAdapter(adapter) ;


        return view;

    }



    private void takePhoto(){
        // 촬영 후 이미지 가져옴
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(activity.getPackageManager())!=null){
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(photoFile!=null){
                    Uri providerURI = FileProvider.getUriForFile(mContext,activity.getPackageName(),photoFile);
                    imgUri = providerURI;
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            }
        }else{
            Log.v("알림", "저장공간에 접근 불가능");
        }
    }

    private void selectAlbum(){
        //앨범 열기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        switch (requestCode){
            case PICK_FROM_ALBUM : {
                //앨범에서 가져오기
                if(data.getData()!=null){
                    try{
                        Uri photoURI = data.getData();

                        //이미지뷰에 이미지 셋팅
                        if (!list.add(photoURI))
                            Toast.makeText(activity, "list add failed", Toast.LENGTH_SHORT).show();
                        MapCoord coord = new MapCoord();
                        InputStream in = activity.getContentResolver().openInputStream(photoURI);
                        ExifInterface exif = new ExifInterface(in);

                        String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);

                        if (latitude != null && longitude != null) {
                            coord.valid = true;
                            if (exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF).equals("E"))
                                coord.longitude = convertToDegree(longitude);
                            else
                                coord.longitude = 0 - convertToDegree(longitude);

                            if (exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF).equals("N"))
                                coord.latitude = convertToDegree(latitude);
                            else
                                coord.latitude = 0 - convertToDegree(latitude);
                        }else {
                            //open google map,
                        }

                        coords.add(coord);
                        adapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.v("알림","앨범에서 가져오기 에러");
                    }
                }
                break;
            }
            case PICK_FROM_CAMERA: {
                //촬영
                try{
                    Log.v("알림", "FROM_CAMERA 처리");
                    galleryAddPic();
                //이미지뷰에 이미지셋팅
                    if (!list.add(imgUri))
                        Toast.makeText(activity, "list add failed", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Objects.requireNonNull(activity).sendBroadcast(mediaScanIntent);
        Toast.makeText(mContext,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException {
        String imgFileName = System.currentTimeMillis() + ".jpg";
        File imageFile;
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

    private void menuOpen(){
        if(!isMenuOpen){
            fab_img.animate().translationY(-getResources().getDimension(R.dimen.add_contacts));
            fab_cam.animate().translationY(-getResources().getDimension(R.dimen.read_contacts));
            isMenuOpen = true;
        } else {
            fab_img.animate().translationY(0);
            fab_cam.animate().translationY(0);

            isMenuOpen = false;
        }
    }

    private void menuClose(){
        fab_img.animate().translationY(0);
        fab_cam.animate().translationY(0);

        isMenuOpen = false;
    }


    private void tedPermission() {

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(activity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        TedPermission.with(Objects.requireNonNull(mContext))
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission] ")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }

    private Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

        result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

        return result;


    };



}
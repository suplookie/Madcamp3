package com.example.madcamp;

        import android.Manifest;
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.core.content.FileProvider;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.google.android.material.floatingactionbutton.FloatingActionButton;
        import com.gun0912.tedpermission.PermissionListener;
        import com.gun0912.tedpermission.TedPermission;

        import java.io.File;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Objects;

public class SecondFragment extends Fragment {

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA= 2;
    private ArrayList<Uri> list, col1, col2, col3;
    private String mCurrentPhotoPath;
    private ImageView img1;
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
        View cardview = inflater.inflate(R.layout.card, container, false);
        tedPermission();

        list = new ArrayList<>();
        col1 = new ArrayList<>();
        col2 = new ArrayList<>();
        col3 = new ArrayList<>();
        /*
        for(int i = 0; i < 3; i++) {
            list.add(0);
        }*/

        img1 = cardview.findViewById(R.id.card_image);
        //img1 = view.findViewById(R.id.android);

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        final RecyclerView recyclerView = view.findViewById(R.id.recycle2) ;
        //recyclerView.setLayoutManager(new LinearLayoutManager(activity)) ;


        GridLayoutManager mGridLayoutManager;
        int cols = 3;
        mGridLayoutManager = new GridLayoutManager(mContext, cols);
        recyclerView.setLayoutManager(mGridLayoutManager);

        // 리사이클러뷰에 CardAdapter 객체 지정.
        adapter = new CardAdapter(list) ;


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

    public void takePhoto(){
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
                        img1.setImageURI(photoURI);
                        if (!list.add(photoURI))
                            Toast.makeText(activity, "list add failed", Toast.LENGTH_SHORT).show();
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
                    img1.setImageURI(imgUri);
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

    public void galleryAddPic(){

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        File f = new File(mCurrentPhotoPath);

        Uri contentUri = Uri.fromFile(f);

        mediaScanIntent.setData(contentUri);

        Objects.requireNonNull(activity).sendBroadcast(mediaScanIntent);

        Toast.makeText(mContext,"사진이 저장되었습니다",Toast.LENGTH_SHORT).show();

    }

    public File createImageFile() throws IOException {
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


}
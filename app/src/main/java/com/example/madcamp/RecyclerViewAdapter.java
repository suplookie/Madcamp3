package com.example.madcamp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private ArrayList<String> mPhoneNo = new ArrayList<>();
    static ArrayList<String> mLocation = new ArrayList<>();
    private Context mContext;
    int i = 0;


    public RecyclerViewAdapter(Context Context, ArrayList<String> ImageNames, ArrayList<Bitmap> Images, ArrayList<String> PhoneNo, ArrayList<String> Location) {
        this.mImageNames = ImageNames;
        this.mImages = Images;
        this.mContext = Context;
        this.mPhoneNo = PhoneNo;
        this.mLocation = Location;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
            Log.d(TAG, "onBindViewHolder: called.");

            holder.getAdapterPosition();

            Glide.with(mContext)
                    .asBitmap()
                    .load(mImages.get(position))
                    .into(holder.image);
            holder.imageName.setText(mImageNames.get(position));
            if (holder.imageName.getText().length() > 13){
                holder.imageName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
            holder.phoneNo.setText(mPhoneNo.get(position));
            if (mPhoneNo.get(position) == "ADD PHONE NUMBER") {
                holder.phoneNo.setTextColor(ContextCompat.getColor(mContext, R.color.no_number));
                holder.phoneNo.setAlpha(0.2f);
            }

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    i++;

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (i==1){
                                Log.d(TAG, "onClick : clicked on : " + mLocation.get(position));

                                if(mLocation.get(position) == "NO ADDRESS ADDED") {
                                    Toast.makeText(mContext, mLocation.get(position), LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(mContext, mLocation.get(position).substring(0, mLocation.get(position).length() - 8), LENGTH_SHORT).show();
                                    openContactMaps(view, mLocation, position);
                                }
                            }else if (i==2){
                                Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(mPhoneNo.get(position)));
                                String[] projection = new String[]{ ContactsContract.PhoneLookup._ID };

                                Cursor cur = mContext.getContentResolver().query(uri, projection, null, null, null);

                                if (cur != null && cur.moveToNext()) {
                                    Long id = cur.getLong(0);

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id));
                                    intent.setData(contactUri);
                                    mContext.startActivity(intent);

                                    cur.close();
                                }
                            }
                            i=0;
                        }
                    }, 200);
                }
            });

    }


    public void openContactMaps(View view, ArrayList<String> mLocation, int position){
        String location = mLocation.get(position);
        Intent intent = new Intent(view.getContext(), ContactMap.class);
        intent.putExtra("CONTACT_LOCATION", location);
        view.getContext().startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView imageName;
        TextView phoneNo;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            phoneNo = itemView.findViewById(R.id.phone_no);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}

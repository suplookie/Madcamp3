package com.example.madcamp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private ArrayList<String> mPhoneNo = new ArrayList<>();
    static ArrayList<String> mLocation = new ArrayList<>();
    private Context mContext;


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
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Log.d(TAG, "onClick : clicked on : " + mLocation.get(position));

                    Toast.makeText(mContext, mLocation.get(position).substring(0, mLocation.get(position).length()-8), Toast.LENGTH_SHORT).show();

                    openContactMaps(view, mLocation, position);

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

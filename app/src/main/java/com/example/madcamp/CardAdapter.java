package com.example.madcamp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<Uri> mData;


    //생성자
    CardAdapter(ArrayList<Uri> list)
    {
        this.mData = list ;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;

        ViewHolder(View itemView) {
            super(itemView);

            imageView1 = itemView.findViewById(R.id.card_image);
        }
    }

    @NonNull
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.card, parent, false) ;
        CardAdapter.ViewHolder vh = new CardAdapter.ViewHolder(view) ;



        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.ViewHolder holder, int position) {
        //String text = mData.get(position) ;
        //holder.imageView1.setText(text) ;
        holder.imageView1.setImageURI(this.mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }

}


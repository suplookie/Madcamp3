package com.example.madcamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<Integer> mData = null ;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView1;

        ViewHolder(View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.iv_photo);
        }
    }
    CardAdapter(ArrayList<Integer> list) {
        mData = list ;
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
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}

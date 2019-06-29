package com.example.madcamp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<Uri> mData;
    private ArrayList<MapCoord> coords;
    Context context;

    //생성자
    CardAdapter(ArrayList<Uri> list, ArrayList<MapCoord> coordList)
    {
        this.mData = list ;
        this.coords = coordList;

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        Card card;

        ViewHolder(View itemView) {
            super(itemView);
            card = new Card();

            card.img = itemView.findViewById(R.id.card_image);
            card.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (coords.get(card.index).valid)
                        Toast.makeText(context, coords.get(card.index).getLatLng().latitude + " " + coords.get(card.index).getLatLng().longitude, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, ImgActivity.class);

                    intent.putExtra("imgUri", mData.get(card.index));

                    context.startActivity(intent);

                }
            });
        }

    }


    @NonNull
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.card, parent, false) ;



        return new CardAdapter.ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.ViewHolder holder, int position) {
        holder.card.img.setImageURI(this.mData.get(position));
        holder.card.index = position;
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    private class Card {
        ImageView img;
        int index;
    }

}



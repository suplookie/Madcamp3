package com.example.madcamp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<Uri> mData;
    Context context;

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
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = ((BitmapDrawable)imageView1.getDrawable()).getBitmap();
                    float scale = (1024/(float)bitmap.getWidth());
                    int image_w = (int) (bitmap.getWidth() * scale);
                    int image_h = (int) (bitmap.getHeight() * scale);
                    Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                    resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(context, ImgActivity.class);

                    intent.putExtra("string", "intent 연습");
                    intent.putExtra("integer", 300);
                    intent.putExtra("double", 3.141592 );
                    intent.putExtra("image", byteArray);

                    context.startActivity(intent);

                    //imageView1.setImageDrawable(view.getResources().getDrawable(R.drawable.android));
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
        View cardview = inflater.inflate(R.layout.fragment_second, parent, false);



        return new CardAdapter.ViewHolder(view) ;
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


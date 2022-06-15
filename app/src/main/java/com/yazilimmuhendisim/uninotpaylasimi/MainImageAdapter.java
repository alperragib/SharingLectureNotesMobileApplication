package com.yazilimmuhendisim.uninotpaylasimi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MainImageAdapter extends RecyclerView.Adapter<MainImageAdapter.ImageTutucu>{
    private Context context;
    private List<String> image;

    public MainImageAdapter(Context context, List<String> image) {
        this.context = context;
        this.image = image;
    }



    public class ImageTutucu extends RecyclerView.ViewHolder{
        private ImageView imageView;


        public ImageTutucu(View view){
            super(view);
            imageView = view.findViewById(R.id.main_image_tasarim_id);

        }

    }

    @NonNull
    @Override
    public ImageTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_image_tasarim,parent,false);
        return new ImageTutucu(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageTutucu holder, final int position) {

        final String url = image.get(position);

        Picasso.with(context).load(url)
                .placeholder(R.drawable.loading)
                .error(R.drawable.loading)
                .into(holder.imageView,new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentImage = new Intent(context.getApplicationContext(),FullImageActivity.class);

                intentImage.putExtra("url",url);

                context.startActivity(intentImage);


            }
        });
    }

    @Override
    public int getItemCount() {
        return image.size();
    }


}

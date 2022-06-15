package com.yazilimmuhendisim.uninotpaylasimi;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageTutucu>{
    private Context context;
    private List<String> image;

    public ImageAdapter(Context context, List<String> image) {
        this.context = context;
        this.image = image;
    }



    public class ImageTutucu extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private Button button;

        public ImageTutucu(View view){
            super(view);
            imageView = view.findViewById(R.id.image_tasarim_id);
            button = view.findViewById(R.id.remove_button1);
        }

    }

    @NonNull
    @Override
    public ImageTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_tasarim,parent,false);
        return new ImageTutucu(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageTutucu holder, final int position) {

        final String bitmap = image.get(position);

        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(bitmap));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentImage = new Intent(context.getApplicationContext(),FullImageActivity.class);

                intentImage.putExtra("bitmap",bitmap);

                context.startActivity(intentImage);
            }
        });
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                AddNoteActivity activity = new AddNoteActivity();
                activity.RemoveImage(bitmap);
*/
                image.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, image.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return image.size();
    }


}

package com.yazilimmuhendisim.uninotpaylasimi;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    private PhotoView photoView;
    private Button back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        back_btn = findViewById(R.id.back_btn_1);
        photoView = findViewById(R.id.photo_view);

        String bitmap = getIntent().getStringExtra("bitmap");
        String url = getIntent().getStringExtra("url");

        if(bitmap == null && url != null){
            Picasso.with(FullImageActivity.this).load(url)
                    .into(photoView,new com.squareup.picasso.Callback(){

                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });

        }else if (url == null && bitmap!=null){
            photoView.setImageBitmap(BitmapFactory.decodeFile(bitmap));
        }


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}

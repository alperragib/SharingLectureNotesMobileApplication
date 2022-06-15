package com.yazilimmuhendisim.uninotpaylasimi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CardTasarimTutucu> {
    private Context context;
    private List<Notlar> notlarListe;
    private MainImageAdapter imageAdapter;



    public MyAdapter(Context context, List<Notlar> notlarListe) {
        this.context = context;
        this.notlarListe = notlarListe;
    }


    public class CardTasarimTutucu extends RecyclerView.ViewHolder{
        private RecyclerView rv_resim;
        private TextView textView;
        private TextView textView2;

        public CardTasarimTutucu(View view){
            super(view);
            rv_resim = view.findViewById(R.id.rv_image);
            textView = view.findViewById(R.id.textView1);
            textView2 = view.findViewById(R.id.textView2);


            rv_resim.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
            rv_resim.setAdapter(imageAdapter);
        }
    }
    @NonNull
    @Override
    public CardTasarimTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tasarim,parent,false);
        return new CardTasarimTutucu(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardTasarimTutucu holder, int position) {

        Notlar notlar = notlarListe.get(position);
        String text1 = notlar.getUni_adi()+"/"+notlar.getBolum_adi()+"/"+notlar.getDers_adi();
        holder.textView.setText(text1);
        holder.textView2.setText(notlar.getNot());
        imageAdapter = new MainImageAdapter(context,notlar.getImagesUrl());
        holder.rv_resim.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        return notlarListe.size();
    }
}

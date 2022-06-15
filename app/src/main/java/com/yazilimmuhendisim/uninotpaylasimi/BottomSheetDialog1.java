package com.yazilimmuhendisim.uninotpaylasimi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.yazilimmuhendisim.uninotpaylasimi.MainActivity.NotAra;

public class BottomSheetDialog1 extends BottomSheetDialogFragment {

    private Spinner spinner_uni;
    private Spinner spinner_bolum;
    private Spinner spinner_ders;
    private ArrayAdapter<String> dataAdapterUni;
    private ArrayAdapter<String> dataAdapterBolum;
    private ArrayAdapter<String> dataAdapterDers;
    private int[] uni_id_spinner,bolum_id_spinner,ders_id_spinner;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.bottom_sheet_layout,container,false);



        Button button1 = v.findViewById(R.id.bottom_btn1);
        spinner_uni = v.findViewById(R.id.bootom_spinner1);
        spinner_bolum = v.findViewById(R.id.bootom_spinner2);
        spinner_ders = v.findViewById(R.id.bootom_spinner3);

        UniSpinner(null,null,v);
        BolumSpinner(null,null,v);
        DersSpinner(null,null,v);

        UniListe(v);
        BolumListe(v);
        DersListe(v);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Ekle(view);
            }
        });

        return v;
    }

    private void Ekle(View v){


        if(spinner_uni.getSelectedItemPosition()==0){

            Toast.makeText(v.getContext(),"Lütfen üniversite seçiniz.",Toast.LENGTH_SHORT).show();
        }
        else if (spinner_bolum.getSelectedItemPosition()==0){
            Toast.makeText(v.getContext(),"Lütfen bölüm seçiniz.",Toast.LENGTH_SHORT).show();
        }
        else if (spinner_ders.getSelectedItemPosition()==0){
            Toast.makeText(v.getContext(),"Lütfen ders seçiniz.",Toast.LENGTH_SHORT).show();
        }
        else{
            dismiss();
            NotAra(uni_id_spinner[spinner_uni.getSelectedItemPosition()],bolum_id_spinner[spinner_bolum.getSelectedItemPosition()],
                    ders_id_spinner[spinner_ders.getSelectedItemPosition()],v);

        }


    }






    private void UniListe(final View v)
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/uni_liste.php";

        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray liste = jsonObject.getJSONArray("uniler");

                        String[] uni = new String[(liste.length()+1)];

                        int[] uni_id = new int[(liste.length()+1)];

                        uni[0]="Üniversite seçiniz";
                        uni_id[0]= -1;

                        for(int i=0;i<liste.length();i++){

                            JSONObject l = liste.getJSONObject(i);

                            uni[i+1] = l.getString("name").trim();
                            uni_id[i+1] = l.getInt("id");
                        }

                        UniSpinner(uni,uni_id,v);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(v.getContext()).add(istek);

    }
    private void BolumListe(final View v)
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/bolum_liste.php";

        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray liste = jsonObject.getJSONArray("bolumler");

                        String[] bolum = new String[(liste.length()+1)];

                        int[] bolum_id = new int[(liste.length()+1)];

                        bolum[0]="Bölüm seçiniz";

                        bolum_id[0]=-1;

                        for(int i=0;i<liste.length();i++){

                            JSONObject l = liste.getJSONObject(i);

                            bolum[i+1] = l.getString("name").trim();
                            bolum_id[i+1] = l.getInt("id");
                        }

                        BolumSpinner(bolum,bolum_id,v);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(v.getContext()).add(istek);

    }
    private void DersListe(final View v)
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/ders_liste.php";

        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray liste = jsonObject.getJSONArray("dersler");

                        String[] ders = new String[(liste.length()+1)];

                        int[] ders_id = new int[(liste.length()+1)];

                        ders[0]="Ders seçiniz";

                        ders_id[0]= -1;

                        for(int i=0;i<liste.length();i++){

                            JSONObject l = liste.getJSONObject(i);

                            ders[i+1] = l.getString("name").trim();

                            ders_id[i+1] = l.getInt("id");
                        }

                        DersSpinner(ders,ders_id,v);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(v.getContext()).add(istek);

    }
    private void UniSpinner(String[] uniler,int[] uni_id,View v){

        if(uniler!=null){

            uni_id_spinner = uni_id;
            dataAdapterUni = new ArrayAdapter<String>(v.getContext(), R.layout.spinner_item, uniler);
            dataAdapterUni.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_uni.setAdapter(dataAdapterUni);
        }
        else{
            String[] str = {"Üniversite seçiniz"};
            dataAdapterUni = new ArrayAdapter<String>(v.getContext(), R.layout.spinner_item,str);
            dataAdapterUni.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_uni.setAdapter(dataAdapterUni);
        }

    }
    private void BolumSpinner(String[] bolumler,int[] bolum_id,View v){

        if(bolumler!=null){
            bolum_id_spinner = bolum_id;
            dataAdapterBolum = new ArrayAdapter<String>(v.getContext(), R.layout.spinner_item, bolumler);
            dataAdapterBolum.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_bolum.setAdapter(dataAdapterBolum);
        }
        else{
            String[] str = {"Bölüm seçiniz"};
            dataAdapterBolum = new ArrayAdapter<String>(v.getContext(), R.layout.spinner_item, str);
            dataAdapterBolum.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_bolum.setAdapter(dataAdapterBolum);
        }

    }
    private void DersSpinner(String[] dersler,int[] ders_id,View v){

        if(dersler!=null){
            ders_id_spinner = ders_id;
            dataAdapterDers = new ArrayAdapter<String>(v.getContext(), R.layout.spinner_item, dersler);
            dataAdapterDers.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_ders.setAdapter(dataAdapterDers);
        }
        else{
            String[] str = {"Ders seçiniz"};
            dataAdapterDers = new ArrayAdapter<String>(v.getContext(), R.layout.spinner_item, str);
            dataAdapterDers.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_ders.setAdapter(dataAdapterDers);
        }

    }


}

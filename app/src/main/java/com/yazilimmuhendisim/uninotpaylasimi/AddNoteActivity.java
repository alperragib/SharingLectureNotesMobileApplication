package com.yazilimmuhendisim.uninotpaylasimi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yazilimmuhendisim.uninotpaylasimi.networking.ApiConfig;
import com.yazilimmuhendisim.uninotpaylasimi.networking.AppConfig;
import com.yazilimmuhendisim.uninotpaylasimi.networking.ServerResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNoteActivity extends AppCompatActivity {

    private Button back_btn,image_ekle,ekle_btn;
    private ProgressDialog progressDialog;
    private static final int STORAGE_PERMISSION_CODE = 2342;
    private String postPath,mediaPath;
    private Spinner spinner_uni;
    private Spinner spinner_bolum;
    private Spinner spinner_ders;
    private ArrayAdapter<String> dataAdapterUni;
    private ArrayAdapter<String> dataAdapterBolum;
    private ArrayAdapter<String> dataAdapterDers;
    private RecyclerView rv;
    private ArrayList<String> images;
    private ImageAdapter adapter;
    private int[] uni_id_spinner,bolum_id_spinner,ders_id_spinner;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        }
        progressDialog = new ProgressDialog(AddNoteActivity.this,R.style.ProgressDialogStyle);
        progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
        progressDialog.setCancelable(false);

        editText = findViewById(R.id.edit_text_not);
        spinner_uni=findViewById(R.id.spinner1);
        spinner_bolum=findViewById(R.id.spinner2);
        spinner_ders=findViewById(R.id.spinner3);
        back_btn = findViewById(R.id.back_btn);
        image_ekle = findViewById(R.id.image_ekle);
        ekle_btn = findViewById(R.id.ekle_btn);
        rv = findViewById(R.id.recylerView_image);

        UniSpinner(null,null);
        BolumSpinner(null,null);
        DersSpinner(null,null);

        UniListe();
        BolumListe();
        DersListe();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        image_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckNetwork();
                requestStoragePermission();
            }
        });
        ekle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeKeyboard();
                Ekle();
            }
        });

        rv.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        images = new ArrayList<>();

        adapter = new ImageAdapter(this,images);
        rv.setAdapter(adapter);


    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            selectImage();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==STORAGE_PERMISSION_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{

            }

        }
    }
    private void Ekle(){

        if(spinner_uni.getSelectedItemPosition()==0){

            AlertDialogGoster("Eksik Bilgi Girişi","Lütfen üniversite seçiniz.","Tamam");
        }
        else if (spinner_bolum.getSelectedItemPosition()==0){
            AlertDialogGoster("Eksik Bilgi Girişi","Lütfen bölüm seçiniz.","Tamam");
        }
        else if (spinner_ders.getSelectedItemPosition()==0){
            AlertDialogGoster("Eksik Bilgi Girişi","Lütfen ders seçiniz.","Tamam");
        }
        else if (images.isEmpty()){
            AlertDialogGoster("Eksik Bilgi Girişi","Lütfen görsel seçiniz.","Tamam");
        }
        else if(editText.getText().toString().trim().isEmpty() || editText.getText()==null){
            AlertDialogGoster("Eksik Bilgi Girişi","Lütfen not giriniz.","Tamam");
        }
        else{
            progressDialog.show();
            NotKaydet(uni_id_spinner[spinner_uni.getSelectedItemPosition()],bolum_id_spinner[spinner_bolum.getSelectedItemPosition()],
                    ders_id_spinner[spinner_ders.getSelectedItemPosition()],editText.getText().toString().trim());
        }


    }
    private void selectImage(){

        if(images.size()<10) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");

            startActivityForResult(galleryIntent, 2);
        }else{
            AlertDialogGoster("Görsel yükleme sınırına ulaştınız!","Bir notta en fazla 10 tane görsel yükleyebilirsiniz.","Tamam");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == 2) {
                    if (data != null) {
                        // Get the Image from data
                        Uri selectedImage = data.getData();
                        if(selectedImage !=null){
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            assert cursor != null;
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            mediaPath = cursor.getString(columnIndex);

                            boolean ayni=false;
                            for(int i=0;i<images.size();i++){
                                if(images.get(i).equals(mediaPath)){
                                    ayni=true;
                                    break;
                                }
                            }
                            if(ayni){

                                AlertDialogGoster("Uyarı!","Aynı görsel tekrar yüklenemez.","Tamam");

                            }else{
                                images.add(mediaPath);
                            }



                            cursor.close();

                            postPath = mediaPath;
                        }

                    }else{
                        Toast.makeText(this, "Görsel seçilmedi!",
                                Toast.LENGTH_SHORT).show();
                    }


                }

            } else {
                Toast.makeText(this, "Görsel seçilmedi!",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Görsel seçilemedi!", Toast.LENGTH_SHORT)
                    .show();
        }


    }
    public void NotKaydet(final int uni_id,final int bolum_id,final int ders_id,final String not)
    {
        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/not_kaydet.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){

                    AlertDialogGoster("Hata!","Not yüklenirken bir hata meydana geldi. Lütfen bilgileri kontrol ediniz.","Tamam");
                    progressDialog.dismiss();
                }
                else{

                        int not_id = Integer.valueOf(response);

                        for(int i=0;i<images.size();i++){
                            if(images.get(i)!=null){
                                ImageKaydet(images.get(i),not_id);
                            }
                        }


                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NoNetwokAllert(true);
            }
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("userid",getUserid());
                params.put("uniid",String.valueOf(uni_id));
                params.put("bolumid",String.valueOf(bolum_id));
                params.put("dersid",String.valueOf(ders_id));
                params.put("not",not.trim());

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);
    }
    public void ImageKaydet(final String bitmap,final int not_id)
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/image_kaydet.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){

                    AlertDialogGoster("Hata!","Not yüklenirken bir hata meydana geldi. Lütfen bilgileri kontrol ediniz.","Tamam");
                    progressDialog.dismiss();

                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray kisilerListe = jsonObject.getJSONArray("user");

                        JSONObject k = kisilerListe.getJSONObject(0);

                        String img_id = k.getString("id");

                        if(img_id.equals("0") || img_id.isEmpty()){
                            AlertDialogGoster("Hata!","Not yüklenirken bir hata meydana geldi. Lütfen bilgileri kontrol ediniz.","Tamam");
                            progressDialog.dismiss();

                        }else{
                            uploadFile(img_id,bitmap);
                        }


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NoNetwokAllert(true);
            }
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("userid",getUserid());
                params.put("bitmap",bitmap);
                params.put("notid",String.valueOf(not_id));

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);
    }

    private void uploadFile(final String id ,String postPath) {

        if (postPath == null || postPath.equals("")) {
            AlertDialogGoster("Hata!","Not yüklenirken bir hata meydana geldi. Lütfen bilgileri kontrol ediniz.","Tamam");
            progressDialog.dismiss();

        } else {
            progressDialog.show();

            Map<String, RequestBody> map = new HashMap<>();
            File file = new File(postPath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            map.put("file\"; filename=\"" + id +".jpg"+ "\"", requestBody);
            ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
            Call<ServerResponse> call = getResponse.upload("token", map);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){

                            progressDialog.dismiss();
                        }
                    }else {
                        ImageSil(id);

                        AlertDialogGoster("Hata!","Not yüklenirken bir hata meydana geldi. Lütfen bilgileri kontrol ediniz.","Tamam");
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    ImageSil(id);
                    AlertDialogGoster("Hata!","Not yüklenirken bir hata meydana geldi. Lütfen bilgileri kontrol ediniz.","Tamam");
                    progressDialog.dismiss();
                }
            });
        }
    }
    public void ImageSil(final String img_id)
    {
        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/image_sil.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NoNetwokAllert(true);
            }
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("imgid",img_id);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);
    }
    public void UniListe()
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/uni_liste.php";

        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){
                    NoNetwokAllert(true);
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

                        UniSpinner(uni,uni_id);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NoNetwokAllert(true);
            }
        });
        Volley.newRequestQueue(this).add(istek);

    }
    public void BolumListe()
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/bolum_liste.php";

        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){
                    NoNetwokAllert(true);
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

                        BolumSpinner(bolum,bolum_id);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NoNetwokAllert(true);
            }
        });
        Volley.newRequestQueue(this).add(istek);

    }
    public void DersListe()
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/ders_liste.php";

        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){
                    NoNetwokAllert(true);
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

                        DersSpinner(ders,ders_id);


                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NoNetwokAllert(true);
            }
        });
        Volley.newRequestQueue(this).add(istek);

    }

    private String getUserid(){

        SharedPreferences sharedPref = this.getSharedPreferences("Data", Context.MODE_PRIVATE);
        String savedString = sharedPref.getString("id","0");
        return savedString;
    }

    private void closeKeyboard ()
    {
        View view = this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    private void AlertDialogGoster(String Baslik,String mesaj,String button){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this,R.style.AlertDialogCustom);
        builder.setTitle(Baslik);
        builder.setMessage(mesaj);
        builder.setPositiveButton(button, null);
        builder.show();
    }

    public void UniSpinner(String[] uniler,int[] uni_id){

        if(uniler!=null){

            uni_id_spinner = uni_id;
            dataAdapterUni = new ArrayAdapter<String>(this, R.layout.spinner_item, uniler);
            dataAdapterUni.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_uni.setAdapter(dataAdapterUni);
        }
        else{
            String[] str = {"Üniversite seçiniz"};
            dataAdapterUni = new ArrayAdapter<String>(this, R.layout.spinner_item,str);
            dataAdapterUni.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_uni.setAdapter(dataAdapterUni);
        }

    }
    public void BolumSpinner(String[] bolumler,int[] bolum_id){

        if(bolumler!=null){
            bolum_id_spinner = bolum_id;
            dataAdapterBolum = new ArrayAdapter<String>(this, R.layout.spinner_item, bolumler);
            dataAdapterBolum.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_bolum.setAdapter(dataAdapterBolum);
        }
        else{
            String[] str = {"Bölüm seçiniz"};
            dataAdapterBolum = new ArrayAdapter<String>(this, R.layout.spinner_item, str);
            dataAdapterBolum.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_bolum.setAdapter(dataAdapterBolum);
        }

    }
    public void DersSpinner(String[] dersler,int[] ders_id){

        if(dersler!=null){
            ders_id_spinner = ders_id;
            dataAdapterDers = new ArrayAdapter<String>(this, R.layout.spinner_item, dersler);
            dataAdapterDers.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_ders.setAdapter(dataAdapterDers);
        }
        else{
            String[] str = {"Ders seçiniz"};
            dataAdapterDers = new ArrayAdapter<String>(this, R.layout.spinner_item, str);
            dataAdapterDers.setDropDownViewResource(R.layout.spinner_item_ic);
            spinner_ders.setAdapter(dataAdapterDers);
        }

    }

    public void CheckNetwork()
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/check_net.php";

        StringRequest istek = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("OK")){
                    NoNetwokAllert(false);
                }
                else{
                    NoNetwokAllert(true);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NoNetwokAllert(true);
            }
        });
        Volley.newRequestQueue(this).add(istek);

    }

    public void NoNetwokAllert(Boolean check){

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this,R.style.AlertDialogCustom);
        builder.setTitle("Hata!");
        builder.setMessage("Lütfen internet bağlantınızı kontrol ediniz.");
        builder.setPositiveButton("Tekrar dene", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CheckNetwork();
            }
        });
        builder.setCancelable(false);

        final AlertDialog ad = builder.show();

        if(check){
            ad.show();
        }else{
            if(ad.isShowing()) {
                ad.dismiss();
            }
        }
    }

}

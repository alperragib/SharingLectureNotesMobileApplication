package com.yazilimmuhendisim.uninotpaylasimi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static RecyclerView rv;
    private static MyAdapter adapter;
    private Button btn1,popup_btn;
    private FloatingActionButton fab;
    private int Dizi[] = new int [3];
    private static ArrayList<Notlar> notlarArrayList;
    // SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getUserid().equals("0")){
            Intent intentMain = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intentMain);
        }
        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        }
        //swipeRefreshLayout = findViewById(R.id.swipe);
        fab = findViewById(R.id.fab);
        btn1 = findViewById(R.id.bottom_btn);
        popup_btn = findViewById(R.id.popup_btn);
        rv = findViewById(R.id.recylerView);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));

        TumNotlarListele();


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog1 bottomSheet = new BottomSheetDialog1();
                bottomSheet.show(getSupportFragmentManager(),"BottomSheet");
            }
        });


        popup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this,view);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.inflate(R.menu.popup_menu);
                popup.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAdd = new Intent(MainActivity.this,AddNoteActivity.class);
                startActivity(intentAdd);
            }
        });
        /*
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TumNotlarListele();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        */

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.item1:
                Intent intent_add = new Intent(MainActivity.this,AddNoteActivity.class);
                startActivity(intent_add);
                return true;
            case R.id.item2:
                Toast.makeText(MainActivity.this,"Ayarlar",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item3:
                cikisYap();
                return true;
                default:
                return false;
        }
    }


    @Override
    public void onBackPressed() {

    }

    private void TumNotlarListele(){

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/not_liste.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                notlarArrayList = new ArrayList<Notlar>();

                if(response.equals("ERR")){
                    NoNetwokAllert(true);
                }
                else{
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray liste = jsonObject.getJSONArray("notes");

                        for(int i=0;i<liste.length();i++){

                            JSONObject not = liste.getJSONObject(i);

                            /*
                            int not_id = not.getInt("id");
                            int uni_id = not.getInt("uni_id");
                            int bolum_id = not.getInt("bolum_id");
                            int ders_id = not.getInt("ders_id");
                            */
                            String yazi = not.getString("yazi");
                            String date = not.getString("date");
                            String uni_adi = not.getString("uni_adi");
                            String bolum_adi = not.getString("bolum_adi");
                            String ders_adi = not.getString("ders_adi");

                            ArrayList<String> imageUrls = new ArrayList<>();

                            JSONArray imageArray = not.getJSONArray("images");

                            for(int j=0;j<imageArray.length();j++)
                            {
                                JSONObject image = imageArray.getJSONObject(j);
                                String url = image.getString("url");
                                imageUrls.add(url);
                            }

                            Notlar n = new Notlar(yazi,uni_adi,bolum_adi,ders_adi,date,imageUrls);
                            notlarArrayList.add(n);

                        }
                        adapter = new MyAdapter(MainActivity.this,notlarArrayList);
                        rv.setAdapter(adapter);


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

                params.put("pass","yazilimmuhendisim");

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);
    }

    private String getUserid(){

        SharedPreferences sharedPref = this.getSharedPreferences("Data",Context.MODE_PRIVATE);
        String savedString = sharedPref.getString("id","0");
        return savedString;
    }
    private void cikisYap(){
        SharedPreferences sp;
        SharedPreferences.Editor e;
        sp = getSharedPreferences("Data",MODE_PRIVATE);
        e = sp.edit();
        e.remove("id");
        e.remove("username");
        e.remove("email");
        e.commit();


        Intent intent_cikis = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent_cikis);

        Toast.makeText(MainActivity.this,"Başarıyla çıkış yapıldı.",Toast.LENGTH_SHORT).show();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
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

    public void AlertDialogGoster(String Baslik,String mesaj,String button){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
        builder.setTitle(Baslik);
        builder.setMessage(mesaj);
        builder.setPositiveButton(button, null);
        builder.show();
    }

    public static void NotAra(final int uni_id, final int bolum_id, final int ders_id,final View v){


        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/not_ara.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                notlarArrayList = new ArrayList<Notlar>();


                if(response.equals("ERR")){

                }
                else{
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray liste = jsonObject.getJSONArray("notes");

                        for(int i=0;i<liste.length();i++){

                            JSONObject not = liste.getJSONObject(i);

                            /*
                            int not_id = not.getInt("id");
                            int uni_id = not.getInt("uni_id");
                            int bolum_id = not.getInt("bolum_id");
                            int ders_id = not.getInt("ders_id");
                            */
                            String yazi = not.getString("yazi");
                            String date = not.getString("date");
                            String uni_adi = not.getString("uni_adi");
                            String bolum_adi = not.getString("bolum_adi");
                            String ders_adi = not.getString("ders_adi");

                            ArrayList<String> imageUrls = new ArrayList<>();

                            JSONArray imageArray = not.getJSONArray("images");

                            for(int j=0;j<imageArray.length();j++)
                            {
                                JSONObject image = imageArray.getJSONObject(j);
                                String url = image.getString("url");
                                imageUrls.add(url);
                            }

                            Notlar n = new Notlar(yazi,uni_adi,bolum_adi,ders_adi,date,imageUrls);
                            notlarArrayList.add(n);

                        }

                        adapter = new MyAdapter(v.getContext(),notlarArrayList);
                        rv.setAdapter(adapter);
                        rv.setHasFixedSize(true);


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
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("uni_id",String.valueOf(uni_id));
                params.put("bolum_id",String.valueOf(bolum_id));
                params.put("ders_id",String.valueOf(ders_id));

                return params;
            }
        };
        Volley.newRequestQueue(v.getContext()).add(istek);
    }


}



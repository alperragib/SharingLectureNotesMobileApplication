package com.yazilimmuhendisim.uninotpaylasimi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private RelativeLayout rellay1, rellay2;

    private Handler handler = new Handler();
    private Runnable runnable;

    private Button Giris_btn, kayit_btn;
    private EditText Username_Edt,Password_Edt;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sp;
    private SharedPreferences.Editor e;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }

        sp = getSharedPreferences("Data",MODE_PRIVATE);
        e = sp.edit();

        progressDialog = new ProgressDialog(LoginActivity.this,R.style.ProgressDialogStyle);
        progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.ic_library_books);
        /*
        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.white));

        }
        */
        rellay1 =  findViewById(R.id.rellay1);
        rellay2 =  findViewById(R.id.rellay2);
        Giris_btn = findViewById(R.id.Btn_girisYap);
        kayit_btn = findViewById(R.id.Btn_kaydol);
        Username_Edt = findViewById(R.id.EdtTxt_Username);
        Password_Edt = findViewById(R.id.EdtTxt_Password);
        signInButton = findViewById(R.id.google_button);




        if(SharedOku("username").equals("0") || SharedOku("id").equals("0")){
            runnable = new Runnable() {
                @Override
                public void run() {
                    rellay1.setVisibility(View.VISIBLE);
                    rellay2.setVisibility(View.VISIBLE);
                }
            };
        }
        else{
            runnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            };
        }

        handler.postDelayed(runnable, 2000);

        Giris_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                giris_yap();
            }
        });
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleGirisYap();
            }
        });
        if(auth.getCurrentUser() != null){
            if(SharedOku("username").equals("0")){
                googleSignOut();
            }
        }

        kayit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentKayit = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intentKayit);
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    private void giris_yap (){

        String username = Username_Edt.getText().toString().toLowerCase().trim();
        String password = Password_Edt.getText().toString().trim();

        if(username.isEmpty() && password.isEmpty()){
            AlertDialogGoster("Hatalı Giriş!","Lütfen e-mail adresinizi ve şifrenizi giriniz.","Tamam");
        }
        else if (username.isEmpty()){
            AlertDialogGoster("Hatalı Giriş!","Lütfen e-mail adresinizi giriniz.","Tamam");
        }
        else if (password.isEmpty()){
            AlertDialogGoster("Hatalı Giriş!","Lütfen şifrenizi giriniz.","Tamam");
        }
        else{
            GirisYap(username,password);
            closeKeyboard();
        }
}

    private void googleGirisYap(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignOut(){
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(LoginActivity.this,"Çıkış yapıldı.",Toast.LENGTH_SHORT).show();

                    }
                });
    }



    private void AlertDialogGoster(String Baslik,String mesaj,String button){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this,R.style.AlertDialogCustom);
        builder.setTitle(Baslik);
        builder.setMessage(mesaj);
        builder.setPositiveButton(button, null);
        builder.show();
    }

    private void SharedKaydet(String key,String value){

        e.remove(key);
        e.putString(key,value);
        e.commit();
    }
    private String SharedOku(String key){
        String metin= sp.getString(key,"0");
        return metin;
    }
    private void SharedSil(String key){

        e.remove(key);
        e.commit();
    }

    public void GirisYap(final String username, final String password)
    {
        progressDialog.show();
        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/giris_yap.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){
                    progressDialog.dismiss();
                    AlertDialogGoster("Hatalı Giriş!","Lütfen e-mail adresinizi ve şifrenizi kontrol ediniz.","Tamam");
                }
                else if (response.equals("ERR1")){
                    progressDialog.dismiss();
                    AlertDialogGoster("Hesabınızı doğrulanmamış!","Giriş yapabilmeniz için e-mail adresinizi doğrulayınız.","Tamam");
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray kisilerListe = jsonObject.getJSONArray("user");

                        JSONObject k = kisilerListe.getJSONObject(0);

                            int kisi_id = k.getInt("id");
                            String kisi_username = k.getString("username");
                            String kisi_email = k.getString("email");

                            SharedKaydet("id",String.valueOf(kisi_id));
                            SharedKaydet("username",kisi_username);
                            SharedKaydet("email",kisi_email);

                            progressDialog.dismiss();
                            Intent intentMain = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intentMain);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialogGoster("Hatalı Giriş!","Lütfen e-mail adresinizi ve şifrenizi kontrol ediniz.","Tamam");
            }
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);


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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In basarili oldugunda Firebase ile yetkilendir
                progressDialog.show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                progressDialog.dismiss();
                AlertDialogGoster("Giriş Başarısız!","Google hesabınızla giriş yapılamadı.","Tamam");
                googleSignOut();
            }
        }
    }

    //GoogleSignInAccount nesnesinden ID token'ı alıp, bu Firebase güvenlik referansını kullanarak
    // Firebase ile yetkilendirme işlemini gerçekleştiriyoruz
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //             Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());


                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            AlertDialogGoster("Giriş Başarısız!","Google hesabınızla giriş yapılamadı.","Tamam");
                            googleSignOut();
                        } else {
                            kisiSorgula(task.getResult().getUser().getDisplayName().toLowerCase(),task.getResult().getUser().getEmail());
                        }
                    }
                });}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
        AlertDialogGoster("Giriş Başarısız!","Google hesabınızla giriş yapılamadı.","Tamam");
    }

    public void kisiSorgula(final String username,final String email)
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/kisi_sorgula.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){
                    googlekisiEkle(username,email);
                }
                else{

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray kisilerListe = jsonObject.getJSONArray("user");

                        JSONObject k = kisilerListe.getJSONObject(0);

                        int kisi_id = k.getInt("id");

                        SharedKaydet("id",String.valueOf(kisi_id));
                        SharedKaydet("username",username);
                        SharedKaydet("email",email);

                        progressDialog.dismiss();
                        Intent intentMain = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intentMain);


                    }
                    catch (JSONException e) {

                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("username",username);
                params.put("email",email);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);


    }
    public void googlekisiEkle(final String username,final String email)
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/user_kaydet.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("ERR")){
                    progressDialog.dismiss();
                    AlertDialogGoster("Giriş Başarısız!","Google hesabınızla giriş yapılamadı.","Tamam");
                    googleSignOut();
                }
                else{

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray kisilerListe = jsonObject.getJSONArray("user");

                        JSONObject k = kisilerListe.getJSONObject(0);

                        int kisi_id = k.getInt("id");

                        SharedKaydet("id",String.valueOf(kisi_id));
                        SharedKaydet("username",username);
                        SharedKaydet("email",email);

                        progressDialog.dismiss();
                        Intent intentMain = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intentMain);

                    }
                    catch (JSONException e) {

                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("username",username);
                params.put("email",email);
                params.put("password","google");

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);


    }

}

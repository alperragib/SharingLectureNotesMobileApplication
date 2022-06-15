package com.yazilimmuhendisim.uninotpaylasimi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    ImageView back_btn;
    Button kayit_btn;
    EditText username_edt,email_edt,password_edt;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
        back_btn = findViewById(R.id.backBtn);
        kayit_btn = findViewById(R.id.Btn_Kayıt_Ol_register);
        username_edt = findViewById(R.id.EdtTxt_Username_register);
        email_edt = findViewById(R.id.EdtTxt_Email_register);
        password_edt = findViewById(R.id.EdtTxt_Password_register);

        progressDialog = new ProgressDialog(RegisterActivity.this,R.style.ProgressDialogStyle);
        progressDialog.setMessage("Devam eden işleminiz bulunmaktadır. Lütfen bekleyiniz..");
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.drawable.ic_library_books);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        kayit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KayitOl();
            }
        });

    }

    private void KayitOl (){
        String username = username_edt.getText().toString().toLowerCase().trim();
        String email = email_edt.getText().toString().trim();
        String password = password_edt.getText().toString().trim();

        boolean email_et=false,email_nok=false;

        for(int i=0;i<email.length();i++)
        {
            if(email.substring(i,i+1).equals("@")){
                email_et=true;
            }
            if(email.substring(i,i+1).equals(".")){
                email_nok=true;
            }
            if(email_et && email_nok){
                break;
            }
        }

        if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
            AlertDialogGoster("Hatalı Giriş!","Lütfen hesap oluşturmak için kullanıcı adı, e-mail adresi ve şifre giriniz.","Tamam");
        }
        else if (username.length()<6){
            AlertDialogGoster("Hatalı Giriş!","Kullanıcı adı en az 6 karakter uzunluğunda olmalıdır.","Tamam");
        }
        else if (username.length()>20){
            AlertDialogGoster("Hatalı Giriş!","Kullanıcı adı en fazla 20 karakter uzunluğunda olabilir.","Tamam");
        }
        else if (email.length()>250){
            AlertDialogGoster("Hatalı Giriş!","Email adresi en fazla 250 karakter uzunluğunda olabilir.","Tamam");
        }
        else if (password.length()>50){
            AlertDialogGoster("Hatalı Giriş!","Şifre en fazla 50 karakter uzunluğunda olabilir.","Tamam");
        }
        else if (password.length()<6){
            AlertDialogGoster("Hatalı Giriş!","Şifre en az 6 karakter uzunluğunda olmalıdır.","Tamam");
        }
        else if (email.length()<5){
            AlertDialogGoster("Hatalı Giriş!","Geçersiz email adresi girdiniz.","Tamam");
        }
        else if (!email_et || !email_nok){
            AlertDialogGoster("Hatalı Giriş!","Geçersiz email adresi girdiniz.","Tamam");
        }
        else{
            closeKeyboard();
            usernameSorgu(username,email,password);
        }
    }
    private void AlertDialogGoster(String Baslik,String mesaj,String button){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this,R.style.AlertDialogCustom);
        builder.setTitle(Baslik);
        builder.setMessage(mesaj);
        builder.setPositiveButton(button, null);
        builder.show();
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


    public void usernameSorgu (final String username, final String email, final String password)
    {
        progressDialog.show();
        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/username_sorgu.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse (String response) {

                if(response.equals("OK"))
                {
                    emailSorgu(username,email,password);
                }
                else
                {
                    progressDialog.dismiss();
                    AlertDialogGoster("Kullanıcı adı mevcut!",username+" zaten mavcut. Lütfen başka bir kullanıcı adı giriniz.","Tamam");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialogGoster("Hata !","Lütfen internet bağlantınızı kontrol ediniz.","Tamam");
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("username",username);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);

    }
    public void emailSorgu (final String username,final String email, final String password)
    {
        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/email_sorgu.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse (String response) {

                if(response.equals("OK"))
                {
                    kisiEkle(username,email,password);
                }
                else
                {
                    progressDialog.dismiss();
                    AlertDialogGoster("Email adresi mevcut!",email + " zaten mavcut. Lütfen başka bir email adresi giriniz.","Tamam");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialogGoster("Hata !","Lütfen internet bağlantınızı kontrol ediniz.","Tamam");
            }
        }){
            protected Map<String,String> getParams() throws AuthFailureError{

                Map<String,String> params = new HashMap<>();

                params.put("email",email);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);

    }
    public void kisiEkle(final String username,final String email, final String password)
    {

        String url = "http://yazilimmuhendisim.com/api/mobil_api_1/user_ekle.php";

        StringRequest istek = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("OK")){

                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this,R.style.AlertDialogCustom);
                    builder.setTitle("Kullanıcı kaydı başarılı");
                    builder.setMessage(email+" adrenine hesabınızı onaylayabilmeniz için onay maili gönderdik. Eğer maili göremiyorsanız spam kutusuna göz atınız.");
                    builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    builder.show();
                }
                else{
                    progressDialog.dismiss();
                    AlertDialogGoster("Kayıt Başarısız!","Kullanıcı kaydı yapılırken bir sorun oluştu. Lütfen bilgilerinizi kontrol ediniz.","Tamam");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                AlertDialogGoster("Hata !","Lütfen internet bağlantınızı kontrol ediniz.","Tamam");
            }
        }){

            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("username",username);
                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(istek);


    }

}

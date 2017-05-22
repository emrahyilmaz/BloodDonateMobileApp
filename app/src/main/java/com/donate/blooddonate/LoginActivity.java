package com.donate.blooddonate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blood.model.BaseReturn;
import com.blood.model.Donor;
import com.blood.service.ApiClient;
import com.blood.service.IDonorRestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edKullanici, edSifre;
    Button btnGiris, btnYeniKayit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edKullanici = (EditText) findViewById(R.id.edKullanici);
        edSifre = (EditText) findViewById(R.id.edSifre);
        btnGiris = (Button) findViewById(R.id.btnGiris);
        btnYeniKayit = (Button) findViewById(R.id.btnYeniKayit);

        btnGiris.setOnClickListener(listener);
        btnYeniKayit.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnGiris:
                    login();
                    return;
                case R.id.btnYeniKayit:
                    Intent i = new Intent(LoginActivity.this,YeniKullaniciActivity.class);
                    startActivity(i);
                    return;
                default:
                    return;
            }
        }
    };

    public void login(){
        IDonorRestService iService =
                ApiClient.getClient().create(IDonorRestService.class);
        Call<BaseReturn<Donor>> call = iService.login(edKullanici.getText().toString(),edSifre.getText().toString());
        call.enqueue(new Callback<BaseReturn<Donor>>() {
            @Override
            public void onResponse(Call<BaseReturn<Donor>> call, Response<BaseReturn<Donor>> response) {
                BaseReturn<Donor> result = response.body();
                if (result.isResult()){
                    Toast.makeText(LoginActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<BaseReturn<Donor>> call, Throwable t) {
                Log.e("LOGIN",t.toString());
                Toast.makeText(LoginActivity.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
}

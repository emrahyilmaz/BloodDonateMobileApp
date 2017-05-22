package com.donate.blooddonate;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.blood.model.BaseReturn;
import com.blood.model.Donor;
import com.blood.service.ApiClient;
import com.blood.service.IDonorRestService;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YeniKullaniciActivity extends AppCompatActivity {

    @BindView(R.id.edAd) EditText edAd;
    @BindView(R.id.edSoyad) EditText edSoyad;
    @BindView(R.id.edTcNo) EditText edTcNo;
    @BindView(R.id.edDogumTarihi) EditText edDogumTarihi;
    @BindView(R.id.edKanGrubu) EditText edKanGrubu;
    @BindView(R.id.edEmail) EditText edEmail;
    @BindView(R.id.edSifre) EditText edSifre;
    @BindView(R.id.edTelNo) EditText edTelNo;

    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

    static final int DATE_DIALOG_ID = 999;
    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yeni_kullanici);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.edDogumTarihi)
    public void changeDogumTarihi(){
        showDialog(DATE_DIALOG_ID);
    }

    @OnClick(R.id.btnKaydet)
    public void kaydet(){
        Donor donor = new Donor();
        donor.setName(edAd.getText().toString());
        donor.setLastname(edSoyad.getText().toString());
        donor.setBloodGroup(edKanGrubu.getText().toString());
        donor.setEmail(edEmail.getText().toString());
        donor.setPassword(edSifre.getText().toString());
        donor.setTcNo(edTcNo.getText().toString());
        donor.setTel(edTelNo.getText().toString());
        donor.setStatus("A");
        donor.setType("D");
        try {
            donor.setBirthDay(formatter.parse(edDogumTarihi.getText().toString()));
        }catch (ParseException e){
            e.printStackTrace();
        }


        IDonorRestService iService =
                ApiClient.getClient().create(IDonorRestService.class);
        Call<BaseReturn<String>> call = iService.save(donor);
        call.enqueue(new Callback<BaseReturn<String>>() {
            @Override
            public void onResponse(Call<BaseReturn<String>> call, Response<BaseReturn<String>> response) {
                BaseReturn<String> result = response.body();
                if (result.isResult()){
                    Toast.makeText(YeniKullaniciActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    Intent i = new Intent(YeniKullaniciActivity.this,LoginActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(YeniKullaniciActivity.this,response.body().getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BaseReturn<String>> call, Throwable t) {
                Log.e("LOGIN",t.toString());
                Toast.makeText(YeniKullaniciActivity.this,t.toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            edDogumTarihi.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
            //dpResult.init(year, month, day, null);

        }
    };
}

package com.donate.blooddonate;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.blood.model.BaseReturn;
import com.blood.model.Donor;
import com.blood.service.ApiClient;
import com.blood.service.IDonorRestService;
import com.blood.util.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YeniKullaniciActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.edAd) EditText edAd;
    @BindView(R.id.edSoyad) EditText edSoyad;
    @BindView(R.id.edTcNo) EditText edTcNo;
    @BindView(R.id.edDogumTarihi) EditText edDogumTarihi;
    @BindView(R.id.edKanGrubu) EditText edKanGrubu;
    @BindView(R.id.edEmail) EditText edEmail;
    @BindView(R.id.edSifre) EditText edSifre;
    @BindView(R.id.edTelNo) EditText edTelNo;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

    static final int DATE_DIALOG_ID = 999;
    private int year;
    private int month;
    private int day;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yeni_kullanici);
        ButterKnife.bind(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        String [ ] PERMISSIONS = {Manifest.permission.CALL_PHONE,
                Manifest.permission.INTERNET,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE};

        if (!hasPermissions(YeniKullaniciActivity.this,PERMISSIONS)){
            ActivityCompat.requestPermissions(YeniKullaniciActivity.this,PERMISSIONS,1);
        }

        getCurrentLocation();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permissions granted.
        } else {
            // no permissions granted.
        }
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
        }
    };

    public  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void getCurrentLocation(){
        GPSTracker gps = new GPSTracker(YeniKullaniciActivity.this);
        if (gps.getLatitude()==0.0||gps.getLongitude()==0.0){
            gps.showSettingsAlert();
        } else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( YeniKullaniciActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( YeniKullaniciActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(YeniKullaniciActivity.this,String.valueOf(mLastLocation.getLatitude()) + " / " + String.valueOf(mLastLocation.getLongitude()),Toast.LENGTH_LONG).show();
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            // mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}

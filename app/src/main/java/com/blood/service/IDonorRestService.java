package com.blood.service;

import com.blood.model.BaseReturn;
import com.blood.model.Donor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IDonorRestService {

    @GET("DonorRestService/login")
    Call<BaseReturn<Donor>> login(@Query("email") String email, @Query("password") String password);

    @POST("DonorRestService/save")
    Call<BaseReturn<String>> save(@Body Donor donor);
}
package com.octane.app.API;



import com.octane.app.Model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {
    @POST("api_signup")
    @FormUrlEncoded
    Call<ResponseModel> register(@Field("first_name") String firstName, @Field("last_name") String lastName, @Field("email") String email,
                                 @Field("city") String city,@Field("state") String state,@Field("country") String country,
                                 @Field("phone") String phone,@Field("car_model") String carModel,@Field("password") String password);

    @POST("api_login")
    @FormUrlEncoded
    Call<ResponseModel> login(@Field("email") String username, @Field("password") String password);

    @POST("api_savepin")
    @FormUrlEncoded
    Call<ResponseModel> savePIN(@Field("email") String username, @Field("password") String password,@Field("pin") String pinCode);

}

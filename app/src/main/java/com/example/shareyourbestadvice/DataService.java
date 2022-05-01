package com.example.shareyourbestadvice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;
import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService {

    @GET("getadvice")
    Call<List<Advice>> getAdviceData();

    @GET("getcategories")
    Call<List<Category>> getCategoryData();

    @POST("addadvice")
    @FormUrlEncoded
    Call<List<Advice>> addAdviceData(@Field("id") int id, @Field("advice") String advice, @Field("author") String author, @Field("category") String category);
}

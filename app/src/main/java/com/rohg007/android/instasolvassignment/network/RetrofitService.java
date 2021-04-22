package com.rohg007.android.instasolvassignment.network;

import android.content.Context;

import com.rohg007.android.instasolvassignment.application.InstasolvAssignmentApplication;
import com.rohg007.android.instasolvassignment.utils.NetworkUtil;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static String BASE_URL = "https://api.themoviedb.org/3/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);
    }
}

package com.example.titomi.workertrackerloginmodule.apis.rest;

import android.content.Context;
import android.util.Log;

import com.example.titomi.workertrackerloginmodule.apis.Interfaces.UsersApiService;
import com.example.titomi.workertrackerloginmodule.apis.model.Users;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Titomi on 2/20/2018.
 */

public class Communicator {

    private static final String TAG = "Communicator";
    private static final String SERVER_URL = "http://fieldmonitor.co/fieldworker_api/";
    private static final String API_KEY = "98SY.4T1nXhPI";
    private Context mCtx;

    public void loginGet(String code){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build()).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL).build();
        UsersApiService service = retrofit.create(UsersApiService.class);

        Call<Users> call = service.getUsers(API_KEY,code);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.body() == null){
                    Log.e(TAG, "Failed");
                    return;
                }
                BusProvider.getInstance().post(new ServerEvent(response.body()));
                Log.e(TAG, "Success");
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });
    }

}

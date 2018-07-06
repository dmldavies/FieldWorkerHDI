package com.example.titomi.workertrackerloginmodule.apis.model.leave_model;

import android.content.Context;

import com.example.titomi.workertrackerloginmodule.apis.Interfaces.LeaveApiService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Titomi on 2/22/2018.
 */

public class LeaveCommunicator {

    private static final String TAG = "LeaveCommunicator";
    private static final String SERVER_URL = "https://chemotropic-partiti.000webhostapp.com/fieldworker_api/";
    private static final String API_KEY = "98SY.4T1nXhPI";
    private Context mCtx;

    //to view leave requests
    public void leaveGet(String view, String id){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder().client(httpClient.build()).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER_URL).build();
        LeaveApiService service = retrofit.create(LeaveApiService.class);

        /*Call<LeaveModel> call = service.getLeave(API_KEY, view, id);
        call.enqueue(new Callback<LeaveModel>() {
            @Override
            public void onResponse(Call<LeaveModel> call, Response<LeaveModel> response) {
                if (response.body() == null){
                    Log.e(TAG, "Failed");
                    return;
                }
                BusProvider.getInstance().post(new ServerEvent(response.body()));
                Log.e(TAG, "Success");
            }

            @Override
            public void onFailure(Call<LeaveModel> call, Throwable t) {
                BusProvider.getInstance().post(new ErrorEvent(-2,t.getMessage()));

            }
        });*/
    }
}

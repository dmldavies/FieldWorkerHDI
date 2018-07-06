package com.example.titomi.workertrackerloginmodule.apis.model.leave_model;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Titomi on 2/22/2018.
 */

public class LeaveApiClient {

    private static final String TAG = "LeaveCommunicator";
    private static final String SERVER_URL = "https://chemotropic-partiti.000webhostapp.com/fieldworker_api/";


    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }
}

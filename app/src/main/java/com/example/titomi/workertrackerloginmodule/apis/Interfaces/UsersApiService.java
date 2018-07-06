package com.example.titomi.workertrackerloginmodule.apis.Interfaces;

import com.example.titomi.workertrackerloginmodule.apis.model.Users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Titomi on 2/20/2018.
 */

public interface UsersApiService {

    @GET("user/login.php")
    Call<Users> getUsers(
            @Query("key") String key,
            @Query("line_id") String line_id);
}

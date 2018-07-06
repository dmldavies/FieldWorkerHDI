package com.example.titomi.workertrackerloginmodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.titomi.workertrackerloginmodule.shared_pref_manager.SharedPrefManager;
import com.example.titomi.workertrackerloginmodule.supervisor.User;

public class HomeSplashActivity extends AppCompatActivity {

    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());

//        Intent intent = new Intent(this, UserLoginActivity.class);
//        startActivity(intent);
//        finish();


        Thread thread = new Thread(){
            @Override
            public void run(){
                User user = sharedPrefManager.getLoggedInUser();
                try{
                    sleep(1500);
                    if (user== null || user.getId() == 0){
                        Intent i = new Intent(HomeSplashActivity.this, LoginActivity.class);

                        startActivity(i);
                        finish();

                    }else{
                        Intent inta = new Intent(HomeSplashActivity.this, DashboardActivity.class);
                        inta.putExtra(getString(R.string.loggedInUser),user);
                        startActivity(inta);
                        finish();
                    }

                    /*SharedPreferences sharedP = getSharedPreferences("NewLogin", Context.MODE_PRIVATE);
                    String val = sharedP.getString("line_id", "");
                    if (val.length() == 0){
                        Intent intent = new Intent(HomeSplashActivity.this, UsersLoginTrivalActivity.class);
                        startActivity(intent);
                        finish();
                    }*/
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
}

package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;


/**
 * Created by NeonTetras on 04-Oct-17.
 */
public class NoNetworkActivity extends Activity implements View.OnClickListener {

    Button redoButton;
    Button openSettingsButton;
   @Override
   public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.no_network_layout);

       redoButton = (Button)findViewById(R.id.redo);
       openSettingsButton = (Button)findViewById(R.id.open_settings);
       openSettingsButton.setOnClickListener(this);
       redoButton.setOnClickListener(this);
   }
    @Override
    public void onClick(View v) {
        if(v == openSettingsButton){
            openSettings();
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == OPEN_WIFI){
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
             if(!wifiManager.isWifiEnabled()){
                 Toast.makeText(this,"Atrium needs network to continue", Toast.LENGTH_LONG).show();
             }else{
                 finish();
             }
        }
    }
    private void openSettings(){
        startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),OPEN_WIFI);
    }
    public static final int OPEN_WIFI = 0;
}

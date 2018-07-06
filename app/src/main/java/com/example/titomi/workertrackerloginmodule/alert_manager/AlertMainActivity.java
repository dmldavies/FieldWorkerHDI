package com.example.titomi.workertrackerloginmodule.alert_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.titomi.workertrackerloginmodule.alert_manager.alert_sub_menu.AbsentActivity;
import com.example.titomi.workertrackerloginmodule.alert_manager.alert_sub_menu.GeofencingActivity;
import com.example.titomi.workertrackerloginmodule.alert_manager.alert_sub_menu.LowBatteryActivity;
import com.example.titomi.workertrackerloginmodule.alert_manager.alert_sub_menu.LowStockActivity;
import com.example.titomi.workertrackerloginmodule.R;

public class AlertMainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    CardView geofencing, lowStock, absentWorker, lowBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Alert Manager");

        geofencing = findViewById(R.id.geofencing_lock);
        lowStock = findViewById(R.id.low_stock);
        absentWorker = findViewById(R.id.absent_worker);
        lowBattery = findViewById(R.id.low_battery);

        geofencing.setOnClickListener(this);
        lowStock.setOnClickListener(this);
        absentWorker.setOnClickListener(this);
        lowBattery.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.geofencing_lock:
                i = new Intent(this, GeofencingActivity.class);
                startActivity(i);
                break;
            case R.id.low_stock:
                i = new Intent(this, LowStockActivity.class);
                startActivity(i);
                break;
            case R.id.absent_worker:
                i = new Intent(this, AbsentActivity.class);
                startActivity(i);
                break;
            case R.id.low_battery:
                i = new Intent(this, LowBatteryActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}

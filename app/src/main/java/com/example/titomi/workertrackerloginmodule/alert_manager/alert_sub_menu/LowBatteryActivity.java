package com.example.titomi.workertrackerloginmodule.alert_manager.alert_sub_menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;

public class LowBatteryActivity extends AppCompatActivity {

    private Context mContext;
    private TextView mTextViewInfo, mTextViewPercentage;
    private ProgressBar mProgressBar;
    private int mProgressStatus;
    Toolbar toolbar;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            mTextViewInfo.setText("Battery Scale : "+ scale);

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            mTextViewInfo.setText(mTextViewInfo.getText() + "\nBattery Level : "+level);

            float percentage = level/(float) scale;

            mProgressStatus = (int)((percentage)*100);

            mTextViewPercentage.setText(""+mProgressStatus+ "%");

            mTextViewInfo.setText(mTextViewInfo.getText()+"\nPercentage : "+mProgressStatus+ "%");

            mProgressBar.setProgress(mProgressStatus);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_battery);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Inventory Manager");

        mContext = getApplicationContext();

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        mContext.registerReceiver(mBroadcastReceiver, iFilter);
        mTextViewInfo = findViewById(R.id.tv_info);
        mTextViewPercentage = findViewById(R.id.tv_percentage);
        mProgressBar = findViewById(R.id.pb);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

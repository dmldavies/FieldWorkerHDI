package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.titomi.workertrackerloginmodule.R;


public class ActivityAttendanceReport extends Activity implements View.OnClickListener{

    private Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        toolbar = (Toolbar)findViewById(R.id.toolBar);

        setActionBar(toolbar);

        findViewById(R.id.home).setOnClickListener(this);


       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:
                finish();
                break;
        }
    }
}

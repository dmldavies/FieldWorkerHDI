package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.services.FieldMonitorMessagingService;

public class ActivityMessageMenu extends AppCompatActivity implements View.OnClickListener{

    TextView sendMessageTv,viewMessagesTv;
    private Context cxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_actions_menu);

        cxt = this;
        sendMessageTv = (TextView)findViewById(R.id.sendMessage);
        viewMessagesTv = (TextView)findViewById(R.id.viewMessages);

        sendMessageTv.setOnClickListener(this);
        viewMessagesTv.setOnClickListener(this);
        findViewById(R.id.inventoryRequest).setOnClickListener(this);
        findViewById(R.id.reports).setOnClickListener(this);
        findViewById(R.id.assignTask).setOnClickListener(this);
        findViewById(R.id.clockIn).setOnClickListener(this);
       // findViewById(R.id.challenges).setOnClickListener(this);
        findViewById(R.id.leaveApplication).setOnClickListener(this);
        findViewById(R.id.attendanceReport).setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendMessage:
                startActivity(ActivityAdminNewMessage.class);
                break;
            case R.id.viewMessages:
                startActivity(ActivityMessageListing.class);
                break;
            case R.id.inventoryRequest:
                startActivity(ActivityInventoryRequestsListing.class);
                break;
            case R.id.reports:
                startActivity(ActivityReportListing.class);
                break;
            case R.id.assignTask:
                startActivity(ActivityTaskListing.class);
                break;
            case R.id.clockIn:
               // startActivity(ActivityInventoryRequest.class);
                break;
          /*  case R.id.challenges:
                startActivity(ActivityChallenges.class);
                break;*/
            case R.id.leaveApplication:
                startActivity(ActivityLeaveApplication.class);
                break;

        /*    case R.id.attendanceReport:
                startActivity(ActivityAttendanceReport.class);
                break;*/
        }
    }

    private void startActivity(Class<?> t){
        Intent i = new Intent(cxt,t);
        startActivity(i);
    }
}

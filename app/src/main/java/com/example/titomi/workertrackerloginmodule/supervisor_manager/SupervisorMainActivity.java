package com.example.titomi.workertrackerloginmodule.supervisor_manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;

public class SupervisorMainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    CardView feedback, assignment;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Supervisor Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        feedback = findViewById(R.id.supervisor_feedback);
        assignment = findViewById(R.id.special_assignments);


    }


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.supervisor_feedback:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.special_assignments:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}

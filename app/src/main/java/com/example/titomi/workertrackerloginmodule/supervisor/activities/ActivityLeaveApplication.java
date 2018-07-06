package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DateTimeUtil;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ActivityLeaveApplication extends AppCompatActivity implements View.OnClickListener {

    private EditText fromDate,toDate,reasonEditText;
    private Context cxt;
    private Button submitButton;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cxt = this;
        fromDate = (EditText)findViewById(R.id.from_date);
        toDate = (EditText)findViewById(R.id.to_date);
        submitButton = findViewById(R.id.submit);
        reasonEditText = findViewById(R.id.reasonEditText);

        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        if(getIntent().getExtras() != null) {
            loggedInUser = (User) getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }

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
            case R.id.from_date:
                DateTimeUtil.showDatePicker(cxt,fromDate);
                break;
            case R.id.to_date:
                DateTimeUtil.showDatePicker(cxt,toDate);
                break;
            case R.id.submit:
                if(!NetworkChecker.haveNetworkConnection(cxt))return;

                try {
                    HashMap<String, String> data = getLeaveData();
                    submitApplication(data);
                } catch (InputValidator.InvalidInputException e) {
                    Toast.makeText(cxt, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void submitApplication(final HashMap<String, String> leaveData) {
        new android.os.AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... strings) {
                return Network.performPostCall(strings[0], leaveData);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Util.disableChildrenViews(cxt,false,R.id.leave_form);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(s == null) return;

                try {
                    JSONObject obj = new JSONObject(s);
                    if(obj.getInt("statusCode") == Entity.STATUS_OK){

                        Toast.makeText(cxt,"Leave application submitted successfully",Toast.LENGTH_SHORT).show();
                        Util.clearEditTexts(fromDate,toDate,reasonEditText);
                        Util.disableChildrenViews(cxt,true,R.id.leave_form);
                        leaveData.clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(s);

                }
            }
        }
        .execute(getString(R.string.api_url)+
                getString(R.string.leave_application_url)+"?key="+
                getString(R.string.field_worker_api_key));
    }

    private HashMap<String, String> getLeaveData() throws InputValidator.InvalidInputException {

            leaveData.put("from_date", InputValidator.validateText(fromDate,10));
            leaveData.put("to_date", InputValidator.validateText(toDate,10));
            leaveData.put("reason", InputValidator.validateText(reasonEditText,5));
            leaveData.put("user_id", ""+loggedInUser.getId());

        return leaveData;
    }
    HashMap<String,String> leaveData = new HashMap<>();
}

package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;
import com.example.titomi.workertrackerloginmodule.supervisor.Institutions;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DateTimeUtil;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import static com.example.titomi.workertrackerloginmodule.supervisor.util.Network.backgroundTask;

public class ActivityAssignTask extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    public static final String update = "update";
    private static final int PICK_INSTITUTIONS = 200;
    final String assign = "assign";
    Context cxt;
    Boolean assignSelfChecked;
    ArrayList<Long> workerIds = new ArrayList<>();
    ArrayList<String> workerNames = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();
    ArrayList<String> lgas = new ArrayList<>();
    String selectedState;
    HashMap<String, String> taskData = new HashMap<>();
    private EditText dateEditText;
    private EditText timeEditText,taskTitleEdit,
            taskDescriptionEdit,institutionNameEdit,
            fullAddressEdit, contactFullNameEdit, contactNumberEdit, locationEdit;
    private Spinner stateSpinner, lgaSpinner, taskTypeSpinner;
    private Spinner workerSpinner;
    private TextView selectLocationText;
    private Button assignTaskBut;
    private CheckBox selfAssignCheck;
    private ProgressBar lgaLoading;
    private Task selectedTask;
    private User loggedInUser;
    private LatLng taskCoordinates;
    private Institutions selectedInstitution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //error reporting
//        Mint.setApplicationEnvironment(Mint.appEnvironmentTesting);
//
//        Mint.initAndStartSession(this.getApplication(), "fa0aaf30");

        setContentView(R.layout.activity_assign_task);

        cxt = this;
        dateEditText = findViewById(R.id.dateText);
        timeEditText = findViewById(R.id.timeText);
        workerSpinner = findViewById(R.id.workers);
        stateSpinner = findViewById(R.id.state);
        lgaSpinner = findViewById(R.id.lga);
        selectLocationText = findViewById(R.id.selectLocation);
        taskTitleEdit = findViewById(R.id.taskTitle);
        selfAssignCheck = findViewById(R.id.selfCheckBox);
        taskDescriptionEdit = findViewById(R.id.description);
        institutionNameEdit = findViewById(R.id.institution);
        fullAddressEdit = findViewById(R.id.fullAddress);
        contactFullNameEdit = findViewById(R.id.contactFullName);
        contactNumberEdit = findViewById(R.id.contactPhone);
        assignTaskBut = findViewById(R.id.assignTaskBut);
        taskTypeSpinner = findViewById(R.id.taskType);
        locationEdit = findViewById(R.id.location);
        lgaLoading = findViewById(R.id.lgaLoading);

        dateEditText.setOnClickListener(this);
        timeEditText.setOnClickListener(this);
       // workerSpinner.setOnItemSelectedListener(this);
        stateSpinner.setOnItemSelectedListener(this);
        selectLocationText.setOnClickListener(this);
        assignTaskBut.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.app_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        selfAssignCheck.setOnClickListener(this);

        taskTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                enableDisableFields(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        assignSelfChecked = selfAssignCheck.isChecked();

        selfAssignCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
              //  Toast.makeText(cxt, "Supervisor only", Toast.LENGTH_SHORT).show();
                workerSpinner.setEnabled(false);
            } else {
              //  Toast.makeText(cxt, "Show all Workers", Toast.LENGTH_SHORT).show();
                workerSpinner.setEnabled(true);
            }
        });


        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
            selectedTask = (Task)getIntent().getExtras().getSerializable("task");

            if(selectedTask != null) {
                setupView(selectedTask);
            }else{
                loadWorkerSpinner();
            }

        }

        switch (loggedInUser.getRoleId()) {
            case User.SUPERVISOR:
                selfAssignCheck.setVisibility(View.VISIBLE);
                break;
            case User.NURSE:
                selfAssignCheck.setVisibility(View.GONE);
                break;
        }


        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                selectedState = (String) parent.getItemAtPosition(position);

                try {
                    loadLgaSpinner(selectedState);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.dateText:
                DateTimeUtil.showDatePicker(cxt,dateEditText);
                break;
            case R.id.timeText:
                DateTimeUtil.showTimePicker(cxt,timeEditText);
                break;
            case R.id.selectLocation:
                if(v.getTag().toString().equalsIgnoreCase("institution")) {
                    Intent i = new Intent(cxt, ActivityInstitutionListing.class);
                    i.putExtra(getString(R.string.loggedInUser), loggedInUser);
                    i.putExtra(getString(R.string.calling_activity),this.getClass().getName());
                    startActivityForResult(i, PICK_INSTITUTIONS);
                }else {
                     Util.showPlacesPicker(cxt);
                }
                break;
            case R.id.assignTaskBut:
                if(!NetworkChecker.haveNetworkConnection(cxt))return;
                if(assignTaskBut.getTag().toString().equalsIgnoreCase("update")){
                    assignTask(getString(R.string.api_url)+getString(R.string.edit_task_url)+"?key="+getString(R.string.field_worker_api_key));
                }else{
                    assignTask(getString(R.string.api_url)+getString(R.string.add_task_url)+"?key="+getString(R.string.field_worker_api_key));
                }

            break;
        }
    }

    private void setupView(Task task){
       // workerIds
        SimpleDateFormat dtf= new SimpleDateFormat("yyyy/MM/dd");
        dateEditText.setText(dtf.format(task.getDateGiven()));
        timeEditText.setText(task.getTimeGiven());
        //workerSpinner.set

        String taskTypes[] =  getResources().getStringArray(R.array.taskType);
        taskTypeSpinner.setSelection(Arrays.asList(taskTypes).indexOf(task.getWorkType()));
        String states[] = getResources().getStringArray(R.array.states);
        stateSpinner.setSelection(Arrays.asList(states).indexOf(selectedTask.getState()));


        taskTitleEdit.setText(task.getName());
        taskDescriptionEdit.setText(task.getDescription());
        institutionNameEdit.setText(task.getInstitution_name());
        fullAddressEdit.setText(task.getAddress());
        contactFullNameEdit.setText(task.getContactName());
        contactNumberEdit.setText(task.getContactNumber());


        locationEdit.setText(task.getLocation());

        assignTaskBut.setTag("update");
        assignTaskBut.setText("Update");
        loadWorkerSpinner();

        taskCoordinates = new LatLng(task.getLatitude(),task.getLongitude());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        switch (requestCode){
            case Util.PICK_PLACES:

                if(data == null) return;
                Place place = PlacePicker.getPlace(this, data);
                locationEdit.setText(place.getAddress());
               taskCoordinates = place.getLatLng();
                break;
            case PICK_INSTITUTIONS:
                if(data.getExtras() != null){
                     selectedInstitution = (Institutions)data.getExtras().getSerializable(getString(R.string.selected_institution));
                    locationEdit.setText(selectedInstitution.getAddress());
                    fullAddressEdit.setText(selectedInstitution.getAddress());
                    institutionNameEdit.setText(selectedInstitution.getName());
                    String states[] = getResources().getStringArray(R.array.states);
                    stateSpinner.setSelection(Arrays.asList(states).indexOf(selectedInstitution.getState()));
                    String[] _lgas = new String[1];
                    _lgas[0] = selectedInstitution.getLga();
                  /*  ArrayAdapter<String> adapter = new ArrayAdapter<>(cxt, android.R.layout.simple_dropdown_item_1line, _lgas);
                    lgaSpinner.setAdapter(adapter);
                    lgas.clear();
                    lgas.add(selectedInstitution.getLga());*/

                    taskCoordinates = new LatLng(selectedInstitution.getLatitude(),selectedInstitution.getLongitude());


                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();


        //loadLgaSpinner();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       /* if(view == stateSpinner) {

        }*/

    }

    private void enableDisableFields(int position){
        switch (position) {
            case 1:
                institutionNameEdit.setInputType(InputType.TYPE_NULL);
                institutionNameEdit.setEnabled(false);
                stateSpinner.setEnabled(false);
               lgaSpinner.setEnabled(false);
                locationEdit.setVisibility(View.GONE);
                selectLocationText.setText("Click to select Institution");
                selectLocationText.setTag("institution");
                fullAddressEdit.setVisibility(View.GONE);
                findViewById(R.id.locationLabel).setVisibility(View.GONE);
                findViewById(R.id.fullAddressLabel).setVisibility(View.GONE);
                break;
            case 0:
                findViewById(R.id.fullAddressLabel).setVisibility(View.VISIBLE);
                institutionNameEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
                institutionNameEdit.setEnabled(true);
                stateSpinner.setEnabled(true);
                lgaSpinner.setEnabled(true);
                fullAddressEdit.setVisibility(View.VISIBLE);
                locationEdit.setVisibility(View.VISIBLE);
                selectLocationText.setText("Click to select");
                selectLocationText.setTag("location");
                findViewById(R.id.locationLabel).setVisibility(View.VISIBLE);
                findViewById(R.id.fullAddressLabel).setVisibility(View.VISIBLE);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    @SuppressLint("StaticFieldLeak")
    private void loadWorkerSpinner(){
        //  final ProgressDialog d = new ProgressDialog(cxt);
        switch (loggedInUser.getRoleId()) {
            case User.SUPERVISOR:
                new android.os.AsyncTask<String,Void,String>(){
                    @Override
                    protected String doInBackground(String... params) {
                        return backgroundTask(null,params[0]);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //  d.setMessage(getString(R.string.please_wait));
                        //  d.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        // super.onPostExecute(s);

                        //  Toast.makeText(cxt,s,Toast.LENGTH_LONG).show();
                        //  d.dismiss();
                        if(s == null){
                            //  Toast.makeText(cxt,"returned null",Toast.LENGTH_LONG).show();
                            return;

                        }

                        try {
                            JSONArray array = new JSONArray(s);

                            workerIds.clear();
                            workerNames.clear();
                            // Toast.makeText(cxt,""+array.length(),Toast.LENGTH_LONG).show();
                            for(int i = 0; i< array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                workerIds.add(obj.getLong("id"));
                                workerNames.add(String.format("%s %s - %s",
                                        obj.getString("first_name"),
                                        obj.getString("last_name"),
                                        obj.getString("line_id")));
                            }
                            //  Toast.makeText(cxt,workerNames.get(0),Toast.LENGTH_LONG).show();
                            setupWorkerSpinner();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }

                    //TODO: get the supervisor id programmatically

                }.execute(String.format(Locale.ENGLISH,"%s?key=%s&view=get_workers&id=%d",getString(R.string.api_url)+getString(R.string.supervisor_view_url),getString(R.string.field_worker_api_key),loggedInUser.getId()));
                break;

            case User.NURSE:

                new android.os.AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        return backgroundTask(null, params[0]);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        //  d.setMessage(getString(R.string.please_wait));
                        //  d.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        // super.onPostExecute(s);

                        //  Toast.makeText(cxt,s,Toast.LENGTH_LONG).show();
                        //  d.dismiss();
                        if (s == null) {
                            //  Toast.makeText(cxt,"returned null",Toast.LENGTH_LONG).show();
                            return;

                        }

                        try {
                            JSONObject obj = new JSONObject(s);

                            workerIds.clear();
                            workerNames.clear();
                            // Toast.makeText(cxt,""+array.length(),Toast.LENGTH_LONG).show();
//                            for(int i = 0; i< obj.length();){
//                                JSONObject obj = array.getJSONObject(i);
                            workerIds.add(obj.getLong("id"));
                            workerNames.add(String.format("%s %s - %s",
                                    obj.getString("first_name"),
                                    obj.getString("last_name"),
                                    obj.getString("line_id")));
//                            }
                            //  Toast.makeText(cxt,workerNames.get(0),Toast.LENGTH_LONG).show();
                            setupWorkerSpinner();
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                            System.out.println(e.getMessage());
                        }

                    }

                    //TODO: get the supervisor id programmatically

                }.execute(String.format("%suser/view.php?key=%s&view=by_id&id=%d", getString(R.string.api_url), getString(R.string.field_worker_api_key), loggedInUser.getId()));
//                String dfg = "http://fieldmonitor.co/fieldworker_api/user/view.php?key=a66Zo3osEyV7o&view=all";
                break;
        }
    }

    private void setupLgaSpinner(){
        ArrayAdapter<String> lgaAdapter = new ArrayAdapter<>(cxt, android.R.layout.simple_spinner_dropdown_item, lgas);
        lgaSpinner.setAdapter(lgaAdapter);

        if(selectedInstitution !=null) {
            lgaSpinner.setSelection(Arrays.asList(lgas.toArray()).indexOf(selectedInstitution.getLga().trim()));
            lgaSpinner.setEnabled(false);
        }
        if(selectedTask !=null) {
            lgaSpinner.setSelection(Arrays.asList(lgas.toArray()).indexOf(selectedTask.getLga().trim()));
        }



    }

    @SuppressLint("StaticFieldLeak")
    private void loadLgaSpinner(String state) throws UnsupportedEncodingException {
        selectedState = Network.encodeUrl(state);
        //Toast.makeText(cxt,selectedState,Toast.LENGTH_SHORT).show();
        //  final ProgressDialog d = new ProgressDialog(cxt);
        new android.os.AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... params) {
                return backgroundTask(null,params[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            lgaSpinner.setEnabled(false);
            lgaLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                lgaLoading.setVisibility(View.INVISIBLE);

                if(s == null){

                    return;

                }
                //don't want the user to be able to select the lga when an institution is selected
                if(selectedInstitution != null) {
                    lgaSpinner.setEnabled(true);
                }

                try {


                    JSONArray array = new JSONArray(s);
                    lgas.clear();


                    for(int i = 0; i<array.length(); i++){
                        lgas.add(array.getString(i).trim());
                    }

                    setupLgaSpinner();

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(s);
                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }





        }.execute(getString(R.string.api_url)+getString(R.string.state_api_url)+"?view=lga&state="+selectedState);
    }

    private void setupWorkerSpinner(){
        if(workerNames.size() == 0){
            return;
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(cxt,android.R.layout.simple_spinner_dropdown_item,workerNames);
        workerSpinner.setAdapter(spinnerAdapter);

        if(selectedTask != null){

//            switch (loggedInUser.getRoleId()){
//                case User.SUPERVISOR:
            workerSpinner.setSelection(Arrays.asList(workerIds.toArray()).indexOf(selectedTask.getWorker().getId()));
//                break;
//                case User.NURSE:
//                workerSpinner.setSelection(Arrays.asList(workerIds.toArray()).indexOf(selectedTask.get));
//                break;
//            }
        }

    }

    private void assignTask(String api_url){
        switch (loggedInUser.getRoleId()) {
            case User.SUPERVISOR:
                try {
                    if (selectedTask != null) {
                        taskData.put(getString(R.string.id), "" + selectedTask.getId());
                    }
                    taskData.put(getString(R.string.supervisor_id), "" + loggedInUser.getId());
                    if (selfAssignCheck.isChecked()) {
                        taskData.put(getString(R.string.worker_id), "" + loggedInUser.getId());
                    } else if (!selfAssignCheck.isChecked()) {
                        taskData.put(getString(R.string.worker_id), "" +
                                workerIds.get(InputValidator.validateSpinner(workerSpinner, -1)));
                    }

                    taskData.put(getString(R.string._task_type), taskTypeSpinner.getSelectedItem().toString());
                    taskData.put(getString(R.string.task_title), InputValidator.validateText(taskTitleEdit, 2));
                    taskData.put(getString(R.string.task_description), InputValidator.validateText(taskDescriptionEdit, 3));
                    taskData.put(getString(R.string.task_location), InputValidator.validateText(locationEdit, 2));
                    taskData.put(getString(R.string.full_address), InputValidator.validateText(fullAddressEdit, 5));

                    taskData.put(getString(R.string.state), InputValidator.validateText(stateSpinner.getSelectedItem().toString(), 2));
                    taskData.put(getString(R.string.lga), lgas.get(InputValidator.validateSpinner(lgaSpinner, -1)));
                    taskData.put(getString(R.string.state), InputValidator.validateText(stateSpinner.getSelectedItem().toString(), 2));
                    taskData.put(getString(R.string.contact_full_name), InputValidator.validateText(contactFullNameEdit, 2));
                    taskData.put(getString(R.string.date_given), InputValidator.validateText(dateEditText, 8).replaceAll("/", "-"));//.toString());
                    taskData.put(getString(R.string.time_given), InputValidator.validateText(timeEditText, 5));
                    taskData.put(getString(R.string.institution_name), InputValidator.validateText(institutionNameEdit, 2));
                    taskData.put(getString(R.string.contact_number), InputValidator.validateText(contactNumberEdit, 11));

                    taskData.put(getString(R.string.latitude), String.format("%f", taskCoordinates.latitude));
                    taskData.put(getString(R.string.longitude), String.format("%f", taskCoordinates.longitude));

                    sendToNetwork(taskData, api_url);
                } catch (InputValidator.InvalidInputException e) {
                    Toast.makeText(cxt, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;

            case User.NURSE:
                try {
                    if (selectedTask != null) {
                        taskData.put(getString(R.string.id), "" + selectedTask.getId());
                    }
                    taskData.put(getString(R.string.supervisor_id), "" + loggedInUser.getSupervisorId());
                    taskData.put(getString(R.string.worker_id), "" + workerIds.get(InputValidator.validateSpinner(workerSpinner, -1)));
                    taskData.put(getString(R.string._task_type), taskTypeSpinner.getSelectedItem().toString());
                    taskData.put(getString(R.string.task_title), InputValidator.validateText(taskTitleEdit, 2));
                    taskData.put(getString(R.string.task_description), InputValidator.validateText(taskDescriptionEdit, 3));
                    taskData.put(getString(R.string.task_location), InputValidator.validateText(locationEdit, 2));
                    taskData.put(getString(R.string.full_address), InputValidator.validateText(fullAddressEdit, 5));

                    taskData.put(getString(R.string.state), InputValidator.validateText(stateSpinner.getSelectedItem().toString(), 2));
                    taskData.put(getString(R.string.lga), lgas.get(InputValidator.validateSpinner(lgaSpinner, -1)));
                    taskData.put(getString(R.string.state), InputValidator.validateText(stateSpinner.getSelectedItem().toString(), 2));
                    taskData.put(getString(R.string.contact_full_name), InputValidator.validateText(contactFullNameEdit, 2));
                    taskData.put(getString(R.string.date_given), InputValidator.validateText(dateEditText, 8).replaceAll("/", "-"));//.toString());
                    taskData.put(getString(R.string.time_given), InputValidator.validateText(timeEditText, 5));
                    taskData.put(getString(R.string.institution_name), InputValidator.validateText(institutionNameEdit, 2));
                    taskData.put(getString(R.string.contact_number), InputValidator.validateText(contactNumberEdit, 11));

                    taskData.put(getString(R.string.latitude), String.format("%f", taskCoordinates.latitude));
                    taskData.put(getString(R.string.longitude), String.format("%f", taskCoordinates.longitude));

                    sendToNetwork(taskData, api_url);
                } catch (InputValidator.InvalidInputException e) {
                    Toast.makeText(cxt, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }



    }

    @SuppressLint("StaticFieldLeak")
    private void sendToNetwork(final HashMap<String, String> taskData, String api_url) {
        new android.os.AsyncTask<String,Void,String>(){

            @Override
            protected String doInBackground(String... strings) {
                return Network.performPostCall(strings[0],taskData);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast toast = Toast.makeText(cxt,"Please wait..",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s == null){
                    return;
                }

                try {

                    JSONObject obj = new JSONObject(s);
                    if(obj.getInt(getString(R.string.statusCode)) == Entity.STATUS_OK){
                        AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                        if(selectedTask == null) {
                            alertDialog.setMessage("Task Added Successfully");
                        }else{
                            alertDialog.setMessage("Task Edited Successfully");
                        }
                        alertDialog.show();

                        //Toast.makeText(cxt,"Task Added Successfully",Toast.LENGTH_LONG).show();
                        clearAllFields();
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                        alertDialog.setMessage(obj.getString("message"));
                        alertDialog.show();
                      //  Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();

                     //   Toast.makeText(cxt,obj.getString(getString(R.string.message)),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                    alertDialog.setMessage(e.getMessage());
                    alertDialog.show();
                    //e.printStackTrace();
                 //   Log.d("test", s);
                    System.out.println(s);
                   // Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();

                }

            }
        }.execute(api_url);
    }

    private void clearAllFields() {
        Util.clearEditTexts(dateEditText, timeEditText, taskTitleEdit,
                taskDescriptionEdit, institutionNameEdit,
                fullAddressEdit, contactFullNameEdit, contactNumberEdit, locationEdit);
//        Util.clearSpinner(workerSpinner, stateSpinner, lgaSpinner, taskTypeSpinner);
        Util.clearSpinner(stateSpinner, lgaSpinner, taskTypeSpinner);
    }
}

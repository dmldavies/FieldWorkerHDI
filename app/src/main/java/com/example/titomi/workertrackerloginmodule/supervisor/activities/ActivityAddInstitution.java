package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.example.titomi.workertrackerloginmodule.supervisor.util.Network.backgroundTask;

public class ActivityAddInstitution extends AppCompatActivity implements View.OnClickListener {

    User loggedInUser;
    ProgressDialog progressDialog;
    private EditText institutionNameEdit,
             locationEdit;
    private Spinner stateSpinner, lgaSpinner, institutionType;

    private TextView selectLocationText;
    private Button addInstitutionBut;
    String selectedState;
    private ProgressBar lgaLoading;
    private Task selectedTask;
    ArrayList<String> lgas = new ArrayList<>();
    private LatLng institutionCoord;
    private Context cxt;
    private Institutions selectedInstitution;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_institution);

        cxt = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lgaSpinner = findViewById(R.id.lga);
        selectLocationText = findViewById(R.id.selectLocation);

        institutionNameEdit = findViewById(R.id.institution);
        stateSpinner = findViewById(R.id.state);
        addInstitutionBut = findViewById(R.id.addButton);
        institutionType = findViewById(R.id.institutionType);
        locationEdit = findViewById(R.id.location);
        lgaLoading = findViewById(R.id.lgaLoading);



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
        selectLocationText.setOnClickListener(this);
        addInstitutionBut.setOnClickListener(this);



        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
            selectedInstitution = (Institutions)extras.getSerializable(getString(R.string.selected_institution));
            if(selectedInstitution != null){
                setupView(selectedInstitution);
            }
        }
    }

    private void setupView(Institutions selectedInstitution) {
        String[] insType = getResources().getStringArray(R.array.institutionType);
        institutionType.setSelection(Arrays.asList(insType).indexOf(selectedInstitution.getType()));
        String [] states = getResources().getStringArray(R.array.states);
        stateSpinner.setSelection(Arrays.asList(states).indexOf(selectedInstitution.getState()));
        locationEdit.setText(selectedInstitution.getAddress());
        institutionCoord = new LatLng(selectedInstitution.getLatitude(),
                selectedInstitution.getLongitude());
        institutionNameEdit.setText(selectedInstitution.getName());

        addInstitutionBut.setText(getString(R.string.update));
        addInstitutionBut.setTag(getString(R.string.update));





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
            case R.id.selectLocation:
                Util.showPlacesPicker(cxt);
                break;
            case R.id.addButton:
                try {
                    String getData;

                        getData = getData();

                    InstitutionNetwork network = new InstitutionNetwork();
                    String actionUrl;
                    if(selectedInstitution == null){
                        actionUrl = getString(R.string.add_institution_url);
                    }else {
                        actionUrl = getString(R.string.edit_institution_url);
                    }
                    network.execute(getString(R.string.api_url)+actionUrl+getData);
                } catch (InputValidator.InvalidInputException | UnsupportedEncodingException e) {
                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private String getData() throws InputValidator.InvalidInputException, UnsupportedEncodingException {

        String _institutionType = institutionType.getSelectedItem().toString();
        String selectedState = Entity.urlEncode(InputValidator.validateText(stateSpinner.getSelectedItem().toString(), 2));
        String lga =  Entity.urlEncode(lgas.get(InputValidator.validateSpinner(lgaSpinner, -1)));
        String location =  Entity.urlEncode(InputValidator.validateText(locationEdit, 2));
        String institutionName = Entity.urlEncode(InputValidator.validateText(institutionNameEdit, 2));


        String getData = String.format(Locale.ENGLISH,"?key=%s&" +
                "name=%s" +
                "&longitude=%f" +
                "&latitude=%f" +
                "&address=%s" +
                "&state=%s" +
                "&lga=%s" +
                "&type=%s",getString(R.string.field_worker_api_key),
                institutionName,
                institutionCoord.longitude,
                institutionCoord.latitude,
                location,
                selectedState,
                lga,
                _institutionType
                );

        StringBuilder sb = new StringBuilder(getData);
        if(selectedInstitution != null){
            sb.append("&id=").append(selectedInstitution.getId());
        }
        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        switch (requestCode){
            case Util.PICK_PLACES:

                if(data == null) return;
                Place place = PlacePicker.getPlace(this, data);
                locationEdit.setText(place.getAddress());
                institutionCoord = place.getLatLng();
                break;
        }
    }

    private void setupLgaSpinner(){
        ArrayAdapter<String> lgaAdapter = new ArrayAdapter<>(cxt, android.R.layout.simple_spinner_dropdown_item, lgas);
        lgaSpinner.setAdapter(lgaAdapter);

        if(selectedInstitution !=null) {
            Toast.makeText(cxt,selectedInstitution.getLga(),Toast.LENGTH_LONG).show();
            lgaSpinner.setSelection(Arrays.asList(lgas.toArray()).indexOf(selectedInstitution.getLga().trim()));
        }


    }

    @SuppressLint("StaticFieldLeak")
    private void loadLgaSpinner(String state) throws UnsupportedEncodingException {
        selectedState = Network.encodeUrl(state);

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
                lgaSpinner.setEnabled(true);

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

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(cxt);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private class InstitutionNetwork extends android.os.AsyncTask<String,Void,String>{

        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showProgressDialog();
            /*progressDialog = new ProgressDialog(cxt);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();

            /*if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }*/

            if(s == null || s.equalsIgnoreCase("null")){
                return;
            }

            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("statusCode") == Entity.STATUS_OK){
                     finish();
                     String msg = selectedInstitution == null ? "Institution added successfully" : "Institution updated successfully";
                     Toast.makeText(cxt,msg,Toast.LENGTH_LONG).show();
                }else{
                    String msg = selectedInstitution == null ? "Error adding institution" : "Error updating institution";
                    Toast.makeText(cxt,msg,Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

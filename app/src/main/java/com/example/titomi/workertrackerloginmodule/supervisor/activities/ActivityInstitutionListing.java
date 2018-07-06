package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;
import com.example.titomi.workertrackerloginmodule.supervisor.Institutions;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.example.titomi.workertrackerloginmodule.supervisor.util.Network.backgroundTask;

public class ActivityInstitutionListing extends AppCompatActivity implements AdapterView.OnItemLongClickListener,AdapterView.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener {

    private ListView institutionList;
    private Spinner statesSpinner;
    private User loggedInUser;
    private Context cxt;
    private SwipeRefreshLayout swipeRefreshLayout;

    String callingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_listing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        cxt = this;
       /* statesSpinner = findViewById(R.id.stateSpinner);
        statesSpinner.setOnItemSelectedListener(this);*/
        institutionList = findViewById(R.id.institutionList);
        institutionList.setOnItemClickListener(this);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(cxt,ActivityAddInstitution.class);
            i.putExtra(getString(R.string.loggedInUser),loggedInUser);
            startActivity(i);
        });
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
            callingActivity = extras.getString(getString(R.string.calling_activity));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(loggedInUser.getRoleId() != User.SUPERVISOR){
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInstitutions(null,null,null);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(callingActivity == null ) return;
        if( !callingActivity.equalsIgnoreCase(ActivityAssignTask.class.getName())) return;
        Institutions inst = (Institutions)parent.getItemAtPosition(position);
        //if(loggedInUser.getRoleId() == User.NURSE){
            Intent i = new Intent();
            i.putExtra(getString(R.string.selected_institution),inst);
            setResult(RESULT_OK,i);
            finish();
      //  }
    }

    private void deleteAction(long id) {
        AlertDialog dialog = new AlertDialog.Builder(cxt).create();
         dialog.setMessage("Are you sure you want to delete this institution?");
         String deleteUrl = getString(R.string.api_url)+
                 getString(R.string.delete_institution_url)+
                 String.format(Locale.ENGLISH,
                         "?key=%s&id=%d",
                         getString(R.string.field_worker_api_key),
                         id);
         dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                 "Yes", (dialog1,
                         which) -> new DeleteNetwork().execute(deleteUrl));

         dialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No",(dialog2,which)->dialog.dismiss());

         dialog.show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getInstitutions(@Nullable String state,@Nullable String lga, @Nullable String type){
        String url;
        if(state == null && lga == null && type == null){
            url = String.format("?key=%s&view=all",getString(R.string.field_worker_api_key));
        }else{
            url = String.format("?key=%s&view=search&state=%s&lga=%s",getString(R.string.field_worker_api_key),state,lga);
        }
        InstitutionsNetwork network = new InstitutionsNetwork();
        network.execute(getString(R.string.api_url)+getString(R.string.view_institutions_api_url)+url);

    }

    @Override
    public void onRefresh() {
        getInstitutions(null,null,null);
    }

    private void setInstitutionAdapter(){
    ArrayAdapter    arrayAdapter = new ArrayAdapter<Institutions>(cxt,R.layout.institutions_single_item_layout,institutionsArrayList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Institutions inst = institutionsArrayList.get(position);
                convertView = View.inflate(cxt,R.layout.institutions_single_item_layout,null);

                LinearLayout.LayoutParams params = new  LinearLayout.LayoutParams( CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,20,0,20);
                convertView.setLayoutParams(params);
                TextView instName = convertView.findViewById(R.id.institutionName);
                TextView stateLga = convertView.findViewById(R.id.institutionStateLga);
                TextView insType = convertView.findViewById(R.id.institutionType);

                instName.setText(inst.getName());
                stateLga.setText(String.format("%s %s",inst.getLga(),inst.getState()));
                insType.setText(inst.getType());
                return convertView;
            }
        };

        institutionList.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        Institutions inst = (Institutions)parent.getItemAtPosition(position);
        AlertDialog dialog = new AlertDialog.Builder(cxt).create();
        View v = View.inflate(cxt,R.layout.institution_listing_long_click_menu_layout,null);

        TextView edit = v.findViewById(R.id.edit);
        TextView delete = v.findViewById(R.id.delete);

        edit.setOnClickListener(myView->{
            Intent i = new Intent(cxt,ActivityAddInstitution.class);
            i.putExtra(getString(R.string.loggedInUser),loggedInUser);
            i.putExtra(getString(R.string.selected_institution),inst);
            startActivity(i);
            dialog.dismiss();
        });

        delete.setOnClickListener(de->deleteAction(inst.getId()));
        dialog.setView(v);
        dialog.show();

        return false;
    }

    private class InstitutionsNetwork extends android.os.AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);
            if(s == null || s.equals("null")) {

                Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), "No institutions found", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
            try {
                JSONArray jsonArray = new JSONArray(s);

                institutionsArrayList.clear();
                for(int i = 0; i<jsonArray.length(); i++ ){
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Institutions ins = new Institutions(obj.getString("name"),
                            obj.getString("state"),
                            obj.getString("address"),
                            obj.getString("lga").trim(),
                            obj.getString("type"),
                            obj.getDouble("longitude"),
                            obj.getDouble("latitude"));
                    ins.setId(obj.getLong("id"));
                    ins.setStatusCode(Entity.STATUS_OK);

                    institutionsArrayList.add(ins);
                    setInstitutionAdapter();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case android.R.id.home:
                finish();
                break;
            case R.id.search:

                    showSearchPopup();

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void showSearchPopup() {
        View view = getLayoutInflater().inflate(R.layout.search_institution_layout,null);
        final Spinner stateSpinner = view.findViewById(R.id.state);
        final Spinner lgaSpinner = view.findViewById(R.id.lga);
        final Button searchActionButton = view.findViewById(R.id.view_report_action_button);
        final AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
       // BaseAdapter adapter = (BaseAdapter)stateSpinner.getAdapter();



        searchActionButton.setOnClickListener((view2)->{
            alertDialog.dismiss();

            try {
                String state = Entity.urlEncode(stateSpinner.getSelectedItem().toString());
                String lga = Entity.urlEncode(lgaSpinner.getSelectedItem().toString());
                getInstitutions(state,lga,null);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        });
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0) return;
                try {
                    loadLgaSpinner((String)parent.getItemAtPosition(position));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
            ArrayList<String> lgas = new ArrayList<>();

            private void setupLgaSpinner(){
                ArrayAdapter<String> lgaAdapter = new ArrayAdapter<>(cxt, android.R.layout.simple_spinner_dropdown_item, lgas);
                lgaSpinner.setAdapter(lgaAdapter);

                }





            @SuppressLint("StaticFieldLeak")
            private void loadLgaSpinner(String state) throws UnsupportedEncodingException {
              String  selectedState = Network.encodeUrl(state);
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
                        view.findViewById(R.id.lgaLoading).setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        view.findViewById(R.id.lgaLoading).setVisibility(View.INVISIBLE);

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
                         //   Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }





                }.execute(getString(R.string.api_url)+getString(R.string.state_api_url)+"?view=lga&state="+selectedState);
            }

        });
        alertDialog.setView(view);
        alertDialog.show();
        //statesSpinner.removeViewAt(0);

    }

    private class DeleteNetwork extends android.os.AsyncTask<String,Void,String>{

        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(cxt);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            if(s == null || s.equalsIgnoreCase("null")){
                return;
            }

            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("statusCode") == Entity.STATUS_OK){
                    finish();
                    String msg = "Institution delete successfully";
                    Toast.makeText(cxt,msg,Toast.LENGTH_LONG).show();
                }else{
                    String msg = "Error deleting institution";
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
    ArrayList<Institutions> institutionsArrayList = new ArrayList<>();



}

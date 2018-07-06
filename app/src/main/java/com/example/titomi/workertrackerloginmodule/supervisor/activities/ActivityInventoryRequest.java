package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Products;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.titomi.workertrackerloginmodule.supervisor.util.Network.backgroundTask;

public class ActivityInventoryRequest extends AppCompatActivity
        implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    Context cxt;

    private EditText commentEdit,quantityEdit;
    private Spinner productSpinner,distributorSpinner;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_request);
        cxt = this;

        quantityEdit = findViewById(R.id.quantity);
        commentEdit = findViewById(R.id.commentField);
        distributorSpinner = findViewById(R.id.distributorSpinner);
        productSpinner = findViewById(R.id.productSpinner);
        findViewById(R.id.submit).setOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));

            if (loggedInUser.getRoleId() != User.SUPERVISOR) {
                hideDistributor();
            }
        }
    }

    private void hideDistributor() {
        distributorSpinner.setVisibility(View.GONE);
        findViewById(R.id.distributorLoading).setVisibility(View.GONE);
        findViewById(R.id.distributorText).setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadDistributorSpinner();
        loadProductsSpinner();
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
    public void onClick(View v) {

        if(!NetworkChecker.haveNetworkConnection(cxt))return;
        HashMap<String,String> postData = new HashMap<>();
        postData.put(getString(R.string.user_id), "" + loggedInUser.getId());
        if (loggedInUser.getRoleId() == User.NURSE) {
            postData.put(getString(R.string.distributor_id), "" + loggedInUser.getSupervisorId());
        } else if (loggedInUser.getRoleId() == User.SUPERVISOR) {
            postData.put(getString(R.string.distributor_id), "" + distributorIds.get(distributorSpinner.getSelectedItemPosition()));
        }
        try {
            postData.put(getString(R.string.quantity), InputValidator.validateText(quantityEdit,1));

        postData.put(getString(R.string.message).toLowerCase(),commentEdit.getText().toString());
            postData.put(""+getString(R.string.product_id),""+productsList.get(productSpinner.getSelectedItemPosition()).getId());
        postData.put(getString(R.string.product_name),productSpinner.getSelectedItem().toString());
        sendToServer(postData);
        } catch (InputValidator.InvalidInputException e) {
            Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    private void loadDistributorSpinner(){
        //  final ProgressDialog d = new ProgressDialog(cxt);
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
                findViewById(R.id.distributorLoading).setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                findViewById(R.id.distributorLoading).setVisibility(View.GONE);
                // super.onPostExecute(s);

                //  Toast.makeText(cxt,s,Toast.LENGTH_LONG).show();
                //  d.dismiss();
                if(s == null){
                    //  Toast.makeText(cxt,"returned null",Toast.LENGTH_LONG).show();
                    return;

                }

                try {
                    JSONArray array = new JSONArray(s);

                    distributorIds.clear();
                    distributorNames.clear();
                    // Toast.makeText(cxt,""+array.length(),Toast.LENGTH_LONG).show();
                    for(int i = 0; i< array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        distributorIds.add(obj.getLong("id"));
                        distributorNames.add(String.format("%s %s",
                                obj.getString("first_name"),
                                obj.getString("last_name")));


                    }

                    setupDistributorSpinner();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

            //TODO: get the supervisor id programmatically

        }.execute(String.format("%s?key=%s&view=all",getString(R.string.api_url)+getString(R.string.distributor_api_url),getString(R.string.field_worker_api_key)));
    }

    private void loadProductsSpinner(){
        //  final ProgressDialog d = new ProgressDialog(cxt);
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
                findViewById(R.id.distributorLoading).setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                findViewById(R.id.distributorLoading).setVisibility(View.GONE);
                // super.onPostExecute(s);

                //  Toast.makeText(cxt,s,Toast.LENGTH_LONG).show();
                //  d.dismiss();
                if(s == null){
                    //  Toast.makeText(cxt,"returned null",Toast.LENGTH_LONG).show();
                    return;

                }

                try {
                    JSONArray array = new JSONArray(s);

                    productsList.clear();

                    // Toast.makeText(cxt,""+array.length(),Toast.LENGTH_LONG).show();
                    for(int i = 0; i< array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        Products prd = new Products();
                        prd.setId(obj.getLong("id"));
                        prd.setName(obj.getString("name"));
                        prd.setPrice(obj.getLong("price"));

                        productsList.add(prd);
                    }

                    setupProductsSpinner();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }



        }.execute(String.format("%s?key=%s&view=all",getString(R.string.api_url)+getString(R.string.products_url),getString(R.string.field_worker_api_key)));
    }
    private void setupDistributorSpinner(){
        if(distributorNames.size() == 0){
            return;
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(cxt,android.R.layout.simple_spinner_dropdown_item, distributorNames);
        distributorSpinner.setAdapter(spinnerAdapter);
    }

    private void setupProductsSpinner(){
        if(productsList.size() == 0){
            return;
        }

        ArrayAdapter<Products> spinnerAdapter = new ArrayAdapter<Products>(cxt,android.R.layout.simple_spinner_dropdown_item, productsList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Products prd = productsList.get(position);
                LayoutInflater inflater = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.textview_layout,null);

                TextView textView = convertView.findViewById(R.id.textView);
                textView.setText(prd.getName());
                return convertView;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return getDropDownView(position,convertView,parent);
            }
        };
        productSpinner.setAdapter(spinnerAdapter);
    }

    private void sendToServer(final HashMap<String,String> postData){
        new android.os.AsyncTask<String,Void,String>(){

            @Override
            protected String doInBackground(String... strings) {
                return Network.performPostCall(strings[0],postData);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                if(s == null) return;

                try {
                    //JSONArray jsonArray = new JSONArray(s);
                    JSONObject obj = new JSONObject(s);
                   // JSONObject obj =  jsonArray.getJSONArray(0).getJSONObject(0);//jsonArray.getJSONObject(0);

                    if(obj.getInt(getString(R.string.statusCode)) == 0){
                        Toast.makeText(cxt,obj.getString(getString(R.string.message).toLowerCase()),Toast.LENGTH_LONG).show();
                        Util.clearEditTexts(commentEdit,quantityEdit);
                        Util.clearSpinner(distributorSpinner);
                        Util.clearSpinner(productSpinner);
                    }
                } catch (JSONException e) {
                    System.out.println(s);
                    AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                    alertDialog.setMessage(e.getMessage());
                    alertDialog.show();
                    System.out.println(s);
                   // Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }.execute(getString(R.string.api_url)+getString(R.string.inventory_request_api)+"?key="+getString(R.string.field_worker_api_key));
    }

    private ArrayList<Long> distributorIds = new ArrayList<>();
    private ArrayList<Products> productsList = new ArrayList<>();
   private ArrayList<String> distributorNames = new ArrayList<>();
}

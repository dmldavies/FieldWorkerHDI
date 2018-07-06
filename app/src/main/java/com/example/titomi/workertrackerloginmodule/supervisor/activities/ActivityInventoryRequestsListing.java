package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Inventory;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by NeonTetras on 27-Feb-18.
 */

public class ActivityInventoryRequestsListing extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private FloatingActionButton actionButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Context cxt;
    private User loggedInUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_request_list_layout);

        cxt = this;
        listView = findViewById(R.id.inventoryRequestList);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        actionButton = findViewById(R.id.newRequest);
        actionButton.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequests();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));

            if(loggedInUser != null && loggedInUser.getRoleId() != User.SUPERVISOR){
                actionButton.setVisibility(View.GONE);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
     loadRequests();
    }

    private void loadRequests(){
        new InventoryRequestNetwork().execute(getString(R.string.api_url)+getString(R.string.inventory_view_requests_url)+"?key="+getString(R.string.field_worker_api_key)+"&view=supervisor_requests&id="+ loggedInUser.getId());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(loggedInUser != null) {
            outState.putSerializable(getString(R.string.loggedInUser), loggedInUser);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            loggedInUser = (User) savedInstanceState.getSerializable(getString(R.string.loggedInUser));
        }

    }



    @Override
    public void onClick(View v) {
        startActivity(new Intent(cxt, ActivityInventoryRequest.class)
                .putExtra(getString(R.string.loggedInUser),
                        loggedInUser));
    }

    private class InventoryRequestNetwork extends android.os.AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String s) {
            swipeRefreshLayout.setRefreshing(false);
            if(s == null){
                return;
            }

            try{
                JSONArray jsonArray = new JSONArray(s);
                //Toast.makeText(cxt,jsonArray.getJSONObject(0).getString("name"),Toast.LENGTH_SHORT).show();
                requestsList.clear();
                for(int i = 0; i<jsonArray.length(); i++){

                    JSONObject obj = jsonArray.getJSONObject(i);
                    Inventory.InventoryRequests requests = new Inventory.InventoryRequests();
                    requests.setId(obj.getInt("id"));
                    requests.setName(obj.getString("name"));
                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy/M/dd H:m:s");
                    requests.setCreated(dtf.parse(obj.getString("created").replaceAll("-","/")));
                    requests.setQuantity(obj.getInt("quantity"));
                    requests.setSupervisorMessage(obj.getString("supervisorMessage"));

                    User distributor = new User();
                    JSONObject dist = obj.getJSONObject("distributor");
                    distributor.setId(dist.getInt("id"));
                    distributor.setName(dist.getString("first_name")+" "+dist.get("last_name"));

                    User supervisor = new User();
                    JSONObject supervisor1 = obj.getJSONObject("supervisor");
                    supervisor.setId(supervisor1.getInt("id"));
                    supervisor.setName(supervisor1.getString("first_name")+" "+dist.get("last_name"));

                    requests.setDistributor(distributor);
                    requests.setSupervisor(supervisor);

                    requestsList.add(requests);


                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                System.err.println(s);
                Log.d(getClass().getName(),e.getMessage());
               // Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
             ArrayAdapter<Inventory.InventoryRequests>
            listAdapter = new ArrayAdapter<Inventory.InventoryRequests>(cxt,R.layout.activity_inventory_request_single_item,requestsList){

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.activity_inventory_request_single_item,null);


                        Inventory.InventoryRequests requests = requestsList.get(position);
                        TextView productText = convertView.findViewById(R.id.productText);
                        TextView distributorText  = convertView.findViewById(R.id.distributorText);
                        TextView quantityText = convertView.findViewById(R.id.quantityText);
                        TextView  commentText  = convertView.findViewById(R.id.commentText);
                        TextView dateTimeText = convertView.findViewById(R.id.dateTimeText);
                        LinearLayout messagesLinearLayout = convertView.findViewById(R.id.messagesLinearLayout);
                        TextView messageText = convertView.findViewById(R.id.messageText);

                     //   Toast.makeText(cxt,requests.getName(),Toast.LENGTH_SHORT).show();
                        productText.setText(requests.getName());
                        distributorText.setText(requests.getDistributor().getName());
                        quantityText.setText(""+requests.getQuantity());
                        commentText.setText(requests.getSupervisorMessage());
                        dateTimeText.setText(DateFormat.getDateTimeInstance().format(requests.getCreated()));


                        return convertView;
                    }
                };
                listView.setAdapter(listAdapter);

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


    private static ArrayList<Inventory.InventoryRequests> requestsList = new ArrayList<>();
}

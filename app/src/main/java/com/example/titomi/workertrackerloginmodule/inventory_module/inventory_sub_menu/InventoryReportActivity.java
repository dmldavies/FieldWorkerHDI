package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Inventory;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityInventoryRequest;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class InventoryReportActivity extends AppCompatActivity implements OnChartValueSelectedListener, View.OnClickListener {

    Toolbar toolbar;
    private int itemBalance;
    private int itemQuantitySold;
    private int itemQuantity;

    private static ListView listView;
    private FloatingActionButton actionButton;
    BarChart barInventoryChart;
    private User loggedInUser;
    private static SwipeRefreshLayout swipeRefreshLayout;
    Context cxt;
    ArrayList<BarEntry> yVals = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);
        cxt = this;
        barInventoryChart = findViewById(R.id.barChartInvt);
        barInventoryChart.getDescription().setEnabled(false);

        listView = findViewById(R.id.inventoryRequestList);
        barInventoryChart.setOnChartValueSelectedListener(this);

        actionButton = findViewById(R.id.newRequest);
        actionButton.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Inventory Report");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
        loadRequests();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(cxt, ActivityInventoryRequest.class)
                .putExtra(getString(R.string.loggedInUser),
                        loggedInUser));
    }

    private class InventoryNetwork extends android.os.AsyncTask<String, Void, String> {

        ArrayList<Task> taskList = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            pb.setVisibility(View.GONE);


            if (s == null) {

                return;
            }

            try {
                JSONObject obj = new JSONObject(s);
                loadChart(obj);

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            }
        }

    }

    private void loadChart(JSONObject obj) throws JSONException {

        itemBalance = obj.getInt("balance");
        itemQuantitySold = obj.getInt("sold");
        itemQuantity = obj.getInt("total");

        yVals.clear();

        BarEntry balanceEntry = new BarEntry(0, itemBalance);
        BarEntry quantityEntry = new BarEntry(2, itemQuantity);
        BarEntry quantitySoldEntry = new BarEntry(1, itemQuantitySold);
        yVals.add(0, balanceEntry);
        yVals.add(1, quantitySoldEntry);
        yVals.add(2, quantityEntry);


        /*yVals.add(new BarEntry(1, itemQuantity));
        yVals.add(new BarEntry(2, itemQuantitySold));*/

        BarDataSet dataSet = new BarDataSet(yVals, "Inventory");


//        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setColors(getResources().getColor(R.color.balanceColor), getResources().getColor(R.color.quantitySoldColor), getResources().getColor(R.color.quantityColor));
        dataSet.setDrawValues(true);

        BarData set = new BarData(dataSet);

        barInventoryChart.setData(set);

        barInventoryChart.invalidate();
        barInventoryChart.setPinchZoom(false);
        barInventoryChart.animateY(500);
        barInventoryChart.getData().setHighlightEnabled(true);

        Legend legend = barInventoryChart.getLegend();
        LegendEntry le = new LegendEntry();
//        legend.setCustom();

    }

    private void loadInventory() {

        String url = "";
        switch (loggedInUser.getRoleId()) {
            case User.SUPERVISOR:
                url = getString(R.string.api_url) + getString(R.string.inventory_view_requests_url) + "?view=user_stock_details&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
            case User.NURSE:
                url = getString(R.string.api_url) + getString(R.string.inventory_view_requests_url) + "?view=user_stock_details&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
        }
        new InventoryNetwork().execute(url);

    }

    private void loadRequests() {
        new InventoryRequestNetwork().execute(getString(R.string.api_url) + getString(R.string.inventory_view_requests_url) + "?key=" + getString(R.string.field_worker_api_key) + "&view=user_requests&id=" + loggedInUser.getId());
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

        }

        @Override
        protected void onPostExecute(String s) {

            if (s == null) {
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);
                //Toast.makeText(cxt,jsonArray.getJSONObject(0).getString("name"),Toast.LENGTH_SHORT).show();
                requestsList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);
                    Inventory.InventoryRequests requests = new Inventory.InventoryRequests();
                    requests.setId(obj.getInt("id"));
                    requests.setName(obj.getString("name"));
                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy/M/dd H:m:s");
                    requests.setCreated(dtf.parse(obj.getString("created").replaceAll("-", "/")));
                    requests.setQuantity(obj.getInt("quantity"));
                    requests.setSupervisorMessage(obj.getString("supervisorMessage"));
                    requests.setAcknowledged(obj.getInt("acknowledged") != 0);
                    User distributor = new User();
                    JSONObject dist = obj.getJSONObject("distributor");
                    distributor.setId(dist.getInt("id"));
                    distributor.setName(dist.getString("first_name") + " " + dist.get("last_name"));

                    User supervisor = new User();
                    JSONObject supervisor1 = obj.getJSONObject("supervisor");
                    supervisor.setId(supervisor1.getInt("id"));
                    supervisor.setName(supervisor1.getString("first_name") + " " + dist.get("last_name"));

                    requests.setDistributor(distributor);
                    requests.setSupervisor(supervisor);

                    requestsList.add(requests);


                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                System.err.println(s);
                Log.d(getClass().getName(), e.getMessage());
                // Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
            ArrayAdapter<Inventory.InventoryRequests>
                    listAdapter = new ArrayAdapter<Inventory.InventoryRequests>(cxt, R.layout.activity_inventory_request_single_item, requestsList) {

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.activity_inventory_request_single_item, null);


                    Inventory.InventoryRequests requests = requestsList.get(position);
                    TextView productText = convertView.findViewById(R.id.productText);
                    TextView distributorText = convertView.findViewById(R.id.distributorText);
                    TextView quantityText = convertView.findViewById(R.id.quantityText);
                    TextView commentText = convertView.findViewById(R.id.commentText);
                    TextView dateTimeText = convertView.findViewById(R.id.dateTimeText);
                    TextView status = convertView.findViewById(R.id.inventoryStatusText);


                    //   Toast.makeText(cxt,requests.getName(),Toast.LENGTH_SHORT).show();
                    productText.setText(requests.getName());
                    distributorText.setText(requests.getDistributor().getName());
                    quantityText.setText("" + requests.getQuantity());
                    commentText.setText(requests.getSupervisorMessage());

                    if(requests.isAcknowledged()){
                        status.setText(getString(R.string.approved));
                        status.setTextColor(getResources().getColor(R.color.md_green_700));
                    }
                    //if(requests.get
                    dateTimeText.setText(DateFormat.getDateTimeInstance().format(requests.getCreated()));


                    return convertView;
                }
            };
            listView.setAdapter(listAdapter);

        }

    }

    private ArrayList<Inventory.InventoryRequests> requestsList = new ArrayList<>();
}

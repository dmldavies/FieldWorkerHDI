package com.example.titomi.workertrackerloginmodule.inventory_module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu.InventoryReportActivity;
import com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu.ReceivedInventoryActivity;
import com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu.RemittanceRecordsActivity;
import com.example.titomi.workertrackerloginmodule.report_module.ActivityWorkerReportListing;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityInventoryRequestsListing;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {

    CardView product_request, received_inventory, remittance_record, inventory_report, sales_report;
    Toolbar toolbar;


    User loggedInUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Inventory Manager");

        product_request = findViewById(R.id.product_request);
        received_inventory = findViewById(R.id.received_inventory);
        remittance_record = findViewById(R.id.remittance_record);
        inventory_report = findViewById(R.id.inventory_report);
        sales_report = findViewById(R.id.sales_report);

        if(getIntent().getExtras() != null){
            loggedInUser = (User) getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }
        product_request.setOnClickListener(this);
        received_inventory.setOnClickListener(this);
        remittance_record.setOnClickListener(this);
        inventory_report.setOnClickListener(this);
        sales_report.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent i = null;

        switch (v.getId()) {
            case R.id.product_request:
                i = new Intent(this, ActivityInventoryRequestsListing.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);
                startActivity(i);
                break;
            case R.id.received_inventory:
                i = new Intent(this, ReceivedInventoryActivity.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);

                break;
            case R.id.remittance_record:

                i = new Intent(this, RemittanceRecordsActivity.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);


                break;
            case R.id.inventory_report:
                i = new Intent(this, InventoryReportActivity.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);

                break;
            case R.id.sales_report:
                i = new Intent(this, ActivityWorkerReportListing.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);

                break;
            default:
                break;
        }
        if(i != null) {
            startActivity(i);
        }
    }
}

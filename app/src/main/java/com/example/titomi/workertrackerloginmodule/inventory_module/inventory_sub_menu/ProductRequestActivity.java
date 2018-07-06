package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;

import java.util.ArrayList;
import java.util.List;

public class ProductRequestActivity extends AppCompatActivity {

    Button request;
    LinearLayout linearLayout;
    EditText remark;
    Toolbar toolbar;
    ImageButton addView;

    List<String> productTypeList, productSizeList;
    ArrayAdapter<String> productTypeAdapter, productSizeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_request);

        request = findViewById(R.id.button);
        addView = findViewById(R.id.imageButton);
        linearLayout = findViewById(R.id.linearLayout);
        remark = findViewById(R.id.editText);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductRequestActivity.this, "Sending Request...", Toast.LENGTH_SHORT).show();
            }
        });

        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view  = layoutInflater.inflate(R.layout.product_request_row, null);
                EditText itemQuantity = view.findViewById(R.id.editText2);
                Spinner productType = view.findViewById(R.id.spinner3);

                productTypeList = new ArrayList<String>();
                productTypeList.add("Always Sanitary Pads");
                productTypeList.add("Pampers");
                productTypeAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, productTypeList){
                    public View getView(int position, View convertedView, ViewGroup parent){
                        View productView = super.getView(position, convertedView, parent);
                        return productView;
                    }

                    public View getDropDownView(int position, View convertedView, ViewGroup parent){
                        View productView = super.getDropDownView(position, convertedView, parent);
                        return productView;
                    }
                };
                productTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productType.setAdapter(productTypeAdapter);
                productType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                Spinner productSize = view.findViewById(R.id.spinner2);
                productSizeList = new ArrayList<String>();
                productSizeList.add("Always Sanitary Pads");
                productSizeList.add("Pampers");
                productSizeAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, productSizeList){
                    public View getView(int position, View convertedView, ViewGroup parent){
                        View productView = super.getView(position, convertedView, parent);
                        return productView;
                    }

                    public View getDropDownView(int position, View convertedView, ViewGroup parent){
                        View productView = super.getDropDownView(position, convertedView, parent);
                        return productView;
                    }
                };
                productSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productSize.setAdapter(productSizeAdapter);
                productSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                Button removeView = view.findViewById(R.id.remove);
                removeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view1) {
                        ((LinearLayout)view.getParent()).removeView(view);
                    }
                });
                linearLayout.addView(view);
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Inventory Manager");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

}

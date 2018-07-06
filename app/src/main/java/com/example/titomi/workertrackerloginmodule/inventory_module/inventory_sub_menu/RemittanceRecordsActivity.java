package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Remittance;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RemittanceRecordsActivity extends AppCompatActivity {
    Toolbar toolbar;

    SwipeRefreshLayout swipeRefreshLayout;
    User loggedInUser;
    ListView listView;
    Context cxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance_records);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        cxt = this;

        listView = findViewById(R.id.listView);
        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRemittanceRecord(loggedInUser);
            }
        });

       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRemittanceRecord(loggedInUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
         getMenuInflater().inflate(R.menu.menu_remittance_list_activity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case R.id.remit:
                Intent i  = new Intent(this,ActivityAddRemit.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getRemittanceRecord(User user){
        String viewParam = "";

        switch (user.getRoleId()){
            case User.NURSE:
                viewParam = "worker&id="+user.getId();
                break;
            case User.SUPERVISOR:
                viewParam = "supervisor&id="+user.getId();
        }

        new RemittanceRecordNetwork().execute(getString(R.string.api_url)+getString(R.string.view_remittance_url)+"?key="+getString(R.string.field_worker_api_key)+"&view="+viewParam);
    }

    private class RemittanceRecordNetwork extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            swipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            swipeRefreshLayout.setRefreshing(false);

            if(s == null){
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);

                if(jsonArray.length() == 0){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutRemittanceRecord),"No record available",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                remList.clear();
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    Remittance rem = new Remittance();
                    if(loggedInUser.getRoleId() == User.NURSE) {

                        JSONObject workerObj = obj.getJSONObject("worker");
                        JSONObject supervisorObj = obj.getJSONObject("supervisor");
                        User worker = new User();
                        worker.setUserLevel(workerObj.getInt("roleId"));
                        worker.setUserLevelText(workerObj.getString("role"));
                        worker.setFeaturedImage(workerObj.getString("photo"));
                        worker.setName(String.format("%s %s",workerObj.getString("first_name"),supervisorObj.getString("last_name")));

                        User supervisor = new User();
                        supervisor.setUserLevel(supervisorObj.getInt("roleId"));
                        supervisor.setUserLevelText(supervisorObj.getString("role"));
                        supervisor.setFeaturedImage(supervisorObj.getString("photo"));
                        supervisor.setName(String.format("%s %s",supervisorObj.getString("first_name"),supervisorObj.getString("last_name")));

                        rem.setWorker(worker);
                        rem.setSupervisor(supervisor);
                    }else if(loggedInUser.getRoleId() == User.SUPERVISOR ){
                        JSONObject supervisorObj = obj.getJSONObject("supervisor");

                        User supervisor = new User();
                        supervisor.setUserLevel(supervisorObj.getInt("roleId"));
                        supervisor.setUserLevelText(supervisorObj.getString("role"));
                        supervisor.setFeaturedImage(supervisorObj.getString("photo"));
                        supervisor.setName(String.format("%s %s",
                                supervisorObj.getString("first_name"),
                                supervisorObj.getString("last_name")));
                        rem.setSupervisor(supervisor);
                    }

                    rem.setAmount(obj.getLong("amount"));
                    rem.setAcknowledged(obj.getInt("acknowledged"));
                    rem.setId(obj.getLong("id"));
                    rem.setProof(obj.getString("proof"));

                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                        rem.setRemittanceDate(dtf.parse(obj.getString("remittanceDate")));
                      // if(rem.setDateAcknowledge(dtf.parse(obj.getString("dateAcknowledge")));



                    remList.add(rem);
                    populateList();

                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                System.err.println(s);
            }
        }
    }

    private void populateList(){



        remAdapter = new ArrayAdapter<Remittance>(cxt,R.layout.remit_single_item_layout,remList) {
            final ArrayList<String> imageList = new ArrayList<>();

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Remittance rem = remList.get(position);
                View view = View.inflate(cxt, R.layout.remit_single_item_layout, null);
                TextView dateRemitted = view.findViewById(R.id.dateRemitted);
                LinearLayout proofMediaLayout = view.findViewById(R.id.proofMediaLayout);
                TextView remittedToText = view.findViewById(R.id.remittedToText);
                TextView amountText = view.findViewById(R.id.amountText);
                TextView approvedText = view.findViewById(R.id.approvedText);


                amountText.setText(String.format("N%s", NumberFormat.getInstance(Locale.getDefault()).format(rem.getAmount())));
                DateFormat dtf = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
                dateRemitted.setText(dtf.format(rem.getRemittanceDate()));
                if (rem.getAcknowledged() == 1) {

                    approvedText.setText(R.string.acknowledged);
                    approvedText.setTextColor(getResources().getColor(R.color.md_green_700));
                } else {
                    approvedText.setText(R.string.pending);
                    approvedText.setTextColor(getResources().getColor(R.color.md_yellow_700));
                }

                if (rem.getWorker() == null) {
                    remittedToText.setText(R.string.admin);
                } else {
                    remittedToText.setText(rem.getSupervisor().getName());
                }


                if (rem.getProof() != null && !rem.getProof().isEmpty()) {
                    String[] imgs = rem.getProof().split(",");
                    for (String im : imgs) {
                        DrawableManager drm = new DrawableManager();
                        View viewImage = getLayoutInflater().inflate(R.layout.report_images_single_item, null);
                        final FrameLayout loadingFrame = viewImage.findViewById(R.id.loadingImageFrame);
                        final ImageView reportImage = viewImage.findViewById(R.id.reportImage);
                        String fullImageUrl = getString(R.string.server_url) + im;

                        final String imageName = ImageUtils.getImageNameFromUrlWithExtension(fullImageUrl);
                        //Toast.makeText(cxt,imageName,Toast.LENGTH_LONG).show();
                        final ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(new Remittance());
                        if (storage.imageExists(imageName)) {
                            if (storage.getImage(imageName) != null) {
                                reportImage.setImageURI(Uri.parse(storage.getImage(imageName).getAbsolutePath()));
                                reportImage.setOnClickListener(view1 -> Util.viewImages(cxt, reportImage, imageList));
                                imageList.add(storage.getImage(imageName).getAbsolutePath());
                            }
                        } else {


                            @SuppressLint("StaticFieldLeak")
                            final ImageUtils.GetImages getImages = new ImageUtils.GetImages(new Remittance(), fullImageUrl, imageName) {
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    loadingFrame.setVisibility(View.VISIBLE);
                                }

                                @Override
                                protected void onPostExecute(Object obj) {
                                    super.onPostExecute(obj);

                                    reportImage.setImageBitmap((Bitmap) obj);
                                    if (storage.getImage(imageName) == null) return;

                                    imageList.add(storage.getImage(imageName).getAbsolutePath());

                                    loadingFrame.setVisibility(View.GONE);
                                    reportImage.setOnClickListener(view1 -> Util.viewImages(cxt, reportImage, imageList));


                                }
                            };
                            getImages.execute();
                        }
                        proofMediaLayout.addView(viewImage);
                    }


                }

                return view;
            }
        };

        listView.setAdapter(remAdapter);


    }

    ArrayList<Remittance> remList = new ArrayList<>();
    ArrayAdapter<Remittance> remAdapter;
}

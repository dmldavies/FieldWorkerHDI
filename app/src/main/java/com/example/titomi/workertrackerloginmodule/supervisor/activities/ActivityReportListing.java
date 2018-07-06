package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DateTimeUtil;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ExcelExporter;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ActivityReportListing extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private ListView reportList;
    static Context cxt;
    private MenuItem exportItem;

    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //error reporting
//        Mint.setApplicationEnvironment(Mint.appEnvironmentTesting);
//
//        Mint.initAndStartSession(this.getApplication(), "fa0aaf30");

        setContentView(R.layout.activity_report_listing);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cxt = this;
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        reportList = findViewById(R.id.reportList);
        reportList.setOnItemClickListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              getNewReports();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        getNewReports();
    }

    private void getNewReports() {
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            String from = Network.encodeUrl(dtf.format(new Date()));
            new ReportNetwork().execute(cxt.getString(R.string.api_url)+cxt.getString(R.string.task_url)+"?view=supervisor_get_report&from="+from
                    +"&key="+cxt.getString(R.string.field_worker_api_key)+"&id="+ loggedInUser.getId());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected  void onDestroy() {

        super.onDestroy();
        taskList.clear();    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.export:
                exportReport();
                break;
            case R.id.search:
                reportDateDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_report_listing,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        exportItem = menu.findItem(R.id.export);
        if(taskList.size()  < 1 ){
            exportItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
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


    private void exportReport() {

        String[] reportHeader = getResources().getStringArray(R.array.header);
        ArrayList<String[]> data = new ArrayList<>();
        for(int i = 0; i<taskList.size(); i++){
            Task task = taskList.get(i);
            DateFormat dtf = DateFormat.getDateTimeInstance();

            String[] d = {""+(i+1),
                            dtf.format(task.getDateDelivered()),
                            task.getState(),
                            task.getLga(),
                            task.getInstitution_name(),
                            task.getAddress(),
                            task.getContactName(),
                            task.getContactNumber(),
                            NumberFormat.getInstance().format(task.getParticipants()),
                            DateFormat.getTimeInstance().format(task.getDateDelivered())
                        };
            data.add(d);
        }
        new ExcelExporter(cxt,reportHeader,data).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Task task = (Task)adapterView.getItemAtPosition(i);
        Intent data = new Intent(cxt,ActivityViewReport.class);
        data.putExtra(getString(R.string.loggedInUser), loggedInUser);
        data.putExtra("task",task);
        startActivity(data);
    }


    private class ReportNetwork extends android.os.AsyncTask<String, Void, String> {

        private void showNoReportSnack(){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), "No reports found", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
          //  progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(s == null) {

                showNoReportSnack();
                return;
            }


            if( s.equalsIgnoreCase("null")) {

                    showNoReportSnack();
                    return;

            }
            try {
                JSONArray jsonArray = new JSONArray(s);
                if (jsonArray.length() == 0) {
                    showNoReportSnack();
                }
                taskList.clear();
             //   Toast.makeText(cxt,"Length: "+jsonArray.length(),Toast.LENGTH_SHORT).show();
              /*  if(jsonArray.length() > 0){
                    if(taskList.size() != 0)
                    exportItem.setVisible(true);
                }else{
                    if(taskList.size() == 0) {
                        exportItem.setVisible(false);
                        showNoReportSnack();
                    }

                }*/
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    JSONObject supervisorObj = obj.getJSONObject("supervisor");
                    JSONObject workerObj = obj.getJSONObject("worker");
                    User supervisor = new User();
                    supervisor.setUserLevel(supervisorObj.getInt("roleId"));
                    supervisor.setUserLevelText(supervisorObj.getString("role"));
                    supervisor.setFeaturedImage(supervisorObj.getString("photo"));
                    supervisor.setName(String.format("%s %s",supervisorObj.getString("first_name")
                            ,supervisorObj.getString("last_name")));
                    supervisor.setEmail(supervisorObj.getString("email"));
                    supervisor.setId(supervisorObj.getInt("id"));
                    User worker = new User();
                    worker.setUserLevel(workerObj.getInt("roleId"));
                    worker.setUserLevelText(workerObj.getString("role"));
                    worker.setFeaturedImage(workerObj.getString("photo"));
                    worker.setName(String.format("%s %s",workerObj.getString("first_name"),
                            supervisorObj.getString("last_name")));
                    worker.setEmail(workerObj.getString("email"));
                    worker.setId(workerObj.getInt("id"));
                    SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    SimpleDateFormat dtf2 = new SimpleDateFormat("yyyy/MM/dd");
                  //  DateFormat dtf = DateFormat.getDateTimeInstance();
                    Date dateGiven = dtf2.parse(obj.getString("dateGiven"));
                    Date stopTime = dtf.parse(obj.getString("stopTime"));

                    Date startTime = dtf.parse(obj.getString("startTime"));
                    Date dateDelivered = dtf.parse(obj.getString("dateDelivered"));
                    SimpleDateFormat tf = new SimpleDateFormat("HH:mm:s");
                    String timeGiven = obj.getString("timeGiven");


                    Task task = new Task(obj.getInt("id"),supervisor,worker,dateGiven,dateDelivered,
                            obj.getString("name"),obj.getString("description"),
                            timeGiven,obj.getString("workType"),obj.getString("contactName"),
                            obj.getString("contactNumber"),
                            obj.getString("institution_name"),
                            obj.getString("location"),
                            obj.getString("lga"),
                            obj.getString("state"),
                            obj.getString("address"),
                            obj.getString("sales"),
                            obj.getString("images"),
                            0,
                            obj.getInt("inventoryBalance"),
                            obj.getInt("quantitySold"),
                            obj.getInt("participants"),
                            obj.getInt("status"),obj.getInt("productId"));
                    task.setWorkerComment(obj.getString("workerComment"));
                    task.setStartTime(startTime);
                    task.setStopTime(stopTime);
                    task.setVideo(obj.getString("video"));
                    task.setAudio(obj.getString("audio"));


                        taskList.add(task);
                }

                if(taskList.size() != 0){
                    exportItem.setVisible(true);
                }
                taskArrayAdapter = new ArrayAdapter<Task>(cxt,R.layout.report_single_item_layout,taskList){



                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.report_single_item_layout,null);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0,5,0,5);

                        convertView.setLayoutParams(layoutParams);

                        Task task = taskList.get(position);

                        ImageView userImage = convertView.findViewById(R.id.user_icon);
                        TextView usernameText = convertView.findViewById(R.id.username);
                        TextView dateSubmittedText = convertView.findViewById(R.id.dateSubmitted);
                        TextView taskTitle = convertView.findViewById(R.id.taskTitle);
                        TextView viewReportText =convertView.findViewById(R.id.viewReportText);

                        DrawableManager drm = new DrawableManager();
                         drm.fetchDrawableOnThread(cxt.getString(R.string.server_url)
                                 +task.getWorker().getFeaturedImage(),userImage);
                         usernameText.setText(Util.toSentenceCase(String.format("%s ( %s )",
                                 task.getWorker().getName(),
                                 task.getWorker().getUserLevelText())));
                       // SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
                        DateFormat dtf = DateFormat.getDateTimeInstance();
                         dateSubmittedText.setText(String.format("Submitted on %s",dtf.format(task.getDateDelivered())));
                         taskTitle.setText(task.getName());

                        viewReportText.setOnClickListener(view -> {
                            //TODO: Take to view report activity
                        });

                        return convertView;
                    }
                };

                reportList.setAdapter(taskArrayAdapter);

            } catch (JSONException e) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), "No report found", Snackbar.LENGTH_LONG);
                snackbar.show();
                e.printStackTrace();
                System.err.println(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

     ArrayList<Task> taskList = new ArrayList<>();
     ArrayAdapter<Task> taskArrayAdapter;

    private void reportDateDialog(){
        View view = getLayoutInflater().inflate(R.layout.search_report_layout,null);
        final EditText fromDate = view.findViewById(R.id.from_date);
        final EditText toDate = view.findViewById(R.id.to_date);
        final Button searchActionButton = view.findViewById(R.id.view_report_action_button);
        final AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();

         fromDate.setOnClickListener(view12 -> DateTimeUtil.showDatePicker(cxt,fromDate));
         toDate.setOnClickListener(view1 -> DateTimeUtil.showDatePicker(cxt,toDate));
         searchActionButton.setOnClickListener(view13 -> {

           if(fromDate.getText().length() == 0 || toDate.getText().length() == 0){
               Toast.makeText(cxt,"Please fill all fields",Toast.LENGTH_SHORT).show();
               return;
           }
           alertDialog.dismiss();
           searchAction(fromDate.getText().toString(),toDate.getText().toString());
         });



         alertDialog.setView(view);
         alertDialog.show();

    }

    private  void searchAction(String fromDate,String toDate){
        new ReportNetwork().execute(cxt.getString(R.string.api_url)+cxt.getString(R.string.task_url)+"?view=supervisor_get_report&from="+fromDate+"&to="+toDate
                +"&key="+cxt.getString(R.string.field_worker_api_key)+"&id="+ loggedInUser.getId());
    }
}

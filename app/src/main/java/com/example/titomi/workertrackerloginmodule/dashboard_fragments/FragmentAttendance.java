package com.example.titomi.workertrackerloginmodule.dashboard_fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Titomi on 2/8/2018.
 */

public class FragmentAttendance extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    final String TAG = "ATTENDANCE FRAGMENT";
    View view;
    Context ctx;
    PieChart pieChart;
    TextView totalAssigned, late, early;
    Context cxt;
    ArrayList<PieEntry> yValues = new ArrayList<>();
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private User loggedInUser;

    public FragmentAttendance() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attendance_fragment, container, false);
        ctx = getActivity();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
        }

        pieChart = view.findViewById(R.id.pieChartAttend);
        refreshLayout = view.findViewById(R.id.swipe_on_attendance);
        refreshLayout.setOnRefreshListener(this);
        ;
        pieChart.getDescription().setEnabled(false);

        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.99f);
        progressBar = view.findViewById(R.id.progressBarAttend);
        early = view.findViewById(R.id.earlyCountTV);
        late = view.findViewById(R.id.lateCountTV);
        totalAssigned = view.findViewById(R.id.totalAssignTaskTV);


        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        yValues.add(new PieEntry((float) 0.0, "Late"));
        yValues.add(new PieEntry((float) 0.0, "Early"));

        Description description = new Description();
        description.setText("Attendance Chart");
        description.setTextSize(15);
        pieChart.setDescription(description);

        PieDataSet dataSet = new PieDataSet(yValues, "Attendance");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(40f);
        data.setValueTextColor(Color.YELLOW);

        loadData();
        return view;
    }

    private void loadData() {
        String url = "";
        switch (loggedInUser.getRoleId()) {
            case User.SUPERVISOR:
                url = getString(R.string.api_url) + getString(R.string.task_url) + "?view=worker&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
            case User.NURSE:
                url = getString(R.string.api_url) + getString(R.string.task_url) + "?view=worker&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
        }
        new AttendanceNetwork().execute(url);


    }

    private void loadChart(ArrayList<Task> taskList) {
        int lateCount = 0;
        int earlyCount = 0;
            for (Task task : taskList) {
                if (task != null && task.getStatus() == Task.COMPLETED) {

                    if (task.getStartTime().after(task.getDateGiven())) {
                        lateCount++;
                    }else{
                        earlyCount++;
                    }
                }
            }


        yValues.clear();

        yValues.add(new PieEntry((float) lateCount, "Late"));
        yValues.add(new PieEntry((float) earlyCount, "Early"));

        PieDataSet dataSet = new PieDataSet(yValues, "Attendance");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.createColors(new int[]{R.color.primary_dark,R.color.graylight}));

        PieData data = new PieData((dataSet));
        data.setValueTextSize(40f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.requestLayout();

        early.setText(NumberFormat.getInstance().format(earlyCount));
        late.setText(NumberFormat.getInstance().format(lateCount));
        totalAssigned.setText(NumberFormat.getInstance().format(taskList.size()));
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private class AttendanceNetwork extends android.os.AsyncTask<String, Void, String> {

        ArrayList<Task> taskList = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(true);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            progressBar.setVisibility(View.GONE);
            refreshLayout.setRefreshing(false);
            if (s == null) {

                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);
                taskList.clear();

                if (jsonArray.length() > 0) {

                } else {
                    // noTaskNotif.setVisibility(View.GONE);
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    JSONObject supervisorObj = obj.getJSONObject("supervisor");
                    JSONObject workerObj = obj.getJSONObject("worker");
                    User supervisor = new User();
                    supervisor.setUserLevel(supervisorObj.getInt("roleId"));
                    supervisor.setUserLevelText(supervisorObj.getString("role"));
                    supervisor.setFeaturedImage(supervisorObj.getString("photo"));
                    supervisor.setName(String.format("%s %s", supervisorObj.getString("first_name"), supervisorObj.getString("last_name")));
                    supervisor.setEmail(supervisorObj.getString("email"));
                    supervisor.setId(supervisorObj.getInt("id"));
                    User worker = new User();
                    worker.setUserLevel(workerObj.getInt("roleId"));
                    worker.setUserLevelText(workerObj.getString("role"));
                    worker.setFeaturedImage(workerObj.getString("photo"));
                    worker.setName(String.format("%s %s", workerObj.getString("first_name"), supervisorObj.getString("last_name")));
                    worker.setEmail(workerObj.getString("email"));
                    worker.setId(workerObj.getInt("id"));
                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    //  DateFormat dtf = DateFormat.getDateTimeInstance();
                    SimpleDateFormat dtf2 = new SimpleDateFormat("yyyy/MM/dd");
                    Date dateGiven = dtf.parse(String.format("%s %s", obj.getString("dateGiven"), obj.getString("timeGiven")));
                    Date stopTime = dtf.parse(obj.getString("stopTime"));
                    Date startTime = dtf.parse(obj.getString("startTime"));
                    Date dateDelivered = dtf.parse(obj.getString("dateDelivered"));
                    SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
                    String timeGiven = obj.getString("timeGiven");


                    Task task = new Task(obj.getInt("id"), supervisor, worker, dateGiven, dateDelivered,
                            obj.getString("name"), obj.getString("description"),
                            timeGiven, obj.getString("workType"), obj.getString("contactName"),
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
                            obj.getInt("status"), obj.getInt("productId"));
                    task.setLatitude(obj.getDouble("latitude"));
                    task.setLongitude(obj.getDouble("longitude"));
                    task.setWorkerComment(obj.getString("workerComment"));
                    /*if(obj.getDouble("startLongitude") != 0.0 && obj.getDouble("startLatitude") != 0.0 &&
                            obj.getDouble("stopLongitude") != 0.0 && obj.getDouble("stopLatitude") != 0.0)*/
                    task.setStartLatitude(obj.getDouble("startLatitude"));
                    task.setStopLatitude(obj.getDouble("stopLatitude"));
                    task.setStartLongitude(obj.getDouble("startLongitude"));
                    task.setStopLongitude(obj.getDouble("stopLongitude"));


                    task.setInventoryBalance(obj.getInt("inventoryBalance"));

                    task.setQuantitySold(0);
                    task.setQuantity(obj.getInt("quantitySold"));

                    task.setQuantitySold(obj.getInt("quantitySold"));
                    task.setQuantity(0);


                    task.setStartTime(startTime);
                    task.setStopTime(stopTime);
                    task.setTimeGiven(timeGiven);


                    taskList.add(task);
                }

                loadChart(taskList);

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            } catch (ParseException e) {

                e.printStackTrace();

            }
        }
    }
}

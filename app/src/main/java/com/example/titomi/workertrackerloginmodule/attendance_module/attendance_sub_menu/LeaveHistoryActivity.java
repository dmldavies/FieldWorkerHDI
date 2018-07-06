package com.example.titomi.workertrackerloginmodule.attendance_module.attendance_sub_menu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.apis.model.Users;
import com.example.titomi.workertrackerloginmodule.apis.model.leave_model.LeaveModel;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class LeaveHistoryActivity extends AppCompatActivity {

    private static final String TAG = LeaveHistoryActivity.class.getSimpleName();
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private static ListView mListView;
    private static Context ctx;

    Users users;
    //    Toolbar toolbar;
//    SharedPrefManager sharedPrefManager;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave_list_layout);

        mListView = findViewById(R.id.leaveRequestList);
        ctx = this;
        if (getIntent().getExtras() != null) {
            loggedInUser = (User) getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }
        mSwipeRefreshLayout = findViewById(R.id.leaveSwipeRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequests();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("Leave Requests");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRequests();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void loadRequests() {
        new LeaveRequestNetwork().execute(getString(R.string.api_url) + getString(R.string.leave_view_requests_url) + "?key=" + getString(R.string.field_worker_api_key) + "&view=user_requests&user_id=" + loggedInUser.getId());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class LeaveRequestNetwork extends android.os.AsyncTask<String, Void, String> {

        static ArrayList<LeaveModel> modelArrayList = new ArrayList<>();
        static ArrayAdapter<LeaveModel> leaveArrayAdapter;
        User applicant = new User();

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }


        @Override
        protected void onPostExecute(String s) {
            mSwipeRefreshLayout.setRefreshing(false);
            if (s == null) {
                return;
            }
            try {
                JSONArray jsonArray = new JSONArray(s);
                modelArrayList.clear();
                System.out.println(jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    JSONObject applicantObj = obj.getJSONObject("applicant");

//                    applicant.setSupervisorId(applicantObj.getLong("supervisor"));


                    LeaveModel leaveModel = new LeaveModel(obj.getString("id"), obj.getString("approvedBy"), obj.getString("fromDate"), obj.getString("toDate"), obj.getString("reason"), obj.getString("date"), obj.getString("comment"), obj.getInt("numDays"), obj.getString("status"), obj.getString("id"));

                    modelArrayList.add(leaveModel);
                }

                ArrayAdapter<LeaveModel> leaveListAdapter = new ArrayAdapter<LeaveModel>(ctx, R.layout.leave_single_item_layout, modelArrayList) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.leave_single_item_layout, null);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 10, 0, 5);

                        convertView.setLayoutParams(layoutParams);

                        LeaveModel model = modelArrayList.get(position);
                        TextView leaveReasonText = convertView.findViewById(R.id.leaveReasonText);
                        TextView numDays = convertView.findViewById(R.id.numDaysText1);
                        TextView approvedBy = convertView.findViewById(R.id.approvedByText);
                        TextView leaveCommentText = convertView.findViewById(R.id.leaveCommentText);
                        TextView fromDate = convertView.findViewById(R.id.fromDate);
                        TextView toDate = convertView.findViewById(R.id.toDate);
                        TextView dateTimeText = convertView.findViewById(R.id.dateTimeText1);
                        TextView status = convertView.findViewById(R.id.inventoryStatusText1);

                        if (approvedBy != null) {
                            approvedBy.setText(model.getApprovedBy());
                        }
                        leaveReasonText.setText(model.getReason());
                        if (numDays != null) {
                            numDays.setText("" + model.getNumDays() + " days");
                        }
                        if (leaveCommentText != null || !Objects.equals(leaveCommentText, "null")) {
                            leaveCommentText.setText(model.getComment());
                        }
//                        fromDate.setText(DateFormat.getDateInstance().format(model.getFromDate()));
                        fromDate.setText(model.getFromDate());
//                        toDate.setText(DateFormat.getDateInstance().format(model.getToDate()));
                        toDate.setText(model.getToDate());
                        if (dateTimeText != null) {
                            dateTimeText.setText(model.getDate().replaceAll("-", "/"));
                        }
                        if (status != null) {
                            status.setText(Util.toSentenceCase(model.getStatus()));
                            switch (model.getStatus()) {
                                case LeaveModel.PENDING:
                                    status.setTextColor(Color.RED);
                                    approvedBy.setVisibility(View.GONE);
                                    leaveCommentText.setVisibility(View.GONE);
                                    break;
                                case LeaveModel.COMPLETED:
                                    status.setTextColor(Color.GREEN);
                                    break;
                            }
                        }

                        return convertView;
                    }
                };
                mListView.setAdapter(leaveListAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
                Log.d(getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
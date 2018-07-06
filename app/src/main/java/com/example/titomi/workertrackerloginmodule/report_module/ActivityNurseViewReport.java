package com.example.titomi.workertrackerloginmodule.report_module;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ExcelExporter;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ActivityNurseViewReport extends AppCompatActivity implements View.OnClickListener {

    Task selectedTask;

    ImageView userImage;
    TextView username, dateSubmitted, taskTitle, taskTypeText, institutionText, addressText, stateText, contactFullName, contactPhone, quantityGivenText, participantsText, quantityDistributedText, balanceText, exportText, approveText, commentText;
    LinearLayout reportImagesLayout;
    Context cxt;
    User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report_layout);


        initComponents();

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            selectedTask = (Task) extras.getSerializable("task");
            loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
            setupView(selectedTask);
        }
    }

    private void initComponents() {
        cxt = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.app_logo);
        userImage = findViewById(R.id.user_icon);
        username = findViewById(R.id.username);
        dateSubmitted = findViewById(R.id.dateSubmitted);
        taskTitle = findViewById(R.id.taskTitle);
        taskTypeText = findViewById(R.id.taskTypeText);
        institutionText = findViewById(R.id.institutionText);
        addressText = findViewById(R.id.addressText);
        stateText = findViewById(R.id.stateText);
        contactFullName = findViewById(R.id.contactFullName);
        contactPhone = findViewById(R.id.contactPhone);
        quantityGivenText = findViewById(R.id.quantityGivenText);
        participantsText = findViewById(R.id.participants);
        quantityDistributedText = findViewById(R.id.quantityDistributedText);
        balanceText = findViewById(R.id.balanceText);
        exportText = findViewById(R.id.exportText);
        reportImagesLayout = findViewById(R.id.reportImagesLayout);
        approveText = findViewById(R.id.approveText);
        commentText = findViewById(R.id.commentText);
        approveText.setOnClickListener(this);

        exportText.setOnClickListener(this);
    }

    private void setupView(final Task task) {
        DrawableManager drm = new DrawableManager();
        drm.fetchDrawableOnThread(getString(R.string.server_url) + task.getWorker().getFeaturedImage(), userImage);
        username.setText(task.getWorker().getName());

        DateFormat dtf = DateFormat.getDateTimeInstance();
        dateSubmitted.setText(dtf.format(task.getDateDelivered()));

        taskTitle.setText(task.getName());
        taskTypeText.setText(task.getWorkType());
        institutionText.setText(task.getInstitution_name());
        addressText.setText(task.getAddress());
        stateText.setText(task.getState());
        contactFullName.setText(task.getContactName());
        contactPhone.setText(task.getContactNumber());
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        quantityGivenText.setText(numberFormat.format(task.getQuantity()));

        quantityDistributedText.setText(numberFormat.format(task.getQuantitySold()));
        balanceText.setText(numberFormat.format(task.getInventoryBalance()));
        participantsText.setText(numberFormat.format(task.getParticipants()));
        commentText.setText(task.getWorkerComment() == null ? "" : task.getWorkerComment());


        if (selectedTask.getStatus() == Task.COMPLETED || loggedInUser.getRoleId() == User.NURSE) {
            approveText.setVisibility(View.GONE);
        }

        loadReportImages(task);


    }

    private void loadReportImages(Task task) {
        final ArrayList<String> imageList = new ArrayList<>();
        if (task.getImages() != null) {
            if (!task.getImages().isEmpty()) {
                String[] images = task.getImages().split(",");
                for (String image : images) {
                    View view = getLayoutInflater().inflate(R.layout.report_images_single_item, null);


                    final ImageView reportImage = view.findViewById(R.id.reportImage);

                    String fullImageUrl = getString(R.string.server_url) + image;

                    final String imageName = ImageUtils.getImageNameFromUrlWithExtension(fullImageUrl);
                    //Toast.makeText(cxt,imageName,Toast.LENGTH_LONG).show();
                    final ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(task);
                    if (storage.imageExists(imageName)) {
                        if (storage.getImage(imageName) == null) return;
                        reportImage.setImageURI(Uri.parse(storage.getImage(imageName).getAbsolutePath()));
                        imageList.add(storage.getImage(imageName).getAbsolutePath());
                    } else {

                        final ImageUtils.GetImages getImages = new ImageUtils.GetImages(task, fullImageUrl, imageName) {
                            @Override
                            protected void onPostExecute(Object obj) {
                                super.onPostExecute(obj);

                                if (storage.getImage(imageName) == null) return;

                                imageList.add(storage.getImage(imageName).getAbsolutePath());
                                reportImage.setImageBitmap((Bitmap) obj);


                            }
                        };

                        getImages.execute();
                    }

                    reportImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Util.viewImages(cxt, reportImage, imageList);
                        }
                    });

                    // reportImage.setOnClickListener();
                    reportImagesLayout.addView(view);
                }


            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // setupView(selectedTask);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("task", selectedTask);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedTask = (Task) savedInstanceState.getSerializable("task");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exportText:
                exportReport();
                break;
            case R.id.approveText:
                new ApproveTaskNetwork().execute(getString(R.string.api_url) + getString(R.string.approve_task_url) + "?key=" + getString(R.string.field_worker_api_key) + "&id=" + selectedTask.getId());
                break;
        }
    }

    private void exportReport() {

        String[] reportHeader = getResources().getStringArray(R.array.header);
        ArrayList<String[]> data = new ArrayList<>();

        Task task = selectedTask;
        DateFormat dtf = DateFormat.getDateTimeInstance();

        String[] d = {"" + 1,
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

        new ExcelExporter(cxt, reportHeader, data).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private class ApproveTaskNetwork extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
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
            if (s == null) return;

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            try {
                JSONObject object = new JSONObject(s);
                if (object.getInt("statusCode") == Entity.STATUS_OK) {
                    Toast.makeText(cxt, "Task approved successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(cxt, "An unexpected error occurred", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(getClass().getName(), e.getMessage());
                System.err.println(s);
            }

        }
    }


}

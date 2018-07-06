package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.report_module.VideoPlayer;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ActivityViewReport extends AppCompatActivity implements View.OnClickListener,MediaPlayer.OnCompletionListener {

    static Context cxt;
    Task selectedTask;
    ImageView userImage;
    TextView username, dateSubmitted, taskTitle, taskTypeText, institutionText, addressText, stateText, contactFullName, contactPhone, quantityGivenText, participantsText, quantityDistributedText, balanceText, exportText, approveText, commentText, playVideo, playAudio;
    LinearLayout reportImagesLayout;
    User loggedInUser;
    String videoUrl;
    String audioUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report_layout);


        initComponents();

        if(getIntent().getExtras() != null){
            Bundle extras  = getIntent().getExtras();
            selectedTask = (Task) extras.getSerializable("task");
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
            setupView(selectedTask);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(audiPlayer != null && audiPlayer.isPlaying()){
            audiPlayer.stop();
            audiPlayer = null;
        }
    }

    private void initComponents(){
        cxt = this;
        ActionBar actionBar =  getSupportActionBar();
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
        exportText  = findViewById(R.id.exportText);
        reportImagesLayout = findViewById(R.id.reportImagesLayout);
        approveText = findViewById(R.id.approveText);
        commentText = findViewById(R.id.commentText);
        approveText.setOnClickListener(this);
        playVideo = findViewById(R.id.videoText);
        playAudio = findViewById(R.id.audioText);
        playAudio.setOnClickListener(this);
        playVideo.setOnClickListener(this);
        exportText.setOnClickListener(this);
    }
    private void setupView(final Task task){
        DrawableManager drm = new DrawableManager();
        drm.fetchDrawableOnThread(getString(R.string.server_url)+task.getWorker().getFeaturedImage(),userImage);
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
        //balanceText.setText(numberFormat.format(task.getInventoryBalance()));
        participantsText.setText(numberFormat.format(task.getParticipants()));
        commentText.setText(task.getWorkerComment() == null ? "" : task.getWorkerComment());
        if (task.getVideo() != null && !task.getVideo().isEmpty()) {
            playVideo.setVisibility(View.VISIBLE);
            videoUrl = getString(R.string.server_url) + task.getVideo();
        } else {
            playVideo.setVisibility(View.GONE);
        }

        if(task.getAudio() != null && !task.getAudio().isEmpty()){
            audioUrl = getString(R.string.server_url)+task.getAudio();
            playAudio.setVisibility(View.VISIBLE);
        }else{
            playAudio.setVisibility(View.GONE);
        }


        if(selectedTask.getStatus() == Task.COMPLETED || loggedInUser.getRoleId() == User.NURSE){
            approveText.setVisibility(View.GONE);
        }

        loadReportImages(task);



    }

    private void loadReportImages(Task task){
        final ArrayList<String> imageList = new ArrayList<>();
        if(task.getImages() != null) {
            if(!task.getImages().isEmpty()) {
                String[] images = task.getImages().split(",");
                for(String image : images) {
                    View view = getLayoutInflater().inflate(R.layout.report_images_single_item, null);


                    final ImageView reportImage = view.findViewById(R.id.reportImage);
                    final FrameLayout loadingFrame =    view.findViewById(R.id.loadingImageFrame);

                    String fullImageUrl = getString(R.string.server_url)+image;

                    final String imageName = ImageUtils.getImageNameFromUrlWithExtension(fullImageUrl);
                    //Toast.makeText(cxt,imageName,Toast.LENGTH_LONG).show();
                    final ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(task);
                    if(storage.imageExists(imageName)){
                        if(storage.getImage(imageName) == null) return;
                        reportImage.setImageURI(Uri.parse(storage.getImage(imageName).getAbsolutePath()));
                        reportImage.setOnClickListener(view1 -> Util.viewImages(cxt, reportImage, imageList));
                        imageList.add(storage.getImage(imageName).getAbsolutePath());
                    }else {

                        final ImageUtils.GetImages getImages = new ImageUtils.GetImages(task, fullImageUrl, imageName) {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                loadingFrame.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected void onPostExecute(Object obj) {
                                super.onPostExecute(obj);

                                reportImage.setImageBitmap((Bitmap) obj);
                                if(storage.getImage(imageName) == null) return;

                                imageList.add(storage.getImage(imageName).getAbsolutePath());

                             loadingFrame.setVisibility(View.GONE);
                                reportImage.setOnClickListener(view1 -> Util.viewImages(cxt, reportImage, imageList));


                            }
                        };

                        getImages.execute();
                    }



                    // reportImage.setOnClickListener();
                    reportImagesLayout.addView(view);
                }



            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        String url = getString(R.string.api_url) + getString(R.string.inventory_view_requests_url) + "?view=user_stock_details&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
        new InventoryNetwork().execute(url);
       // setupView(selectedTask);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("task",selectedTask);
        outState.putSerializable(getString(R.string.loggedInUser),loggedInUser);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedTask = (Task)savedInstanceState.getSerializable("task");
        loggedInUser = (User)savedInstanceState.getSerializable(getString(R.string.loggedInUser));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.exportText:
              exportReport();
                break;
            case R.id.approveText:
                new ApproveTaskNetwork().execute(getString(R.string.api_url)+getString(R.string.approve_task_url)+"?key="+getString(R.string.field_worker_api_key)+"&id="+selectedTask.getId());
                break;
            case R.id.videoText:
                if (videoUrl != null) {
                    //Stop playing audio when video is clicked
                    if(audiPlayer != null && audiPlayer.isPlaying()){
                        audiPlayer.stop();
                    }
                    Intent i = new Intent(cxt, VideoPlayer.class);
                    i.putExtra("videoUrl", videoUrl);
                    startActivity(i);
                }
                break;
            case R.id.audioText:
                if (audioUrl != null) {
                     audiPlayer = new MediaPlayer();
                     audiPlayer.setOnCompletionListener(this);
                    try {
                        audiPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        audiPlayer.setDataSource(audioUrl);
                        audiPlayer.prepare();

                        Thread t = new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                if(!audiPlayer.isPlaying()) {
                                    audiPlayer.start();
                                }
                            }
                        };

                        t.start();

                        audioPlaybackSnack = Snackbar.make(findViewById(R.id.parent),"Playing audio",Snackbar.LENGTH_INDEFINITE);
                        if(audiPlayer.isPlaying()) {
                            audioPlaybackSnack.setAction("Stop playback", v->{
                                audiPlayer.stop();
                            });
                            audioPlaybackSnack.show();
                        }




                         /*   while (audiPlayer.isPlaying()){
                                if(!audiPlayer.isPlaying()){
                                    snackbar.dismiss();
                                    break;

                                }
                            }
*/






                    } catch (IOException e) {
                        Toast.makeText(cxt,"Error playing audio",Toast.LENGTH_LONG).show();
                    }
                    /*Intent i = new Intent(cxt, RecordActivity.class);
                    i.putExtra("audioUrl", audioUrl);
                    startActivity(i);*/
                }
                break;
        }
    }

    private void exportReport() {

        String[] reportHeader = getResources().getStringArray(R.array.header);
        ArrayList<String[]> data = new ArrayList<>();

            Task task = selectedTask;
            DateFormat dtf = DateFormat.getDateTimeInstance();

            String[] d = {""+1,
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

        new ExcelExporter(cxt,reportHeader,data).execute();
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

    private void setBalanceText(JSONObject obj) throws JSONException {
        balanceText.setText(obj.getString("balance"));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(audioPlaybackSnack != null && audioPlaybackSnack.isShown()){
            audioPlaybackSnack.dismiss();
        }
    }

    private static class ApproveTaskNetwork extends AsyncTask<String,Void,String>{
        ProgressDialog progressDialog;
        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);
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
            if(s == null) return;

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            try {
                JSONObject object = new JSONObject(s);
                if(object.getInt("statusCode") == Entity.STATUS_OK){
                    Toast.makeText(cxt,"Task approved successfully",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(cxt,"An unexpected error occurred",Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(getClass().getName(),e.getMessage());
                System.err.println(s);
            }

        }
    }

    private class InventoryNetwork extends android.os.AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
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
                setBalanceText(obj);

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            }
        }

    }

    MediaPlayer audiPlayer;
    Snackbar audioPlaybackSnack;

}

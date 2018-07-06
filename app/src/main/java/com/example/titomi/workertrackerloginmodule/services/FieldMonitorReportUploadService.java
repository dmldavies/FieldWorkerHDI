package com.example.titomi.workertrackerloginmodule.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityTaskListing;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.MediaUploader;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by NeonTetras on 27-Feb-18.
 */

public class FieldMonitorReportUploadService extends Service {
    private  int mediaCount = 0;

    private final IBinder binder = new MyBinder();
    ArrayList<String> images;
    String video;
    String audio;

    HashMap<String, String> postData;
    User loggedInUser;
    NotificationManager notifManager;
    int submittingReportNotif = 001;
    int submissionComplete = 002;
    private Context cxt;
    private Timer mTimer1;
    private TimerTask mTt1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) return START_STICKY;
        postData = (HashMap<String, String>) intent.getSerializableExtra("postData");
        images = intent.getStringArrayListExtra("images");
        video = intent.getStringExtra("video");
        audio = intent.getStringExtra("audio");
        loggedInUser = (User) intent.getSerializableExtra(getString(R.string.loggedInUser));
        cxt = this;

        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //startTimer();

        NotificationCompat.Builder notifBuilder =
                new NotificationCompat
                        .Builder(cxt, getString(R.string.submitting_report))
                        .setSmallIcon(R.mipmap.app_logo)
                        .setContentText("Submitting report....Please wait")
                        .setContentTitle("Field monitor");

        notifBuilder.setAutoCancel(true);
        notifBuilder.setLights(Color.GREEN, 60000, 60000);
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        notifBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        notifManager.notify(submittingReportNotif, notifBuilder.build());





        /*
        * Add media to postData hashmap if media was attached, then upload media*/
        if (images != null && !images.isEmpty()) {

            StringBuilder sb = new StringBuilder();
            for (String img : images) {
                sb.append(String.format("images/reports/%s,", new File(img).getName()));
            }
            sb.deleteCharAt(sb.toString().lastIndexOf(","));
            images = ImageUtils.compressImages(this, new Task(), images);
            mediaCount++;
            postData.put("photo", sb.toString());
            uploadImages();
        }
        if (video != null && !video.equals("")) {
            mediaCount++;
            postData.put("video", String.format("videos/%s", new File(video).getName()));
            uploadVideo();
        }
        if (audio != null && !audio.equals("")) {
            mediaCount++;
            postData.put("audio", String.format("audio/%s", new File(audio).getName()));
            uploadAudio();
        }

        /**
         * Send report  if no media was attached
         */
        if (mediaCount == 0) {
            sendReport();
        }


        return START_STICKY;

    }

    private void uploadImages() {

        ImageUploader imageUploader = new ImageUploader(this, String.format("%s%s", getString(R.string.api_url), getString(R.string.image_upload_url)));
        imageUploader.execute(images);
    }

    private void uploadAudio() {
        ArrayList<String> audios = new ArrayList<>();
        audios.add(audio);
        AudioUploader audioUploader = new AudioUploader(this, String.format("%s%s", getString(R.string.api_url), getString(R.string.audio_upload_url)));
        audioUploader.execute(audios);
    }

    private void uploadVideo() {

        ArrayList<String> videos = new ArrayList<>();
        videos.add(video);
        VideoUploader videoUploader =
                new VideoUploader(this,
                        String.format("%s%s", getString(R.string.api_url),
                                getString(R.string.video_upload_url)));
        videoUploader.execute(videos);
    }


    private void sendReport() {
        try {
            ReportSubmitNetwork network = new ReportSubmitNetwork();
            String getData = Network.getPostDataString(postData);
            System.out.printf("Outputting get data: %s", getData);

            network.execute(getString(R.string.api_url) +
                    getString(R.string.clockOutUrl) + "?key=" +
                    getString(R.string.field_worker_api_key) + "&" + getData);
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    private void notifyCompletion() {

        notifManager.cancel(submittingReportNotif);
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat
                        .Builder(cxt, getString(R.string.submitting_report))
                        .setSmallIcon(R.mipmap.app_logo)
                        .setContentText("Submitting complete")
                        .setContentTitle("Field monitor");

        Intent resultIntent = new Intent(cxt, ActivityTaskListing.class);
        resultIntent.putExtra(getString(R.string.loggedInUser), loggedInUser);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                cxt,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        Toast.makeText(cxt, "Report submitted successfully", Toast.LENGTH_LONG).show();
        //  startActivity(resultIntent);

        notifBuilder.setContentIntent(pendingIntent);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setLights(Color.GREEN, 60000, 60000);
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        notifBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});


        notifManager.notify(submissionComplete, notifBuilder.build());

    }

    public class MyBinder extends Binder {
        public FieldMonitorReportUploadService getService() {
            return FieldMonitorReportUploadService.this;
        }

    }

    private class ReportSubmitNetwork extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPostExecute(String strings) {

            super.onPostExecute(strings);
            System.out.println(strings);
            if (strings == null) return;



            //Report has been submitted. Notify user, and stop service
                notifyCompletion();
                stopSelf();

        }
    }

    class ImageUploader extends MediaUploader {
        public ImageUploader(Context cxt, String uploadApiUrl) {
            super(cxt, uploadApiUrl);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            upload_successes++;
            uploadCompleteAction();



        }
    }

    class VideoUploader extends MediaUploader {
        public VideoUploader(Context cxt, String uploadApiUrl) {
            super(cxt, uploadApiUrl);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            upload_successes ++;
            uploadCompleteAction();


        }

    }

    class AudioUploader extends MediaUploader {
        public AudioUploader(Context cxt, String uploadApiUrl) {
            super(cxt, uploadApiUrl);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            upload_successes++;
            uploadCompleteAction();


        }
    }

    private void uploadCompleteAction(){
        /*
        max upload_successes = 3 because there are only 3 kinds of media to upload
        * if upload_successes == total number of attached media, then upload has completed */
        if(upload_successes == mediaCount)
        {
            //set the upload_success count back to 0
            upload_successes = 0;
            //Media have uploaded successfully, now send report
            sendReport();


        }
    }

    private static int upload_successes = 0;
}

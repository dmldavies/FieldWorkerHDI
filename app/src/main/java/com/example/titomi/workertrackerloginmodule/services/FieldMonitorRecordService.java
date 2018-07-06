package com.example.titomi.workertrackerloginmodule.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.report_module.ReportActivity;
import com.example.titomi.workertrackerloginmodule.supervisor.DatabaseAdapter;
import com.example.titomi.workertrackerloginmodule.supervisor.Messages;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Titomi on 3/9/2018.
 */

public class FieldMonitorRecordService extends Service {

    public static final int RequestPermissionCode = 1;
    private final IBinder binder = new FieldMonitorRecordService.MyBinder();
    private final int notification_id = 32;
    public String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    ReportActivity activity;
    NotificationManagerCompat managerCompat;
    Task task;
    NotificationCompat.Builder mBuilder;
    private Context cxt;
    private Timer mTimer1;
    private TimerTask mTt1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String t;
        t = intent.getStringExtra("filePath");

        AudioSavePathInDevice = t;
        MediaRecorderReady();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
    }


    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }


    public class MyBinder extends Binder {
        public FieldMonitorRecordService getService() {
            return FieldMonitorRecordService.this;
        }

    }
}

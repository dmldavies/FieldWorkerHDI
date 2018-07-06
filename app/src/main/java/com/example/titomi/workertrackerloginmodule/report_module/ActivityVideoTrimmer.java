package com.example.titomi.workertrackerloginmodule.report_module;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.titomi.workertrackerloginmodule.R;
import android.os.Environment;

import java.io.File;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;



public class ActivityVideoTrimmer extends AppCompatActivity implements OnTrimVideoListener
{


    K4LVideoTrimmer videoTrimmer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trimmer);

         videoTrimmer =  findViewById(R.id.timeLine);

        if (videoTrimmer != null) {


            videoTrimmer.setMaxDuration(30);
            videoTrimmer.setOnTrimVideoListener(this);


            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());


            videoTrimmer.setVideoURI(Uri.parse(getIntent().getExtras().getString("video")));

            File directory = new File(Environment
                    .getExternalStorageDirectory()
                    .getPath(),
                   ".FieldMonitor/videos/".toLowerCase()
                           );
            if (!directory.exists()) directory.mkdirs();

            videoTrimmer.setDestinationPath(directory.getAbsolutePath());



        }
    }


    @Override
    public void getResult(Uri uri) {


        Intent i = new Intent();
        i.putExtra("video",uri.toString());
        setResult(RESULT_OK,i);
        finish();

    }

    @Override
    public void cancelAction() {
        setResult(RESULT_CANCELED);
        finish();
    }
}

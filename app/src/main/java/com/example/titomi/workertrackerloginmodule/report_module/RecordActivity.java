package com.example.titomi.workertrackerloginmodule.report_module;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.example.titomi.workertrackerloginmodule.R;

public class RecordActivity extends AppCompatActivity {
    ImageButton startRecord;

    MediaRecorder mediaRecorder;
    String AudioSavePathInDevice = null;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        startRecord = findViewById(R.id.record);
    }
}

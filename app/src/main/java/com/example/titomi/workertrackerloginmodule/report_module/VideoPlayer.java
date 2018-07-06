package com.example.titomi.workertrackerloginmodule.report_module;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.titomi.workertrackerloginmodule.R;


public class VideoPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener{

    VideoView videoView;
    FrameLayout loadingVideoFrame;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        videoView = (VideoView)findViewById(R.id.videoView);
        loadingVideoFrame = findViewById(R.id.loadingVideoFrame);
        if(getIntent() != null){
            Bundle extras = getIntent().getExtras();

             videoUrl = extras.getString("videoUrl");
             videoView.setVideoURI(Uri.parse(videoUrl));
            MediaController vidControl = new MediaController(this);

            videoView.setOnPreparedListener(this);
             vidControl.setAnchorView(videoView);
            videoView.setMediaController(vidControl);
            videoView.start();
            videoView.getCurrentPosition();

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

            default:

                return super.onOptionsItemSelected(item);
        }


    }
    @Override
    public void onConfigurationChanged(Configuration configuration){
        super.onConfigurationChanged(configuration);
    }
    @Override
    public void onResume() {
        super.onResume();
        videoView.resume();
        videoView.seekTo(currentPosition);
       // currentPosition = videoView.getCurrentPosition()


    }
    @Override
    public  void onPause(){
        videoView.pause();
        super.onPause();

    }
    @Override
    public void onDestroy(){
        videoView.stopPlayback();
        super.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.videoUrl),videoUrl);
        outState.putInt(getString(R.string.currentPosition),videoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        videoUrl = savedInstanceState.getString(getString(R.string.videoUrl));
        currentPosition = savedInstanceState.getInt(getString(R.string.currentPosition));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        loadingVideoFrame.setVisibility(View.GONE);
        mp.setOnBufferingUpdateListener((mp1, percent) -> {
            if(percent == 100){
                loadingVideoFrame.setVisibility(View.GONE);
            }else{
                loadingVideoFrame.setVisibility(View.VISIBLE);
            }
        });
        mp.setOnSeekCompleteListener(mp12 -> videoView.seekTo(currentPosition));
    }

    private int currentPosition;
}

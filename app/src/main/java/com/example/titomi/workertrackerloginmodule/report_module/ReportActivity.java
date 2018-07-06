package com.example.titomi.workertrackerloginmodule.report_module;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.services.FieldMonitorRecordService;
import com.example.titomi.workertrackerloginmodule.services.FieldMonitorReportUploadService;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.titomi.workertrackerloginmodule.services.FieldMonitorRecordService.RequestPermissionCode;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int ACTIVITY_RECORD_SOUND = 0;
    private static final int TRIM_VIDEO = 10;
    public static ReportActivity instance;
    private final int notification_id = 32;
    NotificationManagerCompat managerCompat;
    NotificationCompat.Builder mBuilder;
    FloatingActionButton fab_photo, fab_record, fab_send, fab_remove_photo,fab_video;
    FloatingActionMenu floatingActionMenu;
    TextView playVideoText;
    Context cxt;
    LinearLayout reportImagesLayout;
    File outputImageMedia;
    Uri file;
    String videoPath = null;
    String recordPath = null;
    ArrayList<String> reportImages = new ArrayList<>();
    MediaRecorder recorder;
    private EditText timeEditText, taskTitleEdit,
            taskDescriptionEdit, institutionNameEdit,
            fullAddressEdit, quantityEdit, contactFullNameEdit, contactNumberEdit, locationEdit, dateEditText, stateEdit, lgaEdit, taskTypeEdit, participantsEdit, quantitySoldEdit, commentsEdit;
    private User loggedInUser;
    private Task selectedTask;
    private String stopLat,stopLong;
    private MediaRecorder mediaRecorder;
    private String AudioSavePathInDevice;

    private static File getOutputMediaFile() {

        String filePath = ImageUtils.ImageStorage.getStorageDirectory(new Task()).getAbsolutePath();
       /* File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "FieldMonitor");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
       /* return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");*/
        return new File(filePath + File.separator +
                "IMG_" + timeStamp + ".jpg");
        //filePath;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);
       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        if (checkPermission()) {
            MediaRecorderReady();
        } else {
            requestPermission();
        }

System.out.println(this.getClass().getPackage());
        cxt = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        floatingActionMenu = findViewById(R.id.floatingActionMenu);
        fab_photo = findViewById(R.id.fab_photo);
        fab_record = findViewById(R.id.fab_record);
        fab_send = findViewById(R.id.fab_send);
        fab_remove_photo = findViewById(R.id.fab_remove_photo);
        fab_video = findViewById(R.id.fab_video);
        playVideoText = findViewById(R.id.playVideoText);

        dateEditText = findViewById(R.id.dateText);
        timeEditText = (EditText)findViewById(R.id.timeText);
        reportImagesLayout = findViewById(R.id.imagesLayout);
        stateEdit = findViewById(R.id.state);
        lgaEdit = findViewById(R.id.lga);

        taskTitleEdit = findViewById(R.id.taskTitle);
        taskDescriptionEdit = findViewById(R.id.description);
        institutionNameEdit = findViewById(R.id.institution);
        fullAddressEdit = findViewById(R.id.fullAddress);
//        quantityEdit = findViewById(R.id.quantity);
        contactFullNameEdit = findViewById(R.id.contactFullName);
        contactNumberEdit = findViewById(R.id.contactPhone);

        taskTypeEdit = findViewById(R.id.taskType);
        locationEdit = findViewById(R.id.location);

        commentsEdit = findViewById(R.id.commentField);
        participantsEdit = findViewById(R.id.participantsEdit);
        quantitySoldEdit = findViewById(R.id.quantityDistributedEdit);


        fab_photo.setOnClickListener(this);
        fab_video.setOnClickListener(this);
        fab_send.setOnClickListener(this);
        fab_record.setOnClickListener(this);

        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
            selectedTask = (Task)extras.getSerializable("task");
            stopLat = extras.getString("stop_lat");
            stopLong = extras.getString("stop_long");

            try {
                setupView(selectedTask);
            }catch (NullPointerException e){
//                Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fab_photo.setEnabled(false);
            fab_video.setEnabled(false);
            fab_record.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

       enableRecordAudioButton();

    }

    private void setupView(Task task){
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy/M/dd");
        dateEditText.setText(dtf.format(task.getDateGiven()));
        timeEditText.setText(task.getTimeGiven());

        stateEdit.setText(task.getState());
        lgaEdit.setText(task.getLga());

        taskTitleEdit.setText(task.getName());
        taskDescriptionEdit.setText(task.getDescription());
        institutionNameEdit.setText(task.getInstitution_name());
        fullAddressEdit.setText(task.getAddress());
        // quantityEdit.setText(""+task.getQuantity());
        contactFullNameEdit.setText(task.getContactName());
        contactNumberEdit.setText(task.getContactNumber());

        taskTypeEdit.setText(task.getWorkType());
        locationEdit.setText(task.getLocation());

    }

    private void captureImage() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        outputImageMedia = getOutputMediaFile();
        file = Uri.fromFile(outputImageMedia);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fab_photo.setEnabled(true);
                fab_video.setEnabled(true);
                fab_record.setEnabled(true);
            }
        }

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;


                    if (StoragePermission && RecordPermission) {

                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        videoPath = savedInstanceState.getString(getString(R.string.videoUrl));
        reportImages = savedInstanceState.getStringArrayList(getString(R.string.images));
        outputImageMedia = (File)savedInstanceState.getSerializable(getString(R.string.taken_picture));
        loggedInUser = (User)savedInstanceState.getSerializable(getString(R.string.loggedInUser));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable(getString(R.string.loggedInUser),loggedInUser);
        outState.putSerializable(getString(R.string.taken_picture),outputImageMedia);
        outState.putStringArrayList(getString(R.string.images),reportImages);
        outState.putString(getString(R.string.videoUrl),videoPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {


                ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(new Task());
                if(storage.imageExists(outputImageMedia.getName())) {
                    //  Util.getRealPathFromURI(cxt, file);
                    if(!reportImages.contains(outputImageMedia.getAbsolutePath())){
                        reportImages.add(outputImageMedia.getAbsolutePath());
                        loadReportImages(reportImages);
                    }
                }

            }



        switch (requestCode){
            case Util.PICK_VIDEO:
                Uri videoUri = data.getData();
              //  Intent i = new Intent(this, ActivityVideoTrimmer.class);
                videoPath =  Util.getVideoPath(cxt,videoUri);
                if(videoPath != null){

                    playVideoText.setVisibility(View.VISIBLE);
                    playVideoText.setOnClickListener(v -> {
                        Intent i1 = new Intent(ReportActivity.this,VideoPlayer.class);
                        i1.putExtra("videoUrl",videoPath);
                        startActivity(i1);
                    });
                } else {
                    return;
                }
              /*  if (videoUri != null) {
                    i.putExtra("video",videoUri.toString());
                    startActivityForResult(i,TRIM_VIDEO);
                }*/

                break;
            case TRIM_VIDEO:
                String uri = data.getExtras().getString("video");
                videoPath =  Util.getVideoPath(this,Uri.parse(uri));



                break;
        }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_photo:
                captureImage();
                break;
            case R.id.fab_send:
                if(!NetworkChecker.haveNetworkConnection(cxt)){return;}
                try {

                Intent i = new Intent(cxt,FieldMonitorReportUploadService.class);
                if (videoPath != null) {
                    i.putExtra("video", videoPath);
                } else {
                    i.putExtra("video", "");
                }
               if(reportImages == null){
                   i.putStringArrayListExtra("images",null);
               } else{
                   i.putStringArrayListExtra("images",reportImages);
               }
               if(recordPath == null){
                   i.putExtra("audio", "");
               }else{
                   i.putExtra("audio", recordPath);
               }



                HashMap<String,String> postData = new HashMap<>();

                    postData.put("user_id",String.format("%d",loggedInUser.getId()));
                    postData.put("task_id",String.format("%d",selectedTask.getId()));
                    String stopTime = DateFormat.getDateTimeInstance().format(new Date()).replaceAll("/","-");
                    postData.put("stop_time",stopTime);
                    postData.put("stop_latitude",stopLat);
                    postData.put("stop_longitude",stopLong);

                    int productId = 0;


                    switch (selectedTask.getWorkType()) {
                        case "Always School Program":
                            productId = 1;
                            break;
                        case "Pampers Hospital Program":
                            productId = 3;
                            break;
                    }

                    postData.put("product_id", "" + productId);

                    postData.put("participants", InputValidator.validateText(participantsEdit,1));
                    postData.put("quantity_sold", InputValidator.validateText(quantitySoldEdit,1));
                    postData.put("challenges", commentsEdit.getText().toString());

                    i.putExtra("postData",postData);
                    i.putExtra(getString(R.string.loggedInUser),loggedInUser);

                    stopService(new Intent(cxt, FieldMonitorRecordService.class));
                    managerCompat.cancel(32);
                    startService(i);

                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.coordinator), "Report will be submitted in the background.\nPlease do not resend", Snackbar.LENGTH_LONG);

                    fab_send.setEnabled(false);
                    fab_photo.setEnabled(false);
                    fab_record.setEnabled(false);
                    fab_video.setEnabled(false);
                    fab_remove_photo.setEnabled(false);


                    snackbar.show();

                    Handler handler = new Handler();
                    handler.postDelayed(this::finish,4000);


                    if(!snackbar.isShown()){
                        finish();
                    }
                } catch(Exception e){
//                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }



                break;
            case R.id.fab_video:
                Util.requestPermission(ReportActivity.this,Util.PICK_VIDEO);
                break;
            case R.id.fab_record:

                Date createdTime = new Date();
                recordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + createdTime.getTime() + "_rec.acc";
                /*File audioPath = new File(recordPath);
                if (!audioPath.exists()){
                    audioPath.mkdir();
                }*/
                /*Intent intent = new Intent(this, ReportActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);*/

                mBuilder = new NotificationCompat.Builder(getBaseContext(), "Record")
                        .setSmallIcon(R.mipmap.app_logo)
                        .setContentTitle("FieldMonitor Record")
                        .setContentText("Recording Session")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).setUsesChronometer(true);


                managerCompat = NotificationManagerCompat.from(getApplicationContext());
                managerCompat.notify(notification_id, mBuilder.build());

                startService(new Intent(cxt, FieldMonitorRecordService.class).putExtra("filePath", recordPath));
                enableRecordAudioButton();
        }
    }

    private void loadReportImages(final ArrayList<String> images){


        reportImagesLayout.removeAllViews();
        //String[] images = task.getImages().split(",");
        for(final String image : images) {
            final View view = getLayoutInflater().inflate(R.layout.report_image_single_item_with_delete_button, null);


            final ImageView reportImage = view.findViewById(R.id.reportImage);
            final ImageView deleteImage = view.findViewById(R.id.delete);

            reportImage.setImageDrawable(Drawable.createFromPath(image));

            deleteImage.setOnClickListener(v -> {
                reportImagesLayout.removeView(view);
                images.remove(image);
            });
            reportImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.viewImages(cxt, reportImage, images);
                }
            });
            reportImagesLayout.addView(view);
        }

    }

    private void recordAudio() throws IOException {
        Intent intent = new Intent(this, FieldMonitorRecordService.class);
//        intent.putExtra();
        startActivityForResult(intent, 300);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(ReportActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void enableRecordAudioButton(){
        if(Util.isMyServiceRunning(this,FieldMonitorRecordService.class)){
            fab_record.setEnabled(false);
            return;
        }else{
            fab_record.setEnabled(true);
        }
    }


    public Bitmap applyWaterMarkEffect(Bitmap src, String watermark, int x, int y, int color, int alpha, int size, boolean underline) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setUnderlineText(underline);
        canvas.drawText(watermark, x, y, paint);

        return result;
    }
}








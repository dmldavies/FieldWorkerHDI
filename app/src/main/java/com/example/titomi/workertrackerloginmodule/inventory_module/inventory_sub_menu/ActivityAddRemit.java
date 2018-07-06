package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Remittance;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.MediaUploader;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityAddRemit extends AppCompatActivity implements View.OnClickListener {

    private static final int MAX_IMAGE = 6;
    User loggedInUser;
    EditText amountText;
    TextView attachText;
    Context cxt;
    LinearLayout proofMediaLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //error reporting
//        Mint.setApplicationEnvironment(Mint.appEnvironmentTesting);
//
//        Mint.initAndStartSession(this.getApplication(), "fa0aaf30");

        cxt = this;
        setContentView(R.layout.activity_add_remit);
        amountText = findViewById(R.id.amountEdit);
        attachText = findViewById(R.id.attach);
        proofMediaLayout = findViewById(R.id.proofMediaLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }

        attachText.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_enter_remittance_record,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.send:
                try {
                    sendRemittance();
                } catch (InputValidator.InvalidInputException e) {
                    Toast.makeText(cxt, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Util.pickMultiPhoto(cxt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data !=null)
        {
            switch (requestCode){
                case Util.PICK_IMAGE_MULTIPLE:
                    String[] imagesPath = data.getStringExtra("data").split("\\|");
                    for (int i = 0; i < imagesPath.length; i++) {
                        if (!canTakeImages()) {
                            Toast.makeText(this,
                                    String.format("Can only accept a maximum of %d images"
                                            , MAX_IMAGE), Toast.LENGTH_LONG).show();
                            break;
                        }
                        if (i >= MAX_IMAGE) {
                            Toast.makeText(this,
                                    String.format("Can only accept a maximum of %d images"
                                            , MAX_IMAGE), Toast.LENGTH_LONG).show();
                            break;
                        }

                        proofImages.add(imagesPath[i]);


                    }


                    loadFeaturedImages(proofImages);

                    break;
            }
        }

    }

    private boolean canTakeImages(){
        return proofImages.size() < MAX_IMAGE;
    }

    private void  loadFeaturedImages(final ArrayList<String> imageList){



        proofMediaLayout.removeAllViews();


        for(final String path : imageList) {


            // featuredImages.add(path); //and new image to list
            final View view = View.inflate(cxt,R.layout.report_image_single_item_with_delete_button,null);
            final ImageView image =view.findViewById(R.id.reportImage);
            ImageView delete = view.findViewById(R.id.delete);
            image.setImageURI(Uri.parse(path));
            image.setTag(path);


            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //image.setLeft(5);
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Util.viewImages(cxt,image,imageList);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proofMediaLayout.removeView(view);
                    imageList.remove(path);
                }
            });


            proofMediaLayout.addView(view);
        }

    }

    private void sendRemittance() throws InputValidator.InvalidInputException {
        HashMap<String, String> postData = new HashMap<>();
        String supervisorId = loggedInUser.getRoleId() == User.SUPERVISOR ?
                "" + loggedInUser.getId() : "" + loggedInUser.getSupervisorId();
        postData.put("supervisor_id", supervisorId);
        postData.put("amount", InputValidator.validateText(amountText, 2));
        postData.put("worker_id", "" + loggedInUser.getId());



        ArrayList<String> images = ImageUtils.compressImages(cxt, new Remittance(), proofImages);
        //Toast.makeText(cxt,""+images.size(),Toast.LENGTH_LONG).show();
        StringBuilder sb = new StringBuilder();
        for (String im : images) {
            sb.append("images/remittance/").append(new File(im).getName()).append(",");
        }


            String s = sb.deleteCharAt(sb.lastIndexOf(",")).toString();
        if(s.isEmpty()){
            Toast.makeText(cxt,"You must attach a proof of payment",Toast.LENGTH_LONG).show();
            return;
        }
            postData.put("proof", s);


        String remitUrl = loggedInUser.getRoleId() == User.SUPERVISOR ?
                "work/supervisor_add_remit.php" : "work/worker_add_remit.php";
        String url = getString(R.string.api_url) + remitUrl + "?key=" + getString(R.string.field_worker_api_key);


        try {
          //  System.err.println(Network.getPostDataString(postData));
            new RemittanceNetwork(cxt, proofImages).execute(url + "&" + Network.getPostDataString(postData));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private class RemittanceNetwork extends AsyncTask<String, Void, String> {

        ArrayList<String> images;
        HashMap<String, String> postData;
        ProgressDialog progressDialog;
        Context cxt;

        public RemittanceNetwork(Context cxt, ArrayList<String> images) {
            this.images = images;

            this.cxt = cxt;
        }

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(cxt);
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            // if(s == null) return;





            try {
                JSONObject obj = new JSONObject(s);
                //    if(obj.getInt("statusCode") == 1){
                ImageUploader imageUploader = new ImageUploader(cxt, cxt.getString(R.string.api_url) + cxt.getString(R.string.proof_image_uploader) + "?key=" + cxt.getString(R.string.field_worker_api_key));
                imageUploader.execute(images);
                //   }
            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            }
        }
    }

    private static class ImageUploader extends MediaUploader {

        Context cxt;
        ProgressDialog progressDialog;

        public ImageUploader(Context cxt, String uploadApiUrl) {
            super(cxt, uploadApiUrl);
            this.cxt = cxt;


            progressDialog = new ProgressDialog(cxt);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);


            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Toast.makeText(cxt, "Record added successfully", Toast.LENGTH_LONG).show();
                ((Activity) cxt).finish();
            }

        }
    }
  private  ArrayList<String> proofImages = new ArrayList<>();


}

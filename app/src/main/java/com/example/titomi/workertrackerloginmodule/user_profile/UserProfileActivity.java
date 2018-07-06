package com.example.titomi.workertrackerloginmodule.user_profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener{

    Button editProfileBtn;

    Toolbar toolbar;
    TextView profileEmail,profileContact,profileAddress,profileRole,userFullname;
    private User loggedInUser;
    private Context cxt;
    private CircleImageView profileImage;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        editProfileBtn = findViewById(R.id.editProfile);

        toolbar = findViewById(R.id.toolbar);

        profileAddress = findViewById(R.id.profile_address);
        profileContact = findViewById(R.id.profile_contact);
        profileEmail = findViewById(R.id.profile_email);
        profileRole = findViewById(R.id.role);
        profileImage = findViewById(R.id.profile_image);
        userFullname = findViewById(R.id.userFullname);
        cxt = this;

        Bundle extras =getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
            setupView();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.change_photo_image).setOnClickListener(this);



    }

    private void setupView(){
        if(loggedInUser != null){
         ///   ImageUtils.loadImage(cxt,loggedInUser,profileImage);
            profileEmail.setText(loggedInUser.getEmail());
            profileRole.setText(Util.toSentenceCase(loggedInUser.getRole()));
            profileAddress.setText(loggedInUser.getAddress());
            profileContact.setText(loggedInUser.getPhoneNumber());
            userFullname.setText(loggedInUser.getFullName());
           if(loggedInUser.getFeaturedImage() == null ||
                   loggedInUser.getFeaturedImage().isEmpty() ||
                   loggedInUser.getFeaturedImage().equalsIgnoreCase("null")){
               return;
           }


            ImageUtils.loadImage(cxt,loggedInUser,profileImage);
            profileImage.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_photo_image:
                Toast.makeText(this,"Feature coming soon",Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile_image:
                Util.viewImage(cxt,profileImage);
                break;
        }
    }
}

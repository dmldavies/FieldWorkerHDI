package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.DatabaseAdapter;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;

import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import static com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator.validateText;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public class ActivityAdminNewMessage extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private User loggedInUser;
    private EditText titleEdit,messageBodyEdit;
    private Button cancelButton,sendMessageButton;
    private Context cxt;
    private String messageTitle;
    private String messageBody;
    private Spinner sendToSpinner, userSpinner,prioritySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_new_message_layout);

        cxt = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleEdit = (EditText)findViewById(R.id.title);
        messageBodyEdit = (EditText)findViewById(R.id.messageBody);
        sendMessageButton = (Button)findViewById(R.id.sendMessage);
        cancelButton = (Button)findViewById(R.id.cancel);
        sendToSpinner = (Spinner)findViewById(R.id.sendToSpinner);
        userSpinner = findViewById(R.id.userSpinner);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        sendMessageButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        sendToSpinner.setOnItemSelectedListener(this);
        if(getIntent() != null){
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageBodyEdit.setText(messageBody);
        titleEdit.setText(messageTitle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(getString(R.string.user), loggedInUser);
        outState.putString(getString(R.string.message_title), titleEdit.getText().toString());
        outState.putString(getString(R.string.message_body), messageBodyEdit.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        loggedInUser = (User)savedInstanceState.getSerializable(getString(R.string.user));
        messageBody = savedInstanceState.getString(getString(R.string.message_body));
        messageTitle = savedInstanceState.getString(getString(R.string.message_title));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.sendMessage:
                sendMessage();
                break;

        }

    }

    @SuppressLint("StaticFieldLeak")
    private void sendMessage(){
        if(!NetworkChecker.haveNetworkConnection(cxt))return;
        try {

          final  HashMap<String,String> postData = new HashMap<>();
            String receiverText = "";
            if(users.isEmpty()){


                postData.put("recepientType",""+5);
                receiverText = sendToSpinner.getSelectedItem().toString();

            }else{
                postData.put("recepientType",""+8);
                receiverText = users.get(userSpinner.getSelectedItemPosition()).getName();
                postData.put("receiver",""+users.get(userSpinner.getSelectedItemPosition()).getId());

            }
            postData.put("sender",""+loggedInUser.getId());
            postData.put("subject",InputValidator.validateText(titleEdit,3));
            postData.put("body",InputValidator.validateText(messageBodyEdit,3));
            postData.put("priority",prioritySpinner.getSelectedItem().toString());


            final String finalReceiverText = receiverText;
            new android.os.AsyncTask<String,Void,String>(){
                @Override
                protected String doInBackground(String... strings) {
                    return Network.performPostCall(strings[0],postData);
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                Util.disableChildrenViews(cxt,false,R.id.parent);
                    Toast toast = Toast.makeText(cxt,"Sending message.\nPlease wait...",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                    toast.show();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Util.disableChildrenViews(cxt,true,R.id.parent);

                    if(s == null) return;

                    try {
                        JSONObject object = new JSONObject(s);
                         if(object.getInt("statusCode") == 0){
                             Toast toast = Toast.makeText(cxt,object.getString("message"),Toast.LENGTH_LONG);
                              toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                              toast.show();

                              saveMessageToUserPhone(postData.get("sender"),postData.get("subject"), postData.get("body"), postData.get("priority"), finalReceiverText);
                            if(!users.isEmpty()){
                                Util.clearSpinner(userSpinner);
                            }
                             Util.clearSpinner(sendToSpinner);
                            Util.clearEditTexts(titleEdit,messageBodyEdit);
                         }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.err.print(s);
                    }


                }
            }.execute(getString(R.string.api_url)+getString(R.string.add_message_url)+"?key="+getString(R.string.field_worker_api_key));
        } catch (InputValidator.InvalidInputException e) {
            Toast.makeText(cxt, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveMessageToUserPhone(String senderId, String subject, String body, String priority, String receiver) {
        DatabaseAdapter db = DatabaseAdapter.getInstance(cxt);
        db.saveOutBox(senderId,subject,body,receiver,priority);


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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == parent.getCount() - 1){
            userSpinner.setVisibility(View.VISIBLE);

            loadUserSpinner(position);
        }else{
            userSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    private void loadUserSpinner(int userType){
        String url = "supervisor/view.php?view=get_workers&id="+loggedInUser.getId();


        new android.os.AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... strings) {
                return Network.backgroundTask(null,strings[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(s == null)return;

                try {
                    JSONArray jsonArray = new JSONArray(s);
                    users.clear();
                    for(int i = 0; i< jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        final User user  =new User();
                        user.setId(obj.getInt("id"));
                        user.setName(obj.getString("line_id"));
                        user.setEmail(obj.getString("email"));
                        user.setFeaturedImage(obj.getString("photo"));
                        users.add(user);

                        userSpinnerAdapter = new ArrayAdapter<User>(cxt,android.R.layout.simple_spinner_item,users){
                            @Nullable



                            @Override
                            public long getItemId(int position) {
                                return users.get(position).getId();
                            }

                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                LayoutInflater inflater = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                User user = users.get(position);

                                convertView = inflater.inflate(R.layout.user_spinner_layout,null);
                                TextView username = convertView.findViewById(R.id.username);
                                 username.setText(user.getName());
                                ImageView userImage = convertView.findViewById(R.id.user_icon);
                                DrawableManager drm = new DrawableManager();
                                 drm.fetchDrawableOnThread(getString(R.string.api_url)+user.getFeaturedImage(),userImage);
                                return convertView;
                            }

                            @Override
                            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                return this.getView(position, convertView, parent);
                            }
                        };
                        userSpinner.setAdapter(userSpinnerAdapter);
                    }
                } catch (JSONException e) {
                    System.err.println(s);
                }
            }
        }.execute(getString(R.string.api_url)+
                url+"&key="+getString(R.string.field_worker_api_key));
    }


    ArrayList<User> users = new ArrayList<>();
    ArrayAdapter<User> userSpinnerAdapter;


}

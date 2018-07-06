package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.DatabaseAdapter;
import com.example.titomi.workertrackerloginmodule.supervisor.Messages;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
/**
 * Cre
 * ated by NeonTetras on 13-Feb-18.
 */
public class ActivityMessageListing extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,View.OnClickListener {

    private ProgressBar progressBar;
    private LinearLayout messagesLinearLayout;
    private Spinner sortBySpinner;
    private ListView messageList;
    private Context cxt;
    private TabHost tabHost;
    private TabHost.TabSpec tabSpec;
    private LinearLayout inboxLinearLayout,outBoxLinearLayout;
    private ListView messageInboxList,messageOutboxList;
    private FloatingActionButton actionButton;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cxt = this;
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        messagesLinearLayout = (LinearLayout)findViewById(R.id.messagesLinearLayout);
        actionButton = findViewById(R.id.actionButton);
        //sortBySpinner = (Spinner)findViewById(R.id.outBoxSortBy);
        messageList = (ListView)findViewById(R.id.messageInboxList);
        inboxLinearLayout = findViewById(R.id.inboxLinearLayout);
        outBoxLinearLayout = findViewById(R.id.outBoxLinearLayout);
        tabHost = (TabHost) findViewById(R.id.tabhost);
        messageInboxList = findViewById(R.id.messageInboxList);
        messageOutboxList = findViewById(R.id.messageOutboxList);

        tabHost.setup();
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
        }

        tabSpec = tabHost.newTabSpec("Inbox");
        tabSpec.setContent(R.id.inbox_tab);
        tabSpec.setIndicator("Inbox");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Outbox");
        tabSpec.setContent(R.id.outbox_tab);
        tabSpec.setIndicator("Outbox");
        tabHost.addTab(tabSpec);


        messageList.setOnItemClickListener(this);
        messageOutboxList.setOnItemClickListener(this);
        messageOutboxList.setOnItemLongClickListener(this);
        actionButton.setOnClickListener(this);
        getInboxMessages();
        getOutBoxMessages();

        if(loggedInUser.getRoleId() != User.SUPERVISOR){
            actionButton.setVisibility(View.GONE);
            tabHost.getTabWidget().removeView(tabHost.getTabWidget().getChildTabViewAt(1));

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getInboxFromDb();
        getOutBoxMessages();
    }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if(loggedInUser != null) {
                outState.putSerializable(getString(R.string.loggedInUser), loggedInUser);
            }

        }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            loggedInUser = (User) savedInstanceState.getSerializable(getString(R.string.loggedInUser));
        }


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Messages message = (Messages) parent.getItemAtPosition(position);
        Intent i = new Intent(cxt,ActivityViewMessage.class);
            i.putExtra(getString(R.string.loggedInUser),loggedInUser);
         i.putExtra(getString(R.string.message),message);
        startActivity(i);
    }

    private void getMessages(){
        getInboxMessages();
        getOutBoxMessages();
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

    private void saveInboxMessages(Messages msg){
        DatabaseAdapter db=  DatabaseAdapter.getInstance(cxt);
        if(!db.messageExists(msg.getId())){
            ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(msg.getPoster());
            String imageUrl = getString(R.string.server_url)+msg.getFeaturedImage();
            String imageName = ImageUtils.getImageNameFromUrlWithExtension(imageUrl);
             if(!storage.imageExists(imageName)){
                 ImageUtils.GetImages getImages = new ImageUtils.GetImages(msg.getPoster(),imageUrl,imageName);
                 getImages.execute();
             }


        //    Toast.makeText(cxt,"Message does not exists so save",Toast.LENGTH_SHORT).show();
            if(db.saveInBox(msg.getPoster().getId(),msg.getReceiver().getId(),msg.getId(),msg.getTitle(),msg.getBody(),msg.getPoster().getName(),msg.getPoster().getFeaturedImage(),msg.getPriority()) != -1){
             //   Toast.makeText(cxt,"Message saved",Toast.LENGTH_SHORT).show();
            }//else Toast.makeText(cxt,"Message  not saved",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getInboxMessages(){

        new android.os.AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... strings) {
                return Network.backgroundTask(null,strings[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.inBoxProgressBar).setVisibility(View.VISIBLE);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                findViewById(R.id.inBoxProgressBar).setVisibility(View.GONE);

                if(s == null){
                    getInboxFromDb();
                    return;
                }


                try {
                    JSONArray jsonArray = new JSONArray(s);

                    if(jsonArray.length() == 0){
                        getInboxFromDb();

                    }
                        inboxMessages.clear();
                     for(int i = 0; i<jsonArray.length(); i++) {
                         JSONObject msgs = jsonArray.getJSONObject(i);
                         JSONObject obj = msgs.getJSONObject("message");
                         Messages msg = new Messages();
                        msg.setPriority(obj.getString("priority"));
                         msg.setBody(obj.getString("body"));
                         msg.setId(obj.getInt("id"));
                         msg.setTitle(obj.getString("subject"));
                         SimpleDateFormat dtf = new SimpleDateFormat("dd/M/yyyy H:m:s");

                         msg.setDatePosted(dtf.parse(msgs.getString("date")));

                         JSONObject recipient = msgs.getJSONObject("receipient");
                            User receiver = new User();
                         receiver.setId(recipient.getInt("id"));
                         receiver.setName(recipient.getString("first_name")+" "+recipient.getString("last_name"));

                         receiver.setUserLevel(recipient.getInt("roleId"));
                         receiver.setUserLevelText(recipient.getString("role"));
                         JSONObject sender = msgs.getJSONObject("sender");
                         User _sender = new User();
                         _sender.setId(sender.getInt("id"));
                         _sender.setName(sender.getString("first_name")+" "+sender.getString("last_name"));
                         _sender.setFeaturedImage(sender.getString("photo"));
                        _sender.setUserLevel(sender.getInt("roleId"));
                         _sender.setUserLevelText(sender.getString("role"));
                         msg.setFeaturedImage(_sender.getFeaturedImage());

                         msg.setPoster(_sender);
                         msg.setReceiver(receiver);

                         saveInboxMessages(msg);
                          //inboxMessages.add(msg);


                       //   populateMessage(inboxMessages,messageInboxList);
                     }

                    getInboxFromDb();
                } catch (JSONException | ParseException   e) {
                    e.printStackTrace();
                    System.err.println(s);
                }

            }
        }.execute(getString(R.string.api_url)+getString(R.string.view_message_url)+"?key="+getString(R.string.field_worker_api_key)+"&msg_type=inbox&user_id="+loggedInUser.getId());

    }

    private void getInboxFromDb() {
        db = DatabaseAdapter.getInstance(cxt);
        Cursor c = db.fetchInboxMessages(loggedInUser.getId());

        if(c.moveToFirst()){
            inboxMessages.clear();
          //c.moveToNext();
         //   Toast.makeText(cxt,c.getColumnIndex(DatabaseAdapter.MSG_BODY),Toast.LENGTH_SHORT).show();
            for(int i = 0; i<c.getCount(); i++){
                Messages msg = new Messages();
                msg.setId(c.getInt(c.getColumnIndex(DatabaseAdapter.MSG_ID)));
                msg.setBody(c.getString(c.getColumnIndex(DatabaseAdapter.MSG_BODY)));
                msg.setTitle(c.getString(c.getColumnIndex(DatabaseAdapter.SUBJECT)));

                User user = new User();
                user.setName(c.getString(c.getColumnIndex(DatabaseAdapter.SENDER)));
                msg.setPoster(user);
                msg.setPriority(c.getString(c.getColumnIndex(DatabaseAdapter.PRIORITY)));
                msg.setFeaturedImage(c.getString(c.getColumnIndex(DatabaseAdapter.SENDER_IMAGE)));
                msg.setRead(c.getInt(c.getColumnIndex(DatabaseAdapter.IS_READ)) != 0);
             SimpleDateFormat   dtf= new SimpleDateFormat("yyyy/M/dd HH:mm:ss");
                try {
                    msg.setDatePosted(dtf.parse(
                            c.getString(
                                    c.getColumnIndex(DatabaseAdapter.DATE_TIME)
                            ).replaceAll("-","/")));

                } catch (ParseException e) {
                    e.printStackTrace();

                }
                inboxMessages.add(msg);
                c.moveToNext();
            }
            populateMessage(inboxMessages,messageInboxList);
        }else{
            Toast.makeText(cxt,"No messages",Toast.LENGTH_SHORT).show();
        }

    }

    private void populateMessage(final ArrayList<Messages> msgList,ListView listView) {
        //inboxLinearLayout.removeAllViews();

        ArrayAdapter<Messages> msgAdapter = new ArrayAdapter<Messages>(cxt,R.layout.messages_single_item_layout,msgList){


            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Messages msg = msgList.get(position);
                LayoutInflater inflater = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout messageLayout = (LinearLayout)inflater.inflate(R.layout.messages_single_item_layout,null);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,5,0,0);
                messageLayout.setLayoutParams(layoutParams);
                ImageView userImage = messageLayout.findViewById(R.id.user_icon);
                TextView bodyText = messageLayout.findViewById(R.id.messageBody);
                TextView titleText = messageLayout.findViewById(R.id.title);
                TextView dateTime = messageLayout.findViewById(R.id.dateTime);
                TextView usernameText = messageLayout.findViewById(R.id.username);
                TextView newMessage = messageLayout.findViewById(R.id.newText);

                ImageView priorityImage = messageLayout.findViewById(R.id.priorityImage);
                /*DrawableManager drm = new DrawableManager();
                drm.fetchDrawableOnThread(getString(R.string.server_url)+msg.getFeaturedImage(),userImage);*/
                ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(msg.getPoster());
                try {
                    String imageName = ImageUtils.getImageNameFromUrlWithExtension(msg.getFeaturedImage());
                    if (storage.imageExists(imageName)) {
                        userImage.setImageURI(Uri.parse(storage.getImage(imageName).getAbsolutePath()));
                    }
                }catch (NullPointerException e){}

                StringBuilder sb = new StringBuilder();
                 sb.append(msg.getBody().substring(0,msg.getBody().length() > 30 ? 30 : msg.getBody().length()));
                 if(msg.getBody().length() > 30){
                     sb.append("...");
                 }
                bodyText.setText(sb.toString());
                titleText.setText(msg.getTitle());
                DateFormat dtf = DateFormat.getDateTimeInstance();
                //etDateTimeInstance(DateFormat.FULL,Locale.getDefault());
                dateTime.setText(dtf.format(msg.getDatePosted()));
                usernameText.setText(Util.toSentenceCase(String.format("%s ",msg.getPoster().getName())));

                if(msg.isRead()){
                    newMessage.setVisibility(View.GONE);

                }
                if(!msg.getPriority().equalsIgnoreCase("high")){
                    priorityImage.setVisibility(View.GONE);
                }

                return messageLayout;
            }
        };
        listView.setAdapter(msgAdapter);


    }

    private void getOutBoxMessages(){
        db = DatabaseAdapter.getInstance(cxt);
        Cursor cursor = db.fetchOutboxMessages();

       // Toast.makeText(cxt,"Len"+cursor.getCount(),Toast.LENGTH_SHORT).show();
//       / String msgBody = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.MSG_BODY));
      //  Toast.makeText(cxt,msgBody,Toast.LENGTH_SHORT).show();
        outboxMessages.clear();
        if(cursor.moveToFirst()) {
            for(int i=0; i < cursor.getCount(); i++) {
                String msgBody = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.MSG_BODY));
                String subject = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.SUBJECT));
                String dateTime = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.DATE_TIME));
                String destination = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.DESTINATION));
               String priority = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.PRIORITY));
                long id = cursor.getInt(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));

                Messages msg = new Messages();
                msg.setTitle(subject);
                msg.setBody(msgBody);
                msg.setId(id);
               msg.setPriority(priority);
                msg.setRecipient(destination);
                User user = new User();
                user.setName(destination);
                msg.setPoster(user);
                msg.setRead(true);
SimpleDateFormat dtf= new SimpleDateFormat("yyyy/M/dd HH:mm:ss");
                try {
                    msg.setDatePosted(dtf.parse(dateTime.replaceAll("-","/")));

                } catch (ParseException e) {
                    e.printStackTrace();

                }

                outboxMessages.add(msg);
                cursor.moveToNext();
            }
        }

        populateMessage(outboxMessages,messageOutboxList);
    }
    ArrayList<Messages> inboxMessages = new ArrayList<>();
    ArrayList<Messages> outboxMessages = new ArrayList<>();

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.actionButton:
                Intent i = new Intent(cxt,ActivityAdminNewMessage.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);
                startActivity(i);
           // startActivity(ActivityAdminNewMessage.class);
                break;
        }
    }

    private void startActivity(Class<?> activity){
        startActivity(new Intent(this,activity));
    }

    DatabaseAdapter db;

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Messages msg =outboxMessages.get(i);


        final AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
        alertDialog.setMessage("Are you sure you want to delete this message?");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"No",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(db.deleteMessage(msg.getId())){
                    getOutBoxMessages();
                    Toast.makeText(cxt,"Message deleted successfully",Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();

            }
        });
        alertDialog.show();

        return false;
    }
}

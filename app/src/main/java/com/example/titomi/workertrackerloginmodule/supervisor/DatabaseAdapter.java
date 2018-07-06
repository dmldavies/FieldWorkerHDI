package com.example.titomi.workertrackerloginmodule.supervisor;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	
	//Database info
	private static final String DATABASE_NAME = "fieldmonitor.db";

	public static final String OUTBOX_TABLE = "outbox_table";
	public static final String INBOX_TABLE = "inbox_table";
	private static final int DATABASE_VERSION = 12;
	
	//Table info for contact groups
	public static final String KEY_ID = "_id";
	public static final String MSG_ID = "_msg_id";
    public static final String IS_READ = "_is_read";


	
	//Table info for sent sms
	public static final String SENDER_ID = "_sender_id";
	public static final String RECIPIENT_ID = "_recipient_id";
	public static final String MSG_BODY = "_msg";
	public static final String SUBJECT = "_subject";
	public static final String DATE_TIME = "_date_time";
	public static final String PRIORITY = "_priority";

	public static final String DESTINATION = "_destination";
	public static final String SENDER = "_sender";
	
	
	
	//SQL Statement to create a new database.


	private static final String OUTBOX_CREATE = "CREATE TABLE " +
			OUTBOX_TABLE +" ("+KEY_ID+
			" integer primary key autoincrement," +
			SENDER_ID+" integer(11) not null," +
			MSG_BODY+" text(1000) not null," +
			DESTINATION+" text(1000) not null," +
			SUBJECT +" text(100) not null," +
			PRIORITY +" text(6) not null ," +
			DATE_TIME+" DATETIME DEFAULT CURRENT_TIMESTAMP); ";
	public static final String SENDER_IMAGE = "_image";

	private static final String INBOX_CREATE = "CREATE TABLE " +
			INBOX_TABLE +" ("+KEY_ID+
			" integer primary key autoincrement," +
			SENDER_ID+" integer(11) not null," +
			RECIPIENT_ID+" integer(11) not null," +
			MSG_BODY+" text(1000) not null," +
			MSG_ID+" integer(10) not null," +
			SENDER+" text(1000) not null," +
			SENDER_IMAGE+" text(1000) not null," +
			SUBJECT +" text(100) not null," +
			PRIORITY +" integer(6) ," +
            IS_READ +" integer(1) DEFAULT 0," +
			DATE_TIME+" DATETIME DEFAULT CURRENT_TIMESTAMP); ";
	private DatabaseHelper mDbHelper;

	private static SQLiteDatabase mDb;
	
	private static Context context;

	private  static DatabaseAdapter dbInstance;// = new DatabaseAdapter();
	
	private DatabaseAdapter(Context cxt) {
	
	this.context = cxt;
		mDbHelper = new DatabaseHelper(context);
		mDb = mDbHelper.getWritableDatabase();

	}

	public static synchronized DatabaseAdapter getInstance(Context cxt){
		context = cxt;
		if(dbInstance == null) {
			dbInstance = new DatabaseAdapter(cxt);
		}
			return dbInstance;


	}

	
	public long saveOutBox(String senderId,String subject,String body,String destination,String priority){
		//Create a new row of values to insert
		
		ContentValues newValues = new ContentValues();
		
		//Assign value for each row
		
		newValues.put(SENDER_ID, senderId);
		newValues.put(SUBJECT, subject);
		newValues.put(MSG_BODY,body);
		newValues.put(DESTINATION,destination);
		newValues.put(PRIORITY,priority);
	
		
		return mDb.insert(OUTBOX_TABLE,null,newValues);
	}

	public boolean messageExists(long messageId){
		Cursor c =  mDb.query(INBOX_TABLE, new String[]{"*"}, MSG_ID+" = ?", new String[]{""+messageId}, null, null, KEY_ID+" DESC");
		int count = c.getCount();
		c.close();
		return count != 0;
	}

    public boolean readMessage(long messageId){
	    ContentValues val = new ContentValues();
	    val.put(IS_READ,1);
	  return   mDb.update(INBOX_TABLE,val,MSG_ID +" = "+messageId,null) != 0;

    }
	public long saveInBox(long senderId,long recipientId,long msg_id,String subject, String msg_body, String senderName,String senderPic,String priority){
		//Create a new row of values to insert
		
		ContentValues newValues = new ContentValues();
		
		//Assign value for each row
		
		newValues.put(SENDER_ID, senderId);
		newValues.put(RECIPIENT_ID, recipientId);
		newValues.put(MSG_ID, msg_id);
		newValues.put(MSG_BODY, msg_body);
		newValues.put(SUBJECT,subject);
		newValues.put(SENDER,senderName);
        newValues.put(SENDER_IMAGE,senderPic);
		newValues.put(PRIORITY,priority);
	
		
		return mDb.insert(INBOX_TABLE,null,newValues);
	}
	/*
	public long deleteLogin()
	{
		return mDb.delete(LOGIN_TABLE, "1", null);
	}
	public long createLogin(String username,String password){
		//Create a new row of values to insert
		
		ContentValues newValues = new ContentValues();
		
		//Assign value for each row
		
		newValues.put(USERNAME, username);
		newValues.put(PASSWORD, password);
		
		
		return mDb.insert(LOGIN_TABLE,null,newValues);
	}*/
	/* boolean dropTables(){
	public
		mDb.execSQL("DROP TABLE IF EXISTS "+ MESSAGE_TABLE);
		mDb.execSQL("DROP TABLE IF EXISTS "+LOGIN_TABLE);
		System.err.println("Unwanted columns dropped");
	

	return true;
	}
	*/
	public Cursor fetchInboxMessages(long userId){
		return mDb.query(INBOX_TABLE, new String[]{"*"}, RECIPIENT_ID+" = "+userId, null, null, null, KEY_ID+" DESC");
		
	}

	public boolean deleteMessage(long id){

		return mDb.delete(OUTBOX_TABLE, KEY_ID+"= ? ",new String[]{""+id}) > 0;
	}

	public Cursor fetchOutboxMessages(){
		return mDb.query(OUTBOX_TABLE, new String[]{"*"}, null, null, null, null, KEY_ID+" DESC");

	}
private static class DatabaseHelper extends SQLiteOpenHelper {
	
	//public constructor
	public DatabaseHelper(Context cxt){
		super(cxt,DATABASE_NAME,null,DATABASE_VERSION);
		
		
	}
	
	//Called when no database exists in the disk and the helper
	//class needs to create a new one
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL(INBOX_CREATE);
		//db.execSQL(LOGIN_CREATE);
		db.execSQL(OUTBOX_CREATE);
		
	}
	//Called when there is a database version mismatch meaning that
	//the version of the database on the disk needs to be upgraded
	//to the current version
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//Log the version upgrade
		Log.w("TaskDBAdapter","Upgrading from version "+oldVersion+
				"to "+newVersion +", which will destroy old data");
		
	db.execSQL("DROP TABLE IF EXISTS "+ INBOX_TABLE);
	//db.execSQL("DROP TABLE IF EXISTS "+LOGIN_TABLE);
	db.execSQL("DROP TABLE IF EXISTS "+ OUTBOX_TABLE);
		onCreate(db);

	}
	
}

}

package atis.vn.smsnotification.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import atis.vn.smsnotification.model.SMS;

public class SmsDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sms.sqlite";
    private static final String TABLE_NAME = "sms_notification";
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String STATUS = "status";
    private static final String CONTENT = "content";

    private static final String SENDER = "sender";
    private static final String RECEIVED_TIME = "received_time";
    private static final String SEND_TIME = "send_time";
    private static final String ATTEMPT = "attempt";

    private static final int VERSION = 2;
    SQLiteDatabase database;


    private String SQLQuery =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    ID + " integer primary key autoincrement, " +
                    TYPE + " TEXT, " +
                    STATUS + " integer, " +
                    CONTENT + " TEXT, " +

                    SENDER + " TEXT, " +
                    RECEIVED_TIME + " TEXT, " +
                    ATTEMPT + " integer, " +
                    SEND_TIME + " TEXT" +
                    " ) ";

    private String UNIQUE_INDEX =
            "CREATE UNIQUE INDEX unique_sender_code ON sms_notification (sender);";


    public SmsDatabase(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
        Log.e("carpla-thangnm-dev", "onCreate: 1");
        getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e("carpla-thangnm-dev", "onCreate: 2");
        sqLiteDatabase.execSQL(SQLQuery);
        sqLiteDatabase.execSQL(UNIQUE_INDEX);

    }




    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Create
    public boolean addSms(SMS sms) {

        database=   getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TYPE, sms.getType());
        values.put(CONTENT, sms.getContent());

        if(sms.getSender() != null){
            values.put(SENDER, sms.getSender()+"");
        }


        values.put(RECEIVED_TIME, sms.getReceivedTime());
        values.put(SEND_TIME, sms.getSendTime());
        values.put(ATTEMPT, sms.getAttempt());
        values.put(STATUS, sms.getStatus());

        boolean insertResult = database.insert(TABLE_NAME, null, values) > 0;
        Log.e("carpla-thangnm-dev", "result_add_db: " + sms.getSender());
        database.close();
        return insertResult;
    }

    //Find by sender, content and send time
    @SuppressLint("Range")
    public ArrayList<SMS> findManySms(String condition) {
        ArrayList<SMS> smsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + condition;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SMS sms = new SMS();
                sms.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                sms.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
                sms.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));

                sms.setSender(cursor.getString(cursor.getColumnIndex(SENDER)));
                sms.setReceivedTime(cursor.getString(cursor.getColumnIndex(RECEIVED_TIME)));
                sms.setSendTime(cursor.getString(cursor.getColumnIndex(SEND_TIME)));
                sms.setAttempt(cursor.getInt(cursor.getColumnIndex(ATTEMPT)));
                sms.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
                Log.d("content_sms",cursor.getString(cursor.getColumnIndex(CONTENT)) );
                smsList.add(sms);
            } while (cursor.moveToNext());
        }
        return smsList;
    }

//    public boolean deleteSentSms() {
//        SQLiteDatabase database = this.getWritableDatabase();
//        boolean result = database.delete(TABLE_NAME, "status = true", null) > 0;
//        database.close();
//        return result;
//    }

    public boolean deleteSMSById(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        boolean result = database.delete(TABLE_NAME, ID + "=?", new String[]{String.valueOf(id)}) > 0;
        database.close();
        return result;
    }

    public boolean updateSMSById(int id, int newStatus, int newAttempt) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ATTEMPT, newAttempt);
        cv.put(STATUS, newStatus);
        boolean result = database.update(TABLE_NAME, cv,ID + "=?", new String[]{String.valueOf(id)}) >0;
        database.close();
        return result;
    }

}

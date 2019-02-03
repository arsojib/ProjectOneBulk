package com.example.arsojib.bulksms.DataFetch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.Model.Message;

import java.util.ArrayList;

/**
 * Created by AR Sajib on 1/29/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "SmsManagement";
    private static final int DB_VERSION = 1;

    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_NUMBERS = "numbers";
    private static final String TABLE_SCHEDULE = "schedule";
    private static final String TABLE_SCHEDULE_NUMBER = "schedule_number";

    private static final String ID = "id";
    private static final String MESSAGE_ID = "message_id";
    private static final String SCHEDULE_ID = "schedule_id";
    private static final String MESSAGE = "message";
    private static final String NUMBER = "number";
    private static final String STATUS = "status";
    private static final String TIME = "time";
    private static final String TOTAL = "total";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " ( " + ID + " BIGINT, " + MESSAGE + " VARCHAR, " + TIME + " BIGINT " + ");";
    private static final String CREATE_TABLE_NUMBERS = "CREATE TABLE " + TABLE_NUMBERS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + MESSAGE_ID + " BIGINT, " + NUMBER + " VARCHAR, "  + STATUS + " INTEGER, " + TIME + " BIGINT " + ");";

    private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE " + TABLE_SCHEDULE + " ( " + ID + " BIGINT, " + MESSAGE + " VARCHAR, " + TIME + " BIGINT, " + TOTAL + " INTEGER " + ");";
    private static final String CREATE_TABLE_SCHEDULE_NUMBER = "CREATE TABLE " + TABLE_SCHEDULE_NUMBER + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + SCHEDULE_ID + " BIGINT, " + NUMBER + " VARCHAR, " + STATUS + " INTEGER, " + TIME + " BIGINT " + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_MESSAGES);
        sqLiteDatabase.execSQL(CREATE_TABLE_NUMBERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SCHEDULE);
        sqLiteDatabase.execSQL(CREATE_TABLE_SCHEDULE_NUMBER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String userTableDrop = "DROP TABLE IF EXISTS " + TABLE_MESSAGES;
        String notificationTableDrop = "DROP TABLE IF EXISTS " + TABLE_NUMBERS;
        String scheduleTableDrop = "DROP TABLE IF EXISTS " + TABLE_SCHEDULE;
        String scheduleNumberTableDrop = "DROP TABLE IF EXISTS " + TABLE_SCHEDULE_NUMBER;
        sqLiteDatabase.execSQL(userTableDrop);
        sqLiteDatabase.execSQL(notificationTableDrop);
        sqLiteDatabase.execSQL(scheduleTableDrop);
        sqLiteDatabase.execSQL(scheduleNumberTableDrop);
        onCreate(sqLiteDatabase);
    }

    public void addMessage(long messageId, String message, long time, int status, ArrayList<Contact> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, messageId);
        contentValues.put(MESSAGE, message);
        contentValues.put(TIME, time);
        db.insert(TABLE_MESSAGES, null, contentValues);
        db.close();
        addNumber(messageId, time, status, arrayList);
    }

    private void addNumber(long messageId, long time, int status, ArrayList<Contact> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Contact contact : arrayList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MESSAGE_ID, messageId);
            contentValues.put(NUMBER, contact.getNumber());
            contentValues.put(STATUS, status);
            contentValues.put(TIME, time);
            db.insert(TABLE_NUMBERS, null, contentValues);
        }
        db.close();
    }

    public void addSchedule(long scheduleId, String message, long time, ArrayList<Contact> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, scheduleId);
        contentValues.put(MESSAGE, message);
        contentValues.put(TIME, time);
        contentValues.put(TOTAL, arrayList.size());
        db.insert(TABLE_SCHEDULE, null, contentValues);
        db.close();
        addScheduleNumber(scheduleId, time, arrayList);
    }

    private void addScheduleNumber(long scheduleId, long time, ArrayList<Contact> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Contact contact : arrayList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SCHEDULE_ID, scheduleId);
            contentValues.put(NUMBER, contact.getNumber());
            contentValues.put(STATUS, 0);
            contentValues.put(TIME, time);
            db.insert(TABLE_SCHEDULE_NUMBER, null, contentValues);
        }
        db.close();
    }

    public ArrayList<Message> getAllMessage() {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_MESSAGES + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Message message = new Message(c.getLong(0), c.getString(1), c.getLong(2));
                messages.add(message);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return messages;
    }

    public ArrayList<Message> getAllSchedule() {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCHEDULE + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Message message = new Message(c.getLong(0), c.getString(1), c.getLong(2), c.getInt(3));
                messages.add(message);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return messages;
    }

    public ArrayList<Contact> getAllNumberUsingMessageID(long messageId, int status) {
        ArrayList<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NUMBERS + " WHERE " + MESSAGE_ID + "=" + messageId + " AND " + STATUS + "=" + status + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact(c.getInt(0), c.getLong(1), c.getString(2), c.getInt(3), c.getLong(4));
                contacts.add(contact);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return contacts;
    }

    public ArrayList<Contact> getAllScheduleNumberUsingScheduleID(long scheduleId) {
        ArrayList<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SCHEDULE_NUMBER + " WHERE " + MESSAGE_ID + "=" + scheduleId + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact(c.getInt(0), c.getLong(1), c.getString(2), c.getInt(3), c.getLong(4));
                contacts.add(contact);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return contacts;
    }

    public void setMessageUpdate(long messageId, String number, int status, long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        cv.put(TIME, time);
        db.update(TABLE_NUMBERS, cv, NUMBER + " = "+ NUMBER + " AND " + MESSAGE_ID + " = " + messageId , null);
        db.close();
    }

    public void deleteSingleSchedule(long scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULE, SCHEDULE_ID + "=" + scheduleId, null);
        db.close();
    }

}

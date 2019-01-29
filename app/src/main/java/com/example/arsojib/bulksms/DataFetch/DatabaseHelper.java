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

    private static final String ID = "id";
    private static final String MESSAGE_ID = "message_id";
    private static final String MESSAGE = "message";
    private static final String NUMBER = "number";
    private static final String STATUS = "status";
    private static final String TIME = "time";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + MESSAGE + " VARCHAR " + ");";
    private static final String CREATE_TABLE_NUMBERS = "CREATE TABLE " + TABLE_NUMBERS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + MESSAGE_ID + " INTEGER, " + NUMBER + " VARCHAR, "  + STATUS + " INTEGER, " + TIME + " BIGINT " + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_MESSAGES);
        sqLiteDatabase.execSQL(CREATE_TABLE_NUMBERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String userTableDrop = "DROP TABLE IF EXISTS " + TABLE_MESSAGES;
        String notificationTableDrop = "DROP TABLE IF EXISTS " + TABLE_NUMBERS;
        sqLiteDatabase.execSQL(userTableDrop);
        sqLiteDatabase.execSQL(notificationTableDrop);
        onCreate(sqLiteDatabase);
    }

    public void addMessage(String message, long time, int status, ArrayList<Contact> arrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase liteDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE, message);
        db.insert(TABLE_MESSAGES, null, contentValues);
        db.close();

//        String sql = "SELECT * FROM " + TABLE_MESSAGES + ";";
        String sql = "SELECT last_insert_rowid()" + ";";
        Cursor c = liteDatabase.rawQuery(sql, null);
        int id = 0;
        if (c.moveToFirst()) {
            do {
                id = c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();
        liteDatabase.close();
        addNumber(id, time, status, arrayList);
    }

    public void addNumber(int messageId, long time, int status, ArrayList<Contact> arrayList) {
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

    public ArrayList<Message> getAllMessage() {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_MESSAGES + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Message message = new Message(c.getInt(0), c.getString(1));
                messages.add(message);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return messages;
    }

    public ArrayList<Contact> getAllNumberUsingMessageID(int messageId) {
        ArrayList<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + MESSAGE_ID + "=" + messageId + ";";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3), c.getLong(4));
                contacts.add(contact);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return contacts;
    }

    public void setMessageUpdate(String number, int status, long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateSql = "UPDATE " + TABLE_NUMBERS + " SET " + STATUS + "=" + status + "," + TIME + "=" + time + " WHERE " + NUMBER + "=" + number + ";";
        db.execSQL(updateSql);
        db.close();
    }



}
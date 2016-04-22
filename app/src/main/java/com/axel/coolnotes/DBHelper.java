package com.axel.coolnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Александр on 22.04.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notesbase.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "NOTES";
    private static final String COL_NAME = "NAME";
    private static final String COL_DESC = "DESCRIPTION";
    private static final String COL_IMG = "IMAGE_RESOURCE_ID";


    private static DBHelper sInstance;

    public static DBHelper getInstance(Context context) {
        if (sInstance == null)
        {
            sInstance= new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    private DBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE NOTES (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, " +
                "DESCRIPTION TEXT, " +
                "IMAGE_RESOURCE_ID TEXT);");
        insertNote(db, "Тестим лист на анимацию ", "Тест вставки русского шрифта", "тратата");
        insertNote(db, "Тестовая запись 2 ", "Описание для заметки", "какой-то ресурс");
        insertNote(db, "3-я запись, тесты", "Работает :)", "ресурс");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public static void insertNote(SQLiteDatabase db, String name, String desc, String res_id)
    {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, name);
        cv.put(COL_DESC, desc);
        cv.put(COL_IMG, res_id);
        try
        {
            long rowID = db. insertOrThrow(TABLE_NAME, null, cv);
            Log.d(MainActivity.LOG_TAG, "we added in db " + rowID + " row");
        }
        catch (SQLiteException e)
        {
            Log.d(MainActivity.LOG_TAG, e.getMessage());
        }
    }

    public static void insertNote(Context context, String title, String desc, String resource)
    {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, title);
        cv.put(COL_DESC, desc);
        cv.put(COL_IMG, resource);
        try {
            long rowID = db. insertOrThrow(TABLE_NAME, null, cv);
            Log.d(MainActivity.LOG_TAG, "we added in db " + rowID + " row");
        }
        catch (SQLiteException e) {
            Log.d(MainActivity.LOG_TAG,e.getMessage());
        }
        db.close();
    }

    public Cursor getAllNotes(Context context)
    {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME,
                    new String[]{"_id", COL_NAME, COL_DESC, COL_IMG},
                    null,
                    null, null, null, null);
        }
        catch (SQLiteException e) {
            Log.d(MainActivity.LOG_TAG,e.getMessage());
        }
        db.close();
        return cursor;
    }

    public Cursor getNoteByID(Context context, int id)
    {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME,
                    new String[]{"_id", COL_NAME, COL_DESC, COL_IMG},
                    "_id = ?",
                    new String[] {String.valueOf(id)},
                    null, null, null);
        }
        catch (SQLiteException e) {
            Log.d(MainActivity.LOG_TAG,e.getMessage());
        }
        db.close();
        return cursor;
    }

    public void deleteNote(Context context, int id)
    {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete("NOTES", "_id=" +String.valueOf(id), null);
        }
        catch (SQLiteException e)
        {
            Log.d(MainActivity.LOG_TAG,e.getMessage());
        }
        db.close();
    }

}

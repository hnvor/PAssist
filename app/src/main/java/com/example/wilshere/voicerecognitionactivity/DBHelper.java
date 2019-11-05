package com.example.wilshere.voicerecognitionactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper{
    String DB_PATH = "";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    public static final String DATABASE_NAME = "VoicerDB";
    public static final String TABLE_FIRST = "first";
    public static final String TABLE_SECOND = "second";
    public static final String TABLE_CITIES = "cities";
    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_ATRIBUTES = "character";
    public static final String KEY_ID = "id";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_FIRSTID = "firstid";
    public static final String KEY_ANSWER = "answer";
    public static final String KEY_NEXT = "next";
    public static final String KEY_CITY = "city";
    public static final String KEY_USED = "used";
    public static final String KEY_PRIORITY = "priority";
    public static final String TASK_NAME = "name";
    public static final String TASK_STATUS = "status";
    public static final String TASK_START_TIME = "start_time";
    public static final String TASK_END_TIME = "end_time";
    public static final String TASK_TYPE = "type";
    public static final String TASK_ID = "id";
    public static final String ATRIBUTE_ID = "id";
    public static final String ATRIBUTE_NAME = "atribute";
    public static final String ATRIBUTE_VALUE = "value";
    public static final String ATRIBUTE_MULTIPLIER = "multiplier";
    public static final String ATRIBUTE_NEXT_WORSENING = "next_worsening";
    public static final String ATRIBUTE_LAST_WORSENING = "last_worsening";
    public static final String ATRIBUTE_WORSENING_FREQUENCY = "worsening_frequency";
    public static final String ATRIBUTE_IMAGE = "image";
    public static final String PHOTO_TABLE = "profile";
    public static final String PHOTO_NAME = "name";
    public static final String PHOTO_DATA = "data";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 12);
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.myContext = context;
    }
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return myDataBase.query("EMP_TABLE", null, null, null, null, null, null);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onOpen(SQLiteDatabase myDataBase) {
        super.onOpen(myDataBase);
        myDataBase.disableWriteAheadLogging();
    }

    public void addBitmap(String name,byte[] image) throws SQLException{


     try{   SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PHOTO_NAME,name);
        cv.put(PHOTO_DATA,image);
        database.update(PHOTO_TABLE, cv, "id = ?",
                new String[] { "1" });}catch (Exception e){}
    }
    public byte[] getBitmapByName(String name){
        byte[] result = null;
     try {
         SQLiteDatabase database = this.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] select = {PHOTO_DATA};

        qb.setTables(PHOTO_TABLE);
        Cursor c = qb.query(database,select,"name = ?",new String[]{name},null,null,null);
        if(c.moveToFirst()){
            do{
                result = c.getBlob(c.getColumnIndex(PHOTO_DATA));
            }while (c.moveToNext());
        }}catch (Exception e){}
        return result;
    }
}

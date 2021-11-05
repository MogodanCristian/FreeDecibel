package com.steelparrot.freedecibel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.steelparrot.freedecibel.model.YTItem;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    private static final String DATABASE_NAME="DownloadLater.db";
    private static final int DATABASE_VERSION=1;

    private static final String TABLE_NAME="DownloadList";
    private static final String COLUMN_ID="_id";
    private static final String COLUMN_TITLE="title";
    private static final String COLUMN_VIEWS="views";
    private static final String COLUMN_UPLOADER="uploader";
    private static final String COLUMN_TIME_UPLOAD="time_upload";
    private static final String COLUMN_DURATION="duration";
    private static final String COLUMN_URL="url";
    private static final String COLUMN_IMAGE="image";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_UPLOADER + " TEXT, " +
                        COLUMN_DURATION + " TEXT, " +
                        COLUMN_VIEWS + " INTEGER, " +
                        COLUMN_TIME_UPLOAD + " TEXT, " +
                        COLUMN_URL + " TEXT, " +
                        COLUMN_IMAGE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
    }

    public void insertYTItem(String title, String uploader, String duration, long views ,String time_upload, String url, String thumbnail){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_UPLOADER, uploader);
        cv.put(COLUMN_DURATION, duration);
        cv.put(COLUMN_VIEWS, views);
        cv.put(COLUMN_TIME_UPLOAD, time_upload);
        cv.put(COLUMN_URL,url);
        cv.put(COLUMN_IMAGE,thumbnail);
        long result=db.insert(TABLE_NAME,null,cv);
            if(result==-1){
                Toast.makeText(mContext,"Insert failure!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(mContext,"Insert successful!", Toast.LENGTH_SHORT).show();
            }
    }
    public Cursor selectAllData()
    {
        String query ="SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db= this.getReadableDatabase();

        Cursor cursor=null;
        if(db != null)
        {
            cursor=db.rawQuery(query,null);
        }
        return cursor;
    }
    public ArrayList<String> selectAllURLS()
    {
        String query ="SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db= this.getReadableDatabase();
        ArrayList<String> ret=new ArrayList<>();
        Cursor cursor= db.rawQuery(query,null);
        while(cursor.moveToNext())
        {
            ret.add(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
        }
        return ret;
    }

         public void deleteAll()
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME,null,null);
            db.close();
        }

        public void deleteItem(YTItem item)
        {
            SQLiteDatabase db = this.getWritableDatabase();
//            db.delete(TABLE_NAME,COLUMN_URL + " = ?",new String[]{item.getM_url()});
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_URL + "= '" + item.getM_url() + "'");
            db.close();
        }
}

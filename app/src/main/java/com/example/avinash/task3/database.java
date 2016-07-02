package com.example.avinash.task3;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by AVINASH on 6/29/2016.
 */
public class database extends SQLiteOpenHelper {
    public static final String dbname="CONTACT";
    public static final String pname="name";
    public static String pnum="phonenumber";
    public static String pimg="image";
    database(Context c) {
        super(c, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table cinfo" + "(id integer primary key,name text,phonenumber text,image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST cinfo");
        onCreate(db);
    }
    public boolean insertContcts(String name,String phonenumber,byte[] image){
        SQLiteDatabase db=this.getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("name",name); cv.put("phonenumber",phonenumber); cv.put("image", image);
        db.insert("cinfo", null, cv);
        return true;
    }
    public Cursor getinfo(int id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cr=db.rawQuery("select * from cinfo where id="+id+"",null);
        return cr;
    }
    public boolean updateContact(Integer id,String name,String phonenumber,byte[] image){
        SQLiteDatabase db=this.getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("name",name); cv.put("phonenumber",phonenumber); cv.put("image", image);
        db.update("cinfo",cv,"id=?",new String[]{Integer.toString(id)});
        return true;
    }
    public int row(){
        SQLiteDatabase db=this.getReadableDatabase();
        int rownum=(int) DatabaseUtils.queryNumEntries(db,"cinfo");
        return rownum;
    }
    public void drop(){
        SQLiteDatabase db=this.getReadableDatabase();
        db.execSQL("delete from cinfo");

        MainActivity ma=new MainActivity();
        ma.start=0;
        SharedPreferences.Editor editor = ma.sharedpreferences.edit();
        editor.putInt("count", ma.start);
        editor.putInt("length",ma.length);
        editor.apply();
    }
}

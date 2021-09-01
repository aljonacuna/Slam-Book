package com.example.slambookproject.DBhandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.slambookproject.Model.Entrymodel;

import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper {
    public static final String slambook = "slambook.db";
    public static final String tableEnt = "entry";
    private SQLiteDatabase myDB;
    private Cursor cursor;
    private Bitmap imgBitmap;
    private String pass;
    public DBHelper(@Nullable Context context) {
        super(context,slambook,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create table auth(password TEXT primary key,secretquestion TEXT,answer TEXT)");
        DB.execSQL("create table entry(entryid INTEGER primary key AUTOINCREMENT,fname TEXT,lname TEXT," +
                "image BLOB,answer1 TEXT,answer2 TEXT,answer3 TEXT,answer4 TEXT,answer5 TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop table if exists auth");
        DB.execSQL("drop table if exists entry");
    }

    //signup
    public boolean inserData(String pass,String secretquestion,String answer){
        myDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password",pass);
        values.put("secretquestion",secretquestion);
        values.put("answer",answer);
        long result = myDB.insert("auth",null,values);
        if (result == -1)
            return false;
        else
            return true;
    }
    public boolean checkifexist(String pass){
        myDB = this.getWritableDatabase();
        cursor = myDB.rawQuery("Select * from auth where password = ?",new String[]{pass});
        if (cursor.getCount()>0){
            return true;
        }else {
            return false;
        }
    }
    public boolean loginCheck(String pass){
        myDB = this.getWritableDatabase();
        cursor = myDB.rawQuery("Select * from auth where password = ?",new String[]{pass});
        if (cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }//end signin signup

    //Entry
    public boolean insertEntry(Entrymodel entries){
        myDB = this.getWritableDatabase();
        Bitmap bitmap = entries.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte []bytes = baos.toByteArray();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fname",entries.getFname());
        contentValues.put("lname",entries.getLname());
        contentValues.put("image",bytes);
        contentValues.put("answer1",entries.getAnswer1());
        contentValues.put("answer2",entries.getAnswer2());
        contentValues.put("answer3",entries.getAnswer3());
        contentValues.put("answer4",entries.getAnswer4());
        contentValues.put("answer5",entries.getAnswer5());
        long result = myDB.insert(tableEnt,null,contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }
    public Cursor getEntry(){
        myDB = this.getReadableDatabase();
        cursor = myDB.rawQuery("Select * from entry",null);
        return cursor;

    }
    public boolean checkIfEmpty(){
        myDB = this.getWritableDatabase();
        cursor = myDB.rawQuery("Select * from entry",null);
        if (cursor.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public Bitmap getImg(int id){
        myDB = this.getReadableDatabase();
        cursor = myDB.rawQuery("Select image from entry where entryid="+id+"",null);
        while (cursor.moveToNext()) {
            byte[] imgByte = cursor.getBlob(cursor.getColumnIndex("image"));
            imgBitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
        }
        return imgBitmap;
    }
    public void editEntry(Entrymodel editEntry){
        myDB = this.getWritableDatabase();
        Bitmap bitmap = editEntry.getImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte [] imgByte = baos.toByteArray();
        ContentValues values = new ContentValues();
        values.put("entryid",editEntry.getId());
        values.put("fname",editEntry.getFname());
        values.put("lname",editEntry.getLname());
        values.put("image",imgByte);
        values.put("answer1",editEntry.getAnswer1());
        values.put("answer2",editEntry.getAnswer2());
        values.put("answer3",editEntry.getAnswer3());
        values.put("answer4",editEntry.getAnswer4());
        values.put("answer5",editEntry.getAnswer5());
        myDB.update(tableEnt,values,"entryid="+editEntry.getId()+"",null);
    }
    public void deleteEntry(int id,Context context){
        myDB = this.getWritableDatabase();
        myDB.delete(tableEnt,"entryid="+id+"",null);
        Toast.makeText(context, "Successfully deleted", Toast.LENGTH_SHORT).show();
    }

    public Cursor sortEntry(String sortby){
        if (sortby.equals("Ascending")) {
            myDB = this.getReadableDatabase();
            cursor = myDB.rawQuery("Select * from entry Order by lname Asc", null);
        }else{
            myDB = this.getReadableDatabase();
            cursor = myDB.rawQuery("Select * from entry Order by lname Desc", null);
        }
        return cursor;
    }//end emtry

    //forgotpass
    public String forgotPass(String quest,String ans,Context context) {
        myDB = this.getWritableDatabase();
        cursor = myDB.rawQuery("Select password from auth where secretquestion=? and answer=?",new String[]{quest,ans});
        if (cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                pass = cursor.getString(cursor.getColumnIndex("password"));
            }
        }else{
            Toast.makeText(context, "Wrong questions or answer", Toast.LENGTH_SHORT).show();
            pass = "";
        }
        return pass;
    }

    //changepass
    public void changePass(String newpass, String oldpass){
        myDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password",newpass);
        myDB.update("auth",values,null,null);
    }
    public void changeSq(String pass,String secretquest,String answer){
        myDB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("secretquestion",secretquest);
        values.put("answer",answer);
        myDB.update("auth",values,null,null);
    }
}

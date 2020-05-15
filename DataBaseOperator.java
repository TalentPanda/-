package com.demo.demos.FindU.SearchByWiFi.core.SQLite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.demo.demos.FindU.SearchByWiFi.core.Application.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class DataBaseOperator {

    private DataBaseOpenHelper dbOpenhelper;
    private SQLiteDatabase db;
    private static DataBaseOperator mOperator = null;

    private DataBaseOperator(){
        if (MyApplication.getContext() == null){
            Log.d("MyApplication","为空");
        }else{
            dbOpenhelper = new DataBaseOpenHelper(MyApplication.getContext(),"macData",null,1);
            db = dbOpenhelper.getWritableDatabase();
        }
    }

    public static synchronized DataBaseOperator getInstance(){
        if (mOperator == null){
            mOperator = new DataBaseOperator();
        }
        return mOperator;
    }



    public void add(DataBaseItem dataBaseItem){
        db.execSQL("insert into macData values(?,?)",
                new Object[]{dataBaseItem.getmMac(),dataBaseItem.getmFirm()});
    }

    public void delete(String mMac){
        db.execSQL("delete from macData where mac=?", new String[]{mMac});
    }

    public void update(DataBaseItem dataBaseItem){
        db.execSQL("update macData set mac = ?,firm = ?",
                new Object[]{dataBaseItem.getmMac(),dataBaseItem.getmFirm()});
    }
    //
    public DataBaseItem selectFirmByMac(String mMac){

        DataBaseItem dataBaseItem = new DataBaseItem();
        //Cursor 是所有查询结果的合集
        Cursor cursor = db.rawQuery("select * from macData where mac = ?", new String[]{mMac});
        while (cursor.moveToNext()){
            //因为通过MAC码查询出来的结果只有一行
            dataBaseItem.setmMac(cursor.getString(0));
            dataBaseItem.setmFirm(cursor.getString(1));
        }
        cursor.close();
        return  dataBaseItem;
    }

    public List<DataBaseItem> selectAll(){

        ArrayList<DataBaseItem> dataBaseItems = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from macData",null);
        while (cursor.moveToNext()){
            DataBaseItem dataBaseItem = new DataBaseItem();
            dataBaseItem.setmMac(cursor.getString(0));
            dataBaseItem.setmFirm(cursor.getString(1));
            dataBaseItems.add(dataBaseItem);
        }
        cursor.close();
        return dataBaseItems;
    }


}

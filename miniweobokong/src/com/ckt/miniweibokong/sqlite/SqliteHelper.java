package com.ckt.miniweibokong.sqlite;

import com.sina.weibo.sdk.openapi.models.User;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper{
	public static final String TB_NAME = "users";
	public static final String ID="_id";
	public static final String USERID="userId";
	public static final String FOLLOWERS_COUNT="followers_count";
	public static final String FRIENDS_COUNT="friends_count";
	public static final String STATUSES_COUNT="statuses_count";
	
	public static final String USERNAME="userName";
	public static final String USERICON="userIcon";
	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	//创建表
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL("CREATE TABLE IF NOT EXISTS "+
	                TB_NAME+"("+
	                ID+" integer primary key,"+
	                USERID+" varchar,"+
	                FOLLOWERS_COUNT+" integer,"+
	                FRIENDS_COUNT+" integer,"+
	                STATUSES_COUNT+" integer,"+
	                USERNAME+" varchar,"+
	                USERICON+" blob"+
	                ")"
	                );
	        Log.e("Database","onCreate");
	}
	//更新表
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
        Log.e("Database","onUpgrade");
	}
	//更新列
	public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
        try{
            db.execSQL("ALTER TABLE " +
                    TB_NAME + " CHANGE " +
                    oldColumn + " "+ newColumn +
                    " " + typeColumn
            );
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

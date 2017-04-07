package com.liu.AssetsScan.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1211;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name, int version){  
		this(context,name,null,version);  
	}  

	public DatabaseHelper(Context context, String name){  
		this(context,name,VERSION);  
	}  

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "create table if not exists MessageTable (" +
				"zzbh TEXT," +		//id
				"zzlb TEXT,"+   	//资产类别
				"zzflag TEXT,"+   	//已读未读
				"message TEXT,"+	//消息
				"user TEXT,"+		//使用人
				"PRIMARY KEY(zzbh,zzlb,user)"+
				");"; 
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}

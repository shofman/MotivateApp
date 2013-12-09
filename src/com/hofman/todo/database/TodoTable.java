package com.hofman.todo.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {
	
	public static final String TABLE_TODO = "todo";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_USERID = "userid";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_SUMMARY = "summary";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DAILY = "daily";
	public static final String COLUMN_CREATIONDAY = "creationday";
	public static final String COLUMN_CREATIONMONTH = "creationmonth";
	public static final String COLUMN_CREATIONYEAR = "creationyear";
	public static final String COLUMN_DUEDATE = "duedate";
	public static final String COLUMN_REMINDER = "reminder";
	public static final String COLUMN_TAGS = "tags";
	public static final String COLUMN_PRIORITY = "priority";
	public static final String COLUMN_TIME = "time";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TODO
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_CATEGORY + " text not null, "
			+ COLUMN_SUMMARY + " text not null, "
			+ COLUMN_DESCRIPTION + " text not null, "
			+ COLUMN_DAILY + " integer not null, "
			+ COLUMN_CREATIONDAY + " text not null, "
			+ COLUMN_CREATIONMONTH + " text not null, "
			+ COLUMN_CREATIONYEAR + " text not null, "
			+ COLUMN_DUEDATE + " text, " 
			+ COLUMN_REMINDER + " integer, "
			+ COLUMN_TAGS + " text not null, "
			+ COLUMN_PRIORITY + " text not null, " 
			+ COLUMN_TIME + " text not null"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(TodoTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
		onCreate(database);
	}

}

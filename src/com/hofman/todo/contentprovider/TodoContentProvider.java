package com.hofman.todo.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.hofman.todo.database.TodoDatabaseHelper;
import com.hofman.todo.database.TodoTable;

public class TodoContentProvider extends ContentProvider {

	private TodoDatabaseHelper database;
	
	private static final int TODOS = 10;
	private static final int TODO_ID = 20;
	
	private static final String AUTHORITY = "com.hofman.todo.contentprovider";
	private static final String BASE_PATH = "todos";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/todos";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/todo";
	
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(AUTHORITY, BASE_PATH, TODOS);
		sUriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TODO_ID);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		
		switch(uriType) {
		case TODOS:
			rowsDeleted = sqlDB.delete(TodoTable.TABLE_TODO, selection, selectionArgs);
			break;
		case TODO_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(TodoTable.TABLE_TODO, TodoTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(TodoTable.TABLE_TODO, TodoTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}
	@Override
	public String getType(Uri uri) {
		return null;
	}
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch(uriType) {
		case TODOS:
			id = sqlDB.insert(TodoTable.TABLE_TODO, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return Uri.parse(BASE_PATH + "/" + id);
	}
	@Override
	public boolean onCreate() {
		database = new TodoDatabaseHelper(getContext());
		return false;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection);
		
		queryBuilder.setTables(TodoTable.TABLE_TODO);
		
		int uriType = sUriMatcher.match(uri);
		switch (uriType) {
		case TODOS:
			break;
		case TODO_ID:
			queryBuilder.appendWhere(TodoTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown uri" + uri);
		}
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}
	
	private void checkColumns(String[] projection) {
		String[] available = {TodoTable.COLUMN_LABEL, TodoTable.COLUMN_SUMMARY, 
							  TodoTable.COLUMN_DESCRIPTION, TodoTable.COLUMN_DAILY, 
							  TodoTable.COLUMN_CREATIONDAY, TodoTable.COLUMN_CREATIONMONTH,
							  TodoTable.COLUMN_CREATIONYEAR, TodoTable.COLUMN_DUEDAY,
							  TodoTable.COLUMN_DUEMONTH, TodoTable.COLUMN_DUEYEAR,
							  TodoTable.COLUMN_TAGS, TodoTable.COLUMN_PRIORITY,
							  TodoTable.COLUMN_TIME, TodoTable.COLUMN_REMINDER,
							  TodoTable.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sUriMatcher.match(uri);
		int rowsUpdated = 0;
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		switch (uriType) {
		case TODOS:
			rowsUpdated = sqlDB.update(TodoTable.TABLE_TODO, values, selection, selectionArgs);
			break;
		case TODO_ID:
			String id = uri.getLastPathSegment();
			if(TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(TodoTable.TABLE_TODO, values, TodoTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(TodoTable.TABLE_TODO, values, TodoTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
	
}

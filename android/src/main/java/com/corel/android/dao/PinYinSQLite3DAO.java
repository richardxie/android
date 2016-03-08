package com.corel.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.corel.android.pinyin.PinYin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PinYinSQLite3DAO implements IPinYinDAO{

	private static final String TAG = "PinYinDB";
	private static final String DATABASE_NAME = "PinYin.db";
	private static final int DATABASE_VERSION = 2;
	private static final String PINYIN_TABLE_NAME = "pinyin";

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + PINYIN_TABLE_NAME + " ("
					+ PinYin.PinYinColumns._ID + " INTEGER PRIMARY KEY,"
					+ PinYin.PinYinColumns.CARD + " INTEGER,"
					+ PinYin.PinYinColumns.CHINESE + " TEXT," 
					+ PinYin.PinYinColumns.PINYIN + " TEXT," 
					+ PinYin.PinYinColumns.ENGLISH + " TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	@Inject
	public PinYinSQLite3DAO(Context context) {
		this.mContext = context;
		open();
	}

	//打开数据库

	public void open() {
		Log.i(TAG, "open");
		mOpenHelper = new DatabaseHelper(mContext);
	}

	// Close the database
	public void close() {
		if (mOpenHelper != null)
			mOpenHelper.close();
	}
	
	public void drop() {
		mOpenHelper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + PINYIN_TABLE_NAME);
	}

	public void insert(ContentValues values) {
		if (values == null) {
			values = new ContentValues();
			values.put(PinYin.PinYinColumns.CARD, 1);
			values.put(PinYin.PinYinColumns.CHINESE, "好");
			values.put(PinYin.PinYinColumns.PINYIN, "hao");
			values.put(PinYin.PinYinColumns.ENGLISH, "Good");

		}
		mOpenHelper.getWritableDatabase().insert(PINYIN_TABLE_NAME,
				PinYin.PinYinColumns.CHINESE, values);
		Log.i(TAG, "insert");
	}

	public void delete(String where, String[] whereArgs) {
		mOpenHelper.getWritableDatabase().delete(PINYIN_TABLE_NAME, where,
				whereArgs);
		Log.i(TAG, "delete");
	}

	public int update(ContentValues values, String where, String[] whereArgs) {
		if (values == null) {
			values = new ContentValues();
			values.put(PinYin.PinYinColumns.CARD, 1);
			values.put(PinYin.PinYinColumns.CHINESE, "坏");
			values.put(PinYin.PinYinColumns.PINYIN, "huai");
			values.put(PinYin.PinYinColumns.ENGLISH, "Bad");

		}
		return mOpenHelper.getWritableDatabase().update(PINYIN_TABLE_NAME, values, where, whereArgs);
	}

	public Cursor query(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// 参数1：表名
		// 参数2：要想显示的列
		// 参数3：where子句
		// 参数4：where子句对应的条件值
		// 参数5：分组方式
		// 参数6：having条件
		// 参数7：排序方式
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(PINYIN_TABLE_NAME);
		qb.setProjectionMap(sNotesProjectionMap);
		Cursor c = qb.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, null);
		return c;
	}
	
	@Override
	public PinYin get(int id) {
		PinYin pinyin = null;
		Cursor c = query(null, PinYin.PinYinColumns._ID + "=?", new String[]{"" + id}, null);
		if(c != null) {
			c.moveToFirst();
			String chinese = c.getString(c.getColumnIndex(PinYin.PinYinColumns.CHINESE));
			String english = c.getString(c.getColumnIndex(PinYin.PinYinColumns.ENGLISH));
			String py = c.getString(c.getColumnIndex(PinYin.PinYinColumns.PINYIN));
			c.close();
			pinyin= new PinYin(chinese, py, english);
		}
		return pinyin;
	}
	
	@Override
	public PinYin get(final String c) {
		PinYin pinyin = null;
		Cursor cursor = query(null, PinYin.PinYinColumns.CHINESE + "=?", new String[]{c}, null);
		if(cursor != null) {
			if(cursor.moveToNext()) {
				String chinese = cursor.getString(cursor.getColumnIndex(PinYin.PinYinColumns.CHINESE));
				String english = cursor.getString(cursor.getColumnIndex(PinYin.PinYinColumns.ENGLISH));
				String py = cursor.getString(cursor.getColumnIndex(PinYin.PinYinColumns.PINYIN));
				pinyin = new PinYin(chinese, py, english);
			}
			cursor.close();
		}
		return pinyin;
	}

	@Override
	public List<PinYin> getAll(int cardId) {
		List<PinYin> pys = new ArrayList<PinYin>();
		Cursor cursor = query(null, PinYin.PinYinColumns.CARD + "=?", new String[]{"" + cardId}, null);
		if(cursor != null) {
			while(cursor.moveToNext()) {
				String chinese = cursor.getString(cursor.getColumnIndex(PinYin.PinYinColumns.CHINESE));
				String english = cursor.getString(cursor.getColumnIndex(PinYin.PinYinColumns.ENGLISH));
				String py = cursor.getString(cursor.getColumnIndex(PinYin.PinYinColumns.PINYIN));
				pys.add(new PinYin(chinese, english, py));
			}
			cursor.close();
		}
		return pys;
	}

	@Override
	public void add(int card, PinYin py) {
		ContentValues values = new ContentValues();
		values.put(PinYin.PinYinColumns.CHINESE, py.getChinese());
		values.put(PinYin.PinYinColumns.PINYIN, py.getPinyin());
		values.put(PinYin.PinYinColumns.ENGLISH, py.getEnglish());
		insert(values);
	}

	@Override
	public void addAll(int card, Set<PinYin> py) {
		for(PinYin p : py)
			add(card, p);
	}
	
	private static HashMap<String, String> sNotesProjectionMap;
	static {
		sNotesProjectionMap = new HashMap<String, String>();
		sNotesProjectionMap.put(PinYin.PinYinColumns.CHINESE, PinYin.PinYinColumns.CHINESE);
		sNotesProjectionMap.put(PinYin.PinYinColumns.PINYIN, PinYin.PinYinColumns.PINYIN);
		sNotesProjectionMap.put(PinYin.PinYinColumns.ENGLISH, PinYin.PinYinColumns.ENGLISH);
	}
	
	private DatabaseHelper mOpenHelper;

	@Inject Context mContext;
}

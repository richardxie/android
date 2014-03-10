package com.corel.android.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.corel.android.dao.PinYinSQLite3DAO;
import com.corel.android.pinyin.PinYin;

import java.util.List;

public class PinYinDBTestCase extends AndroidTestCase {
	PinYinSQLite3DAO dbs;

	@Override
	protected void setUp() throws Exception {
		dbs = new PinYinSQLite3DAO(getContext());
	}

	@Override
	protected void tearDown() throws Exception {
		dbs.close();
	}

	public void testInsert() {
		dbs.open();
		
		dbs.insert(null);
		Cursor c = dbs.query(new String[]{PinYin.PinYinColumns.CHINESE, PinYin.PinYinColumns.PINYIN}, null, null, null);
		assertEquals(c.getCount(), 1);
		
		c.moveToFirst();
		String chinese = c.getString(c.getColumnIndex(PinYin.PinYinColumns.CHINESE));
		assertEquals(chinese, "好");
	}
	
	public void testUpdate() {
		dbs.open();
		
		dbs.insert(null);
		ContentValues values = new ContentValues();
		values.put(PinYin.PinYinColumns.CHINESE, "坏");
		values.put(PinYin.PinYinColumns.PINYIN, "huai");
		values.put(PinYin.PinYinColumns.ENGLISH, "Bad");
		dbs.update(null, PinYin.PinYinColumns.CHINESE + " = ?", new String[]{"好"});
	}
	
	public void testDelete() {
		dbs.open();
		
		dbs.insert(null);
		dbs.delete(PinYin.PinYinColumns.CHINESE + " = ?", new String[]{"好"});
	}
	
	public void testGet() {
		dbs.open();
		PinYin py = dbs.get("坏");
		System.out.println(py);
	}
	
	public void testGetAll() {
		dbs.open();
		List<PinYin> py = dbs.getAll(1);
		for(PinYin p : py)
			System.out.println(p);
	}
}

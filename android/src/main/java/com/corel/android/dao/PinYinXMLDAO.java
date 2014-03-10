package com.corel.android.dao;

import android.util.Log;

import com.corel.android.pinyin.PinYin;

import java.util.List;
import java.util.Set;

public class PinYinXMLDAO implements IPinYinDAO {

	private static final String TAG = "PinYinXMLDAO";
	@Override
	public PinYin get(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PinYin get(String chinese) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PinYin> getAll(int cardId) {
		Log.i(TAG, "get All Pinyin for card:" + cardId);
		return null;
	}

	@Override
	public void add(int card, PinYin py) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAll(int card, Set<PinYin> py) {
		// TODO Auto-generated method stub

	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}

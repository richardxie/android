package com.corel.android.gesture;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class PinYinGestureService implements IPinYinGestureService {

	private static final String TAG = "PinYinGestureService";
	@Override
	public void load(int card) {
		Log.i(TAG, "gesture loading");
		if(card == mCurrentCard) {
			Log.i(TAG, "gesture loaded, gesture number:" + mGestureStore.getGestureEntries().size());
			return;
		}
		
		mCurrentCard = card;
		mStoreFile = new File(Environment.getExternalStorageDirectory(), "PinYin/gestures/gesture" + card);
		
		if(mGestureStore != null)
			mGestureStore.save();
		
		if(!mStoreFile.exists()) {
			try {
				mStoreFile.createNewFile();
			} catch (IOException e) {
				;
			}
		}
		
		mGestureStore = GestureLibraries.fromFile(mStoreFile);
		mGestureStore.load();
		Log.i(TAG, "gesture loaded, gesture number:" + mGestureStore.getGestureEntries().size());
	}

	public final boolean preLoadCheck(int card) {
		File path = new File(Environment.getExternalStorageDirectory(), "PinYin/gestures/gesture" + card);
		return path.exists();
	}
	
	@Override
	public final void release() {
		Log.i(TAG, "gesture release");
	}
	
	@Override
	public final boolean save() {
		Log.i(TAG, "gesture saving");
		return mGestureStore.save();
	}
	
	@Override
	public final int getCurrentCard() {
		return mCurrentCard;
	}

	@Override
	public final ArrayList<Gesture> getGestures(final String name) {
		return mGestureStore.getGestures(name);
	}
	
	@Override
	public final Set<String> getGestureEntries() {
		return mGestureStore.getGestureEntries();
	}
	
	@Override
	public final void removeGesture(final String entryName) {
		mGestureStore.removeEntry(entryName);
	}

	@Override
	public final void removeGesture(String entryName, Gesture gesture) {
		mGestureStore.removeGesture(entryName, gesture);
	}

	@Override
	public final void addGesture(String entryName, Gesture gesture) {
		mGestureStore.addGesture(entryName, gesture);
	}
	
	@Override
	public String getCurrentGestureFile() {
		return mStoreFile.getAbsolutePath();
	}

	private GestureLibrary mGestureStore;
	private File mStoreFile;
	private int mCurrentCard;
}

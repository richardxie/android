package com.corel.android.gesture;

import android.gesture.Gesture;

import com.corel.android.pinyin.IPinYinService;

import java.util.List;
import java.util.Set;

public interface IPinYinGestureService extends IPinYinService{
	List<Gesture> getGestures(final String name);
	
	Set<String> getGestureEntries();
	
	void removeGesture(final String entryName);

	void removeGesture(final String entryName, final Gesture gesture);

	void addGesture(final String entryName, final Gesture gesture);
	
	String getCurrentGestureFile();
}

package com.corel.android.audio;

import java.util.EventObject;

public class PinYinAudioLoaderEvent extends EventObject {

	private static final long serialVersionUID = -1414311049162226310L;

	private int mProgress;
	private int mMax;
	public PinYinAudioLoaderEvent(Object source, int progress, int max) {
		super(source);
		this.mProgress = progress;
		this.mMax = max;
	}

	public final int getProgress() {
		return mProgress;
	}
	
	public final int getMax() {
		return mMax;
	}
}

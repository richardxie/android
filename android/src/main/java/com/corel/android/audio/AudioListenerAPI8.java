package com.corel.android.audio;

import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;

//import com.corel.android.paint.FingerPaintActivity;

public class AudioListenerAPI8 implements PinYinAudioLoaderListener, OnLoadCompleteListener {

	private Handler mHandler;
	private int mMax;
	private int mProgress;
	
	public AudioListenerAPI8() {
	}
	
	public AudioListenerAPI8(Handler handler) {
		setMessageHandler(handler);
	}

	public void setMessageHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public void onLoadComplete(SoundPool pool, int sampleId,
			int status) {
		mProgress ++;
		//Message msg = mHandler.obtainMessage(FingerPaintActivity.PROGRESS_MSG);
		Bundle b = new Bundle();
		//b.putInt(FingerPaintActivity.PROGRESS_VAL, mProgress * 100 / mMax);
		//msg.setData(b);
		//mHandler.sendMessage(msg);
		if(mProgress == mMax)
			pool.setOnLoadCompleteListener(null);
	}

	public void load(PinYinAudioLoaderEvent event) {
		SoundPool pool = (SoundPool) event.getSource();
		mMax = event.getMax();
		pool.setOnLoadCompleteListener(this);
	}

	public void loadCompleted() {
		/*Message msg = mHandler.obtainMessage(FingerPaintActivity.PROGRESS_MSG);
		Bundle b = new Bundle();
		b.putInt(FingerPaintActivity.PROGRESS_VAL, 100);
		msg.setData(b);
		mHandler.sendMessage(msg);*/
	}
}

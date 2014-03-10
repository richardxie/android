package com.corel.android.audio;

import android.os.Handler;

//import com.corel.android.paint.FingerPaintActivity;

public class AudioListenerAPI7 implements PinYinAudioLoaderListener {

	private Handler mHandler;

	public AudioListenerAPI7() {
		
	}
	
	public AudioListenerAPI7(Handler handler) {
		setMessageHandler(handler);
	}
	
	public void setMessageHandler(Handler handler) {
		this.mHandler = handler;
	}

	public void load(PinYinAudioLoaderEvent event) {
		/*int progress = event.getProgress();
		int max = event.getMax();
		Message msg = mHandler.obtainMessage(FingerPaintActivity.PROGRESS_MSG);
		Bundle b = new Bundle();
		b.putInt(FingerPaintActivity.PROGRESS_VAL, progress * 100 / max);
		msg.setData(b);
		mHandler.sendMessage(msg);*/
	}

	public void loadCompleted() {
		/*Message msg = mHandler.obtainMessage(FingerPaintActivity.PROGRESS_MSG);
		Bundle b = new Bundle();
		b.putInt(FingerPaintActivity.PROGRESS_VAL, 100);
		msg.setData(b);
		mHandler.sendMessage(msg);*/
	}
}

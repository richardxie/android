package com.corel.android.audio;

import android.os.Build;
import android.os.Handler;

public class AudioCompletedListenerFactory {

	public static final PinYinAudioLoaderListener createAudioListener(Handler handler) {
		switch(Build.VERSION.SDK_INT) {
		case Build.VERSION_CODES.ECLAIR_MR1:
			return new AudioListenerAPI7(handler);
		case Build.VERSION_CODES.FROYO: 
			return new AudioListenerAPI8(handler);
		}
		return new AudioListenerAPI8(handler);
	}
}

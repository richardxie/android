package com.corel.android.audio;

import java.util.EventListener;

public interface PinYinAudioLoaderListener extends EventListener{
	void loadCompleted();
	void load(PinYinAudioLoaderEvent event);
}

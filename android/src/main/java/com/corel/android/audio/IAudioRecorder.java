package com.corel.android.audio;


public interface IAudioRecorder {
	/**
	 * INITIALIZING : recorder is initializing;
	 * READY : recorder has been initialized, recorder not yet started
	 * RECORDING : recording
	 * ERROR : reconstruction needed
	 * STOPPED: reset needed
	 */
	public enum State {INITIALIZING, READY, RECORDING, ERROR, STOPPED};
	
	// The interval in which the recorded samples are output to the file
	final int TIMER_INTERVAL = 120;
	
	public State getState();
	
	void configue(int audioSource, int sampleRate, int channelConfig,
			int audioFormat);
	
	void prepare();
	
	void start();
	
	void stop();
	
	void release();
	
	void reset();
	
	void setOutputFile(String path);
}

package com.corel.android.audio;

public interface IAudioRecognizeService {
	/**
	 * Audio recognize API
	 * @param filePath, a FLAC audio file
	 * @return audio recognized speech
	 */
	String getAudioRecognize(String filepath);
	
	/**
	 * convert a wav file to flac file
	 * @param in a wav PCM file
	 * @param out a flac encode file
	 * 
	 */
	void convertAudioFormat(String in, String out);
}

package com.corel.android.audio;

import com.corel.android.pinyin.IPinYinService;

import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Audio Service for Pinyin
 * 
 * @author richard
 * 
 */
public interface IPinYinAudioService extends IPinYinService {

	int SOUND_NUM_IN_ONECARD = 42;

	/**
	 * select audio wanted to play
	 * 
	 * @param id
	 *            audio id
	 */
	void select(int id) throws ExecutionException, InterruptedException;

	void select(String name) throws ExecutionException, InterruptedException;

	/**
	 * play audio
	 */
	void play() throws ExecutionException, InterruptedException;

	/**
	 * stop audio
	 */
	void stop();

	/**
	 * unload audio
	 */
	void unload(int id);

	/**
	 * get list for PinYin in current audio service
	 * 
	 * @return
	 */
	Set<String> getCurrentPinYin();
	
	/**
	 * get stored Audio path
	 * 
	 * @return audio path
	 */
	String getAudioPath();
	

	void addListener(PinYinAudioLoaderListener listener);

	void removeListener(PinYinAudioLoaderListener listener);
}

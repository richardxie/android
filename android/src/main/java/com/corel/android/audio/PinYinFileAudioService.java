package com.corel.android.audio;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.corel.android.http.HttpClientStratgy;
import com.corel.android.pinyin.PinYin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.FutureTask;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

//import com.corel.android.javaFlacEncoder.FLAC_FileEncoder;
//import com.corel.android.javaFlacEncoder.FLAC_FileEncoder.Status;

@Singleton
public class PinYinFileAudioService implements IPinYinAudioService, IAudioRecognizeService {

	private static final String TAG = "PinYinFileAudioService";
	
	/**
	 * a async audio loader
	 * @author richard
	 *
	 */
	private class Loader implements Callable<SoundPool> {
		public SoundPool call() throws Exception {
			File folder = new File(mSoundFolder, "card" + mCardNumber);
			String[] files = null;
			if (!folder.exists()) {
				if (!folder.mkdirs()) {
					Log.e(TAG, "make dir failed:" + folder);
				}
			}
			files = folder.list(new FilenameFilter() {

				public boolean accept(File dir, String filename) {
					int index = filename.indexOf(PinYin.AUDIO_PR);
					return index != -1;
				}
			});
			int max = files.length;
			int current = 0;
			for (String f : files) {
				current++;
				load(new PinYinAudioLoaderEvent(mSoundPool, current, max));
				final String path = new File(folder, f).getAbsolutePath();
				int soundID = mSoundPool.load(path, 1);
				mSoundMap.put(f, soundID);
			}
			loadCompleted();
			return mSoundPool;
		}

	}
	
	@Override
	public String getAudioRecognize(String filepath) {
		return mHttpClient.getAudioRecognize(filepath);
	}
	
	public void convertAudioFormat(String in, String out) {
		/*File input = new File(Environment.getExternalStorageDirectory(), in);
		File output = new File(Environment.getExternalStorageDirectory(), out);
		FLAC_FileEncoder encoder = new FLAC_FileEncoder();
		Status status = encoder.encode(input, output);
		Log.i(TAG, "FLAC encode status:" + status + "samples:" + encoder.getLastTotalSamplesEncoded());
	*/}
	
	@Override
	public void load(int card) {
		if(card == mCardNumber)
			return;
		
		unloadAll();
		
		Log.i(TAG, "load audio for card:" + card);
		this.mCardNumber = card;
		mTask = new FutureTask<SoundPool>(new Loader());
		new Thread(mTask).start();
	}
	
	private void unloadAll() {
		Collection<Integer> sounds = mSoundMap.values();
		for(int soundID : sounds) {
			mSoundPool.unload(soundID);
		}
		sounds.clear();
	}

	@Override 
	public boolean preLoadCheck(int card){
		File folder = new File(mSoundFolder, "card" + mCardNumber);
		return folder.exists();
	}
	
	@Override
	public boolean save() {return false;}

	@Override
	public void select(int id) throws AudioSelectedFailedException{
		Log.i(TAG, "select audio id:" + id);
		if(!mSoundMap.containsValue(Integer.valueOf(id)))
			throw new AudioSelectedFailedException("No such audio for id:" + id);
		mCurrentSoundID = id;
	}
	
	@Override
	public void select(String name) throws AudioSelectedFailedException{
		Log.i(TAG, "select audio name:" + name);
		String fileName = name + PinYin.AUDIO_PR;
		if(!mSoundMap.containsKey(fileName))
			throw new AudioSelectedFailedException("No such audio for name:" + name);
		mCurrentSoundID = mSoundMap.get(fileName);
	}

	@Override
	public void play() {
		Log.i(TAG, "Play a audio");
		if(mSoundPool.play(mCurrentSoundID, 1, 1, 1, 0, 1) == 0)
			Log.e(TAG, "failed to play audio, id = " + mCurrentSoundID);
	}

	@Override
	public void stop() {
		Log.i(TAG, "stop a audio");
		mSoundPool.stop(mCurrentSoundID);
	}

	@Override
	public void unload(int id) {
		if(!mSoundPool.unload(id))
			Log.e(TAG, "failed t onload audio, id = " + id);
	}
	
	@Override
	public Set<String> getCurrentPinYin() {
		Log.i(TAG, "get current pinyin list");
		return mSoundMap.keySet();
	}
	
	@Override
	public int getCurrentCard() {
		return mCardNumber;
	}
	
	@Override
	public String getAudioPath() {
		return mSoundFolder.getAbsolutePath();
	}

	@Override
	public void release() {
		Log.i(TAG, "release current audio servcie");
		mSoundPool.release();
	}

	public void loadCompleted() {
		for (PinYinAudioLoaderListener listener : mListeners) {
			listener.loadCompleted();
		}
	}

	public void addListener(PinYinAudioLoaderListener listener) {
		if (listener == null)
			return;

		if (mListeners == null)
			mListeners = new CopyOnWriteArrayList<PinYinAudioLoaderListener>();

		mListeners.add(listener);
	}

	public void removeListener(PinYinAudioLoaderListener listener) {
		if (listener == null)
			return;

		if (mListeners == null)
			return;

		mListeners.remove(listener);
	}
	
	public void load(PinYinAudioLoaderEvent event) {
		Log.d(TAG, "load state max = " + event.getMax() + " progress = "
				+ event.getProgress());
		fireAudioLoaderEvent(event);
		if (event.getProgress() == event.getMax()) {
			mListeners.clear();
		}
	}
	
	private void fireAudioLoaderEvent(PinYinAudioLoaderEvent event) {
		for (PinYinAudioLoaderListener listener : mListeners) {
			listener.load(event);
		}
	}

	private SoundPool createSoundPool() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			createNewSoundPool();
		}else{
			createOldSoundPool();
		}

		return mSoundPool;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	protected void createNewSoundPool(){
		AudioAttributes attributes = new AudioAttributes.Builder()
				.setUsage(AudioAttributes.USAGE_GAME)
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.build();
		mSoundPool = new SoundPool.Builder()
				.setAudioAttributes(attributes)
				.build();
	}
	@SuppressWarnings("deprecation")
	protected void createOldSoundPool(){
		mSoundPool = new SoundPool(SOUND_NUM_IN_ONECARD, AudioManager.STREAM_MUSIC, 0);
	}
	
	public final static File mSoundFolder = new File(Environment.getExternalStorageDirectory(), "PinYin/sounds");
	private int mCardNumber;
	private int mCurrentSoundID;
	private CopyOnWriteArrayList<PinYinAudioLoaderListener>  mListeners = new CopyOnWriteArrayList<PinYinAudioLoaderListener>();
	private SoundPool mSoundPool;
	{
		createSoundPool();
	}

	private HashMap<String, Integer> mSoundMap = new HashMap<String, Integer>();
	private FutureTask<SoundPool> mTask;
	
	@Inject @Named("SYNC")  HttpClientStratgy mHttpClient;
}

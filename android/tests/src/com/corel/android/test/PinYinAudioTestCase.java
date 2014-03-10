package com.corel.android.test;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PinYinAudioTestCase extends AndroidTestCase{
	///IPinYinAudioService audioService;
	boolean[] finished = new boolean[1];
	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		audioService = new PinYinFileAudioService();
//		//audioService.load(1);
//		audioService.addListener(new PinYinAudioLoaderListener() {
//
//			@Override
//			public void loadCompleted() {
//				synchronized(finished) {
//					finished[0] = true;
//					finished.notifyAll();
//				}
//			}
//
//			@Override
//			public void load(PinYinAudioLoaderEvent event) {
//				// TODO Auto-generated method stub
//				
//			}});
//		
	}

	@Override
	protected void tearDown() throws Exception {
		//audioService.release();
	}
	
	public void testPlaySound() {
	//	waitforAudioCompleted();
//		audioService.select("Good");
//		audioService.play();
	}
	
	public void testSoundPool() throws IOException {
		final boolean[] f= new boolean[1];
		SoundPool pool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		File file = new File("/sdcard/PinYin/sounds/card1/Oh.mp3");
		if(!file.exists())
			throw new IOException("ddfdfdfd");
		//int soundID = pool.load(file.getAbsolutePath(), 1);
		int soundID = pool.load(getContext(), R.raw.good, 1);
		f[0] = false;
		pool.setOnLoadCompleteListener(new OnLoadCompleteListener(){

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				synchronized(f) {
					f[0] = true;
					f.notifyAll();
				}
			}
			
		});
		
		synchronized(f) {
			if(!f[0]) {
				try {
					f.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		pool.play(soundID, 1, 1, 1, 0, 1);
		
	}
	
	public void testMediaPlayer() throws IllegalArgumentException, IllegalStateException, IOException {
		MediaPlayer player = new MediaPlayer();
		File f = new File("/sdcard/PinYin/sounds/card1/Good.mp3");
		if(!f.exists())
			throw new IOException("ddfdfdfd");
		FileInputStream in = new FileInputStream(f);
		player.setDataSource(in.getFD());
		player.prepare();
		player.start();
	}

	private void waitforAudioCompleted() {
		synchronized(finished) {
			if(!finished[0]) {
				try {
					finished.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}

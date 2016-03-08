package com.corel.android.pinyin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.corel.android.R;
import com.corel.android.dao.IPinYinDAO;
import com.corel.android.gesture.CreateGestureActivity;
import com.corel.android.http.AsyncHttpClientStrategy;
import com.corel.android.http.HttpClientStratgy;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;
import javax.inject.Named;


public class PinyinService extends Service implements IPinYinService{

	private final static String TAG = "PinYinService";
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private int NOTIFICATION = 10456;
	private Notification notification;
	private PendingIntent contentIntent;
	private CountDown cd;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage:" + msg);
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
			String words = (String)msg.obj;
			int len =  words.length();
			int i = 0;
			cd = new CountDown(mClient, len);
			mClient.setSync(cd);
			List <PinYin> pinyin = new ArrayList<PinYin>();
			while (i < len) {
				char c = words.charAt(i++);
				Log.i(TAG, "start to download:" + c);
				pinyin.add(mClient.doGetPinYin4Word(String.valueOf(c)));
			}

			try {
				cd.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			cd = new CountDown(mClient, 2 * pinyin.size());
			mClient.setSync(cd);
			InputStream in = null;
			for(PinYin p : pinyin) {
				String chinese = p.getChinese();
				String english = p.getEnglish();
				Log.i(TAG, "start to download sound:" + chinese + " and " + english);
				if(!exists(chinese)) {
					 in = mClient.doGetSound4Word(chinese);
					 saveAudio(chinese, in);
				}
				if(!exists(english)) {
					in = mClient.doGetSound4Word(english);
					saveAudio(english, in);
				}
			}
			
			updateDB(pinyin);

			try {
				cd.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			stopSelf(msg.arg1);
		}

		private void updateDB(List<PinYin> pinyin) {
			Set<PinYin> py = new HashSet<PinYin>();
			py.addAll(pinyin);
			mDB.addAll(mCurrentCard, py);
		}

		private void saveAudio(final String name, final InputStream in) {
			String targetFileName = name + PinYin.AUDIO_PR;
			FileOutputStream out = null;
			File folder = new File(PINYIN_COMMON_SOUND_FOLDER, "card" + mCurrentCard);
			if(!folder.exists()) folder.mkdirs();
			
			try {
				out = new FileOutputStream(new File(folder, targetFileName));
				IOUtils.copy(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				IOUtils.closeQuietly(out);
			}
		}
	}

	public static final class CountDown {
		private CountDownLatch cd;

		public CountDown(HttpClientStratgy client, int size) {
			if (client instanceof AsyncHttpClientStrategy) {
				cd = new CountDownLatch(size);
			}
		}

		public void countDown() {
			if (cd != null)
				cd.countDown();
		}
		
		public void await() throws InterruptedException {
			if (cd != null)
				cd.await();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDB.open();
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("PinYinServiceHandler");
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		// Display a notification about us starting. We put an icon in the
		// status bar.
		showNotification();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "拼音服务启动", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			msg.obj = extras.getString("Words");
			msg.arg2 = extras.getInt("CardId");
			load(msg.arg2);
		}
		msg.arg1 = startId;
		
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "拼音服务完成", Toast.LENGTH_SHORT).show();
		release();
	}
	
	@Override
	public void load(int card) {
		mCurrentCard = card;
	}

	@Override
	public void release() {
		mDB.close();
		notification.setLatestEventInfo(this, "拼音服务", "拼音负责完成", contentIntent);
		contentIntent.cancel();
	}

	@Override
	public boolean save() {
		return false;
	}
	
	@Override
	public int getCurrentCard() {
		return mCurrentCard;
	}

	@Override
	public boolean preLoadCheck(int card) {
		return false;
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = "拼音服务服务中";

		// Set the icon, scrolling text and timestamp
		notification = new Notification(R.drawable.icon, text,
				System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, CreateGestureActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, "拼音服务", text, contentIntent);

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}
	
	private boolean exists(final String word) {
		File f = new File(PINYIN_COMMON_SOUND_FOLDER, "card" + mCurrentCard + "/" + word + PinYin.AUDIO_PR);
		return f.exists();
	}
	
	@Inject	NotificationManager mNM;
	@Inject  @Named("ASYNC") HttpClientStratgy mClient;
	@Inject @Named("JSON") IPinYinDAO mDB;
	private static final File PINYIN_COMMON_SOUND_FOLDER = new File(Environment.getExternalStorageDirectory() + "/PinYin/sounds");
	private int mCurrentCard;
}

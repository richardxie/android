package com.corel.android.test;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

import com.corel.android.pinyin.PinyinService;

import java.util.ArrayList;

public class PinYinServiceTest extends ServiceTestCase<PinyinService> {

	private String TAG = "myservicetest";

	public PinYinServiceTest() {
		super(PinyinService.class);
	}

	public PinYinServiceTest(Class<PinyinService> serviceClass) {
		super(serviceClass);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		mContext = null;
		super.tearDown();
	}

	public void testStartable() {
		Log.i(TAG, "start test start Service");
		Intent startIntent = new Intent(getContext(), PinyinService.class);
		ArrayList<String> w = new ArrayList<String>();
		w.add("1");
		w.add("2");
		startIntent.putStringArrayListExtra("Chapter", w);
		startService(startIntent);

		PinyinService Serv = getService();
		assertNotNull(Serv);
	}

	public void testStopable() {
		Log.i(TAG, "start test stop Service");

		try {
			Intent startIntent = new Intent(getContext(), PinyinService.class);
			ArrayList<String> w = new ArrayList<String>();
			w.add("1");
			w.add("2");
			startIntent.putStringArrayListExtra("Chapter", w);
			startService(startIntent);
			PinyinService service = getService();

			service.stopService(startIntent);
//			PinyinService Serv = getService();
//			assertNull(Serv);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		} finally {
			Log.i(TAG, "end teststopService");
		}
	}
	
	public void testGetWords() {
		Intent startIntent = new Intent(getContext(), PinyinService.class);
		ArrayList<String> w = new ArrayList<String>();
		w.add("1");
		w.add("2");
		startIntent.putStringArrayListExtra("Chapter", w);
		startService(startIntent);
	}
}

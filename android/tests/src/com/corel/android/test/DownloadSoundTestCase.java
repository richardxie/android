package com.corel.android.test;

import android.os.Environment;

import com.corel.android.http.SyncHTTPURLStrategy;
import com.corel.android.http.SyncHttpClientStrategy;

import junit.framework.TestCase;

import java.io.File;

public class DownloadSoundTestCase extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testHTTPClientDownload() {
		SyncHttpClientStrategy client = new SyncHttpClientStrategy();
		client.doGetSound4Word("好");
		File f = new File(Environment.getExternalStorageDirectory() + "/好.mp3");
		assertTrue(f.exists());
		
		f.delete();
		assertFalse(f.exists());
		
	}
	
	public void testHTTPURLDownload() {
		SyncHTTPURLStrategy client = new SyncHTTPURLStrategy();
		client.doGetSound4Word("坏");
		File f = new File(Environment.getExternalStorageDirectory() + "/坏.mp3");
		assertTrue(f.exists());
		
		f.delete();
		assertFalse(f.exists());
	}
}

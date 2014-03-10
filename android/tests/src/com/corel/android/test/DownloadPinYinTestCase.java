package com.corel.android.test;

import com.corel.android.http.SyncHttpClientStrategy;

import junit.framework.TestCase;

public class DownloadPinYinTestCase extends TestCase {

	public void testHTTPClientDownloadPinYin() {
		SyncHttpClientStrategy client = new SyncHttpClientStrategy();
		client.doGetPinYin4Word("好");
	}
	
//	public void testAsyncHTTPClientDownloadPinYin() {
//		CountDownLatch cd = new CountDownLatch(1);
//		AsyncHttpClientStrategy client = new AsyncHttpClientStrategy(cd);
//		client.doGetPinYin4Word("好");
//		try {
//			cd.await();
//		} catch (InterruptedException e) {
//			assertTrue(false);
//		};
//		assertTrue(true);
//	}
}

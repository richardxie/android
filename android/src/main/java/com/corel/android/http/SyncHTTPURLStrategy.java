package com.corel.android.http;

import android.util.Log;

import com.corel.android.pinyin.PinYin;
import com.corel.android.pinyin.PinyinService.CountDown;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SyncHTTPURLStrategy implements HttpClientStratgy {

	private static final String TAG = "SyncHTTPURLStrategy";
	
	@Override
	public PinYin doGetPinYin4Word(String ch) {
		return null;
	}
                                                                                                                                                   
	@Override
	public void setSync(CountDown count) {
	}
	
	@Override
	public InputStream doGetSound4Word(String ch) {
		Log.i(TAG, "get sound for word:" + ch + " from googel translate.");
		try {
			final URI uri = new URI(
					"http://translate.google.cn/translate_tts?tl=zh-CN&q=" + ch);
			final URL u = new URL(uri.toASCIIString());

			// this is the name of the local file you will create
			
			final HttpURLConnection connection = (HttpURLConnection)u.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			final InputStream in = connection.getInputStream();
			return in;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	@Override
	public String getAudioRecognize(String file) {
		throw new UnsupportedOperationException();
		
	}
}

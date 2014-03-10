package com.corel.android.http;

import android.util.Log;

import com.corel.android.pinyin.PinYin;
import com.corel.android.pinyin.PinyinService.CountDown;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.InputStream;

public class AsyncHttpClientStrategy implements HttpClientStratgy {
	private static final String TAG = "AsyncHttpClientStrategy";
	private CountDown cd;
	public AsyncHttpClientStrategy() {

	}

	public AsyncHttpClientStrategy(CountDown cd) {
		this.cd = cd;
	}

	@Override
	public void setSync(CountDown count) {
		this.cd = count;
	}
	
	@Override
	public PinYin doGetPinYin4Word(final String ch) {
		AsyncHttpClient client = new AsyncHttpClient();

		RequestParams params = new RequestParams();
		params.put("client", "t");
		params.put("text", ch);
		params.put("hl", "zh-CN");
		params.put("sl", "zh-CN");
		params.put("tl", "en");

		client.get("http://translate.google.cn/translate_a/t", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						Log.i(TAG,"get PinYin for " + ch + ":"
								   + response.toString());
					}

					public void onFinish() {
						if(cd != null)
							cd.countDown();
					}

					public void onFailure(Throwable error, String content) {
						error.printStackTrace();
						Log.i(TAG, "failure:" + content);
					}
				});
		return null;
	}

	@Override
	public InputStream doGetSound4Word(final String ch) {
		return null;
	}
	
	@Override
	public String getAudioRecognize(String file) {
		throw new UnsupportedOperationException();
		
	}
}

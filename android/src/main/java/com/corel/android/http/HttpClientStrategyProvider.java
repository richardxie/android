package com.corel.android.http;

import com.google.inject.Provider;

public class HttpClientStrategyProvider implements Provider<HttpClientStratgy> {

	@Override
	public HttpClientStratgy get() {
		return new AsyncHttpClientStrategy();
	}
}

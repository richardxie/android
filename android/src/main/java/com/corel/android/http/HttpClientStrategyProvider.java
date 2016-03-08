package com.corel.android.http;

import javax.inject.Provider;

public class HttpClientStrategyProvider implements Provider<HttpClientStratgy> {

	@Override
	public HttpClientStratgy get() {
		return new AsyncHttpClientStrategy();
	}
}

package com.corel.android.pinyin;

import com.corel.android.audio.IAudioRecognizeService;
import com.corel.android.audio.IPinYinAudioService;
import com.corel.android.audio.PinYinFileAudioService;
import com.corel.android.dao.IPinYinDAO;
import com.corel.android.dao.PinYinJSONDAO;
import com.corel.android.dao.PinYinSQLite3DAO;
import com.corel.android.gesture.IPinYinGestureService;
import com.corel.android.gesture.PinYinGestureService;
import com.corel.android.http.AsyncHttpClientStrategy;
import com.corel.android.http.HttpClientStratgy;
import com.corel.android.http.SyncHTTPURLStrategy;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import roboguice.inject.SharedPreferencesName;

//import com.corel.android.audio.ExtAudioRecorder;
//import com.corel.android.audio.FLACAudioRecorder;

public class PinYinModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HttpClientStratgy.class).annotatedWith(Names.named("SYNC"))
				.to(SyncHTTPURLStrategy.class).in(Singleton.class);
		bind(HttpClientStratgy.class).annotatedWith(Names.named("ASYNC"))
				.to(AsyncHttpClientStrategy.class).in(Singleton.class);
		bind(IPinYinAudioService.class).to(PinYinFileAudioService.class).in(
				Singleton.class);
		bind(IAudioRecognizeService.class).to(PinYinFileAudioService.class).in(
				Singleton.class);
		bind(IPinYinGestureService.class).to(PinYinGestureService.class).in(
				Singleton.class);
		bind(IPinYinDAO.class).annotatedWith(Names.named("DB"))
				.to(PinYinSQLite3DAO.class).in(Singleton.class);
		bind(IPinYinDAO.class).annotatedWith(Names.named("JSON"))
				.to(PinYinJSONDAO.class).in(Singleton.class);
		bind(IWordLoader.class).to(AssetWordLoder.class).in(Singleton.class);
		//bind(IAudioRecorder.class).annotatedWith(Names.named("FLAC")).to(
		//		FLACAudioRecorder.class);
		bindConstant().annotatedWith(Names.named("FLAC")).to(".flac");
		
		//bind(IAudioRecorder.class).annotatedWith(Names.named("PCM")).to(
		//		ExtAudioRecorder.class);
		bindConstant().annotatedWith(Names.named("PCM")).to(".wav");
		bindConstant().annotatedWith(SharedPreferencesName.class)
				.to("Settings");
		bindConstant().annotatedWith(Names.named("JSON_FILE"))
				.to("PINYIN_JSON");
		bindConstant().annotatedWith(Names.named("AUDIO_FILE")).to(
				"PinYin/sounds");
		bindConstant().annotatedWith(Names.named("GESTURE_FILE")).to(
				"PinYin/gestures");
	}
}
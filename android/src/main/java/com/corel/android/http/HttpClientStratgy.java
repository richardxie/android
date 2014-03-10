package com.corel.android.http;

import com.corel.android.pinyin.PinYin;
import com.corel.android.pinyin.PinyinService.CountDown;

import java.io.InputStream;

public interface HttpClientStratgy {
	/**
	 * get PinYin and English for Chinese word
	 * @param ch a Chinese word
	 */
	PinYin doGetPinYin4Word(String ch);
	
	/**
	 * get Sound for a English or Chinese word
	 * @param ch a Chinese or English word
	 */
	InputStream doGetSound4Word(String ch);
	
	/**
	 * for Ansync HTTP download
	 * @param count
	 */
	void setSync(CountDown count);
	
	/**
	 * get Recognize for audio
	 */
	String getAudioRecognize(String file); 
}

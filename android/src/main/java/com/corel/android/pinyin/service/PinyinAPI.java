package com.corel.android.pinyin.service;

import com.corel.android.pinyin.ResponsePinyin;
import com.corel.android.translate.service.ResponseData;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by 强 on 1/5 0005.
 */
public interface PinyinAPI {
    @GET("/topy")
    ResponsePinyin topinyin(@Query("words") String words, @Query("accent") String accent);
}

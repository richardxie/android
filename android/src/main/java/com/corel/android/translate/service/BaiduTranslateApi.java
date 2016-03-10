package com.corel.android.translate.service;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by 强 on 1/5 0005.
 */
public interface BaiduTranslateApi {
    @GET("/translate")
    ResponseData translate(@Query("query") String query, @Query("from") String from, @Query("to") String to );
}

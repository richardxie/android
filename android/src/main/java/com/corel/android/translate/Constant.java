package com.corel.android.translate;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import javax.inject.Inject;

public class Constant {
    public static final String URL = "http://apis.baidu.com/apistore/tranlateservice";
    public static String mAppKey;

//    @Inject
//    static ApplicationContext packageManager;
//    static {
//        ApplicationInfo appInfo = null;
//        try {
//            appInfo = packageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//
//            mAppId = appInfo.metaData.get("com.baidu.speech.APP_ID").toString();
//            mAppKey = appInfo.metaData.getString("com.baidu.speech.API_KEY");
//            mAppSecret = appInfo.metaData.getString("com.baidu.speech.SECRET_KEY");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

}

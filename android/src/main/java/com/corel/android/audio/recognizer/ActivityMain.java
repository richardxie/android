package com.corel.android.audio.recognizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.corel.android.R;
import com.corel.android.pinyin.ResponsePinyin;
import com.corel.android.pinyin.service.PinyinAPI;
import com.corel.android.translate.service.ResponseData;
import com.corel.android.translate.service.BaiduTranslateApi;

import retrofit.RequestInterceptor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Func0;
import retrofit.RestAdapter;

/**
 * Created by weilikai01 on 2015/5/12.
 */
public class ActivityMain extends PreferenceActivity {
    private Handler backgroundHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.category);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove(Constant.EXTRA_INFILE).commit(); // infile参数用于控制识别一个PCM音频流（或文件），每次进入程序都将该值清楚，以避免体验时没有使用录音的问题

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        translateObservable().subscribeOn(HandlerScheduler.from(backgroundHandler))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (c) -> {
                            Log.d("Demo", "onNext:" + c);
                        },
                        (Throwable e) -> {
                            Log.e("Ops! Error: ", "Rx Did it again!", e);
                        },
                        () -> {
                            Log.d("Demo", "onCompleted!");
                        }
                );

        pinyinObservable().subscribeOn(HandlerScheduler.from(backgroundHandler))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (c) -> {
                            Log.d("Demo", "onNext:" + c);
                        },
                        (Throwable e) -> {
                            Log.e("Ops! Error: ", "Rx Did it again!", e);
                        },
                        () -> {
                            Log.d("Demo", "onCompleted!");
                        }
                );
    }
    // Retrofit section start from here...
    // create an adapter for retrofit with base url

    private static Observable<ResponseData> translateObservable() {
        return Observable.defer(new Func0<Observable<ResponseData>>() {
            @Override
            public Observable<ResponseData> call() {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(com.corel.android.Constant.TRANSLATE_SERVICE_URL)
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addHeader("apikey", com.corel.android.Constant.APPSTORE_KEY);
                            }
                        })
                        .build();

                // creating a service for adapter with our GET class
                BaiduTranslateApi demo = restAdapter.create(BaiduTranslateApi.class);
                return Observable.just(demo.translate("I'm chinese", "en", "zh"));
            }
        });
    }

    private static Observable<ResponsePinyin> pinyinObservable() {
        return Observable.defer(new Func0<Observable<ResponsePinyin>>() {
            @Override
            public Observable<ResponsePinyin> call() {
                // Retrofit section start from here...
                // create an adapter for retrofit with base url
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(com.corel.android.Constant.PINYIN_SERVICE_URL)
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addHeader("apikey", com.corel.android.Constant.APPSTORE_KEY);
                            }
                        })
                        .build();

                // creating a service for adapter with our GET class
                PinyinAPI demo = restAdapter.create(PinyinAPI.class);
                return Observable.just(demo.topinyin("我", "1"));
            }
        });
    }

    private static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("SchedulerSample-BackgroundThread", Process.THREAD_PRIORITY_BACKGROUND);
        }
    }
}

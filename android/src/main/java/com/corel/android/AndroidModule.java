package com.corel.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.corel.android.audio.AddAudioActivity;
import com.corel.android.gesture.AddGestureActivity;
import com.corel.android.gesture.CreateGestureActivity;
import com.corel.android.pinyin.PinYinModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by 强 on 3/8 0008.
 */
@Module(
        includes = {
                PinYinModule.class
        },
        injects = {
                CreateGestureActivity.class,
                AddGestureActivity.class,
                AddAudioActivity.class
        },
        library = true
)
public class AndroidModule {
    private final HelloAndroidApplication application;

    public AndroidModule(HelloAndroidApplication application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton
    LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    PackageManager providePackageManager() {
        return application.getPackageManager();
    }
}

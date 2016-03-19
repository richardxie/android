package com.corel.android;

import android.app.Application;

import com.corel.android.pinyin.PinYinModule;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by 强 on 3/8 0008.
 */
public class HelloAndroidApplication extends Application {
    private ObjectGraph graph;

    @Override public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.asList(
                new AndroidModule(this)
        );
    }

    public void inject(Object object) {
        graph.inject(object);
    }
}

package com.corel.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by 强 on 3/11 0011.
 */
public class BaseButterKnifeActivity extends Activity {
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }
}

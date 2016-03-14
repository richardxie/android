package com.corel.android.opencv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.corel.android.BaseButterKnifeActivity;
import com.corel.android.R;

import butterknife.OnClick;

public class TutorialMainActivity extends BaseButterKnifeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_opencv_main_view);
    }

    @OnClick({R.id.tutorial1Btn, R.id.tutorial2Btn, R.id.tutorial3Btn}) void doIt(View v) {
        switch (v.getId()) {
            case R.id.tutorial3Btn: {
                Intent intent = new Intent(this, Tutorial3Activity.class);
                startActivity(intent);
            }
                break;
            case R.id.tutorial2Btn: {
                Intent intent = new Intent(this, Tutorial2Activity.class);
                startActivity(intent);
            }
                break;
            case R.id.tutorial1Btn: {
                Intent intent = new Intent(this, Tutorial1Activity.class);
                startActivity(intent);
            }
                break;
            default:
        }
    }
}

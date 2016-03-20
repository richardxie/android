package com.corel.android.audio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.corel.android.BaseButterKnifeActivity;
import com.corel.android.R;
import com.corel.android.audio.recognizer.ActivityMain;
import com.corel.android.audio.tts.MainActivity;
import com.corel.android.opencv.Tutorial1Activity;
import com.corel.android.opencv.Tutorial2Activity;
import com.corel.android.opencv.Tutorial3Activity;

import butterknife.OnClick;

public class AudioMainActivity extends BaseButterKnifeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_audio_main_view);
    }

    @OnClick({R.id.recognizerBtn, R.id.ttsBtn, R.id.audioListBtn}) void doIt(View v) {
        switch (v.getId()) {
            case R.id.recognizerBtn: {
                Intent intent = new Intent(this, ActivityMain.class);
                startActivity(intent);
            }
                break;
            case R.id.ttsBtn: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.audioListBtn: {
                Intent intent = new Intent(this, AddAudioActivity.class);
                intent.putExtra("name","好");
                startActivity(intent);
            }
                break;
            default:
        }
    }
}

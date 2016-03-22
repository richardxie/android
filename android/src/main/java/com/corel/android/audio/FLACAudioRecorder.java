package com.corel.android.audio;

import javaFlacEncoder.FLAC_FileEncoder;
/**
 * Created by 强 on 3/20 0020.
 */
public class FLACAudioRecorder implements IAudioRecorder {
    public State getState(){
        return State.INITIALIZING;
    }

    public void configue(int audioSource, int sampleRate, int channelConfig,
                  int audioFormat){

    }

    public void prepare(){}

    public void start(){}

    public void stop(){}

    public void release(){}

    public void reset(){}

    public void setOutputFile(String path){}
}

package com.corel.android.audio;

import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.corel.android.R;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.File;
import java.io.IOException;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

@ContentView(R.layout.add_sound)
public class AddAudioActivity extends RoboActivity{

	private enum State { IDLE, RECORDING};
	public static final int UPDATE = 1;
	
	@Inject @Named("PCM")
	private IAudioRecorder mRecorder;
	@Inject @Named("PCM")
	private String prefix;
	private MediaPlayer mPlayer;
	
	private int mDuration;
	private State mState;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(mState == State.RECORDING) {
				super.handleMessage(msg);
				mDuration ++;
				mTimeTV.setText(timeToString());
				handler.sendMessageDelayed(handler.obtainMessage(UPDATE), 10000);
			}
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAudioName == null)
			mAudioName = "good";
		mAudioNameTV.setText(mAudioName);
		mRecorder.configue(MediaRecorder.AudioSource.MIC,
					16000,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
		
	}
	
	public void onDestroy() {
		super.onDestroy();
		if(mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
		if(mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
	}
	
	public void play(View view) {
		if(mPlayer == null)
			mPlayer = new MediaPlayer();
		
		mPlayer.reset();
		String fileName = mAudioNameTV.getText().toString()+ prefix;
		String path =  new File(mAudioService.getAudioPath(), fileName).getAbsolutePath();
		PlayAudio task = new PlayAudio();
		task.execute(path);
		mRecordBtn.setEnabled(false);
		mStopBtn.setEnabled(true);
		mWord.setVisibility(View.VISIBLE);
	}
	
	public void record(View view) {
		mPlayBtn.setEnabled(true);
		mStopBtn.setEnabled(true);
	
		//mRecorder.reset();
		String fileName = mAudioNameTV.getText().toString() + prefix;
		String path =  new File(mAudioService.getAudioPath(), fileName).getAbsolutePath();
		mRecorder.setOutputFile(path);
		try {
			mRecorder.prepare();
			mRecorder.start();
			mState = State.RECORDING;
			handler.sendEmptyMessage(UPDATE);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	public void stop(View view) {
		mPlayBtn.setEnabled(true);
		mRecordBtn.setEnabled(true);
		mDoneBtn.setEnabled(true);
		if(mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
		}
		mRecorder = null;
		handler.removeMessages(UPDATE);
		mState = State.IDLE;
		mDuration = 0;
		setResult(RESULT_OK);
	}
	
	public void done(View view) {
		setResult(RESULT_OK);
		finish();
	}
	
	private String timeToString() {
		if(mDuration > 60) {
			int min = mDuration / 60;
			String m = min > 9 ? min + "": "0" + min;
			int sec = mDuration % 60;
			String s = sec > 9 ? sec + "" : "0" + sec;
			return m + ":" + s;
		}
		else {
			return "00:" + (mDuration > 9 ? mDuration+ "" : "0" + mDuration);
		}
	}
	
	private class PlayAudio extends AsyncTask<String, Integer, String> {

		private String word;
		@Override
		protected String doInBackground(String... params) {
			try {
				//mRecognizeService.convertAudioFormat("hao.wav", "haoEncode.flac");
				word = mRecognizeService.getAudioRecognize("PinYin/test.flac");
				mPlayer.setDataSource(params[0]);
				mPlayer.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mPlayer.start();
			mWord.setText(word);
		}
	}
	
	@InjectView(R.id.play)
	private Button mPlayBtn;
	
	@InjectView(R.id.stop)
	private Button mStopBtn;
	
	@InjectView(R.id.record)
	private Button mRecordBtn;
	
	@InjectView(R.id.done)
	private Button mDoneBtn;
	
	@InjectView(R.id.time)
	private TextView mTimeTV;
	
	@InjectView(R.id.audio_name)
	private EditText mAudioNameTV;
	
	@InjectExtra(value="name", optional=true)
	private String mAudioName;
	
	@InjectView(R.id.word)
	private TextView mWord;
	
	@Inject
	private IAudioRecognizeService mRecognizeService;
	
	@Inject
	private IPinYinAudioService mAudioService;
}

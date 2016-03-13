package com.corel.android.gesture;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.corel.android.BaseButterKnifeActivity;
import com.corel.android.HelloAndroidApplication;
import com.corel.android.R;

import javax.inject.Inject;

import butterknife.Bind;

public class AddGestureActivity extends BaseButterKnifeActivity {
	private static final float LENGTH_THRESHOLD = 1.0f;
	
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized(this) {
			try {
				this.wait(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return super.onTouchEvent(event);
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gesture);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
    	setTitle("Add a Gesture");
        name = getIntent().getStringExtra(CreateGestureActivity.GESTURES_NAME);
        if(name != null) {
        	mGestureName.setText(name);
        }
        ((HelloAndroidApplication) getApplication()).inject(this);
        overlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
        overlay.addOnGestureListener(new GesturesProcessor());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (mGesture != null) {
            outState.putParcelable("gesture", mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        mGesture = savedInstanceState.getParcelable("gesture");
        if (mGesture != null) {
            overlay.post(new Runnable() {
                public void run() {
                    overlay.setGesture(mGesture);
                }
            });

            mDoneButton.setEnabled(true);
        }
    }

    public void addGesture(View v) {
        if (mGesture != null) {
            final TextView input = (TextView) findViewById(R.id.gesture_name);
            final CharSequence name = input.getText();
            if (name.length() == 0) {
                input.setError(getString(R.string.error_missing_name));
                return;
            }

            mGestureService.addGesture(name.toString(), mGesture);
            mGestureService.save();

            setResult(RESULT_OK);

            Toast.makeText(this, R.string.save_success, Toast.LENGTH_LONG).show();
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }
    
    public void cancelGesture(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
    
    public void earseGesture(View v) {
    	 if (mGesture != null) {
    		 overlay.clear(true);
    	 }
    }
    
    private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
            mDoneButton.setEnabled(false);
            mGesture = null;
        }

        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }

        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            mGesture = overlay.getGesture();
            if (mGesture.getLength() < LENGTH_THRESHOLD) {
                overlay.clear(false);
            }
            mDoneButton.setEnabled(true);
        }

        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }
    }
    
    @Bind(R.id.done)  View mDoneButton;
    @Bind(R.id.gestures_overlay)  GestureOverlayView overlay;
    @Bind(R.id.gesture_name) EditText mGestureName;
    private String name;
    @Inject IPinYinGestureService mGestureService;
    
    private Gesture mGesture;
}

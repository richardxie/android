package com.corel.android.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.corel.android.R;
import com.corel.android.pinyin.PinYin;

import java.util.ArrayList;

public class GuessGame extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 TextView label = new TextView(this);
		 if(mType == 1)
			 label.setText(R.string.reading);  
		 else if(mType == 2)
			 label.setText(R.string.writing);
		 else
			 label.setText(R.string.error_missing_name);
		 
		 label.setTextSize(20);  
		 label.setGravity(Gravity.CENTER_HORIZONTAL);  
		 
		 ImageView pic = new ImageView(this);  
		 pic.setImageResource(R.drawable.icon);  
		 pic.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
		 pic.setAdjustViewBounds(true);  
		 pic.setScaleType(ScaleType.FIT_XY);  
		 pic.setMaxHeight(250);  
		 pic.setMaxWidth(250);  
		 LinearLayout ll = new LinearLayout(this);  
		 ll.setOrientation(LinearLayout.VERTICAL);  
		 ll.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
		 ll.setGravity(Gravity.CENTER);  
		 ll.addView(label);  
		 ll.addView(pic);  
		 setContentView(ll);

		mType = getIntent().getIntExtra("type", 1);
		mWords = getIntent().getExtras().getParcelableArrayList("Words");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private int mType;

    public ArrayList<PinYin> mWords;
}

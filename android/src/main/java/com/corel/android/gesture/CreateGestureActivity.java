package com.corel.android.gesture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.corel.android.R;
import com.corel.android.pinyin.PinYin;
import com.google.inject.Inject;

import java.util.ArrayList;

import roboguice.activity.RoboListActivity;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;

//import com.corel.android.cloud.DropBoxActivity;

@ContentView(R.layout.create_gestures)
public class CreateGestureActivity extends RoboListActivity{

	public static final String TAG = "CreateGestureActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(settings != null) {
			int card = settings.getInt("card", 1);
			Log.i(TAG, "current card:" + card);
		}
		setListAdapter(new ArrayAdapter<PinYin>(this, android.R.layout.simple_list_item_1, mWords));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		PinYin word = (PinYin) getListView().getAdapter().getItem(position);
		Intent intent = new Intent(this, AddGestureActivity.class);
		intent.putExtra(GESTURES_NAME, word.getChinese());
		startActivity(intent);
	}
	
	//done Button
	public void done(View v) {
		finish();
	}
	
	//download Button
	public void downloadGestures(View v) {
		/*Intent intent = new Intent(this, DropBoxActivity.class);
		intent.putExtra("ACTION", ListGestureActivity.REQUEST_DOWNLOAD_GESTURE);
		String file = mGestureService.getCurrentGestureFile();
		intent.putExtra("FILENAME", file);
		startActivityForResult(intent, ListGestureActivity.REQUEST_DOWNLOAD_GESTURE);*/
	}
	
	public static final String GESTURES_NAME = "gesture.name";
	
	@Inject
	private SharedPreferences settings;
	
	@InjectExtra("Words")
    public ArrayList<PinYin> mWords;
	
	@Inject 
	private IPinYinGestureService mGestureService;
}
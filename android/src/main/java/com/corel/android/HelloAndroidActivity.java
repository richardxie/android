package com.corel.android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.SearchView;

import com.corel.android.audio.AudioMainActivity;
import com.corel.android.audio.recognizer.ActivityMain;
import com.corel.android.audio.tts.MainActivity;
import com.corel.android.gesture.CreateGestureActivity;
import com.corel.android.login.LoginActivity;
import com.corel.android.opencv.Tutorial2Activity;
import com.corel.android.opencv.TutorialMainActivity;
import com.corel.android.pinyin.PinYin;
import com.corel.android.pinyin.PinyinService;

import com.google.common.collect.Lists;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelloAndroidActivity extends BaseButterKnifeActivity implements SearchView.OnQueryTextListener,
		SearchView.OnSuggestionListener{

	private static String TAG = "andorid";

    private SlidingMenu menu;

    private static final String[] COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
    };
    private SuggestionsAdapter mSuggestionsAdapter;
    /**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
       // setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.i(TAG, "onCreate");
		tv.setText("ButterKnife/Dagger workable!");
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(javaScript = new JavaScriptInterface(this), "demo");
		webView.setWebViewClient(new WebViewClient());
		handler.post(new Runnable() {

			@Override
			public void run() {
				webView.loadUrl("file:///android_asset/hello.html");
			}

		});

        menu = new SlidingMenu(this);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.sliding_shadow_width);
        menu.setShadowDrawable(R.drawable.sliding_shadow);
        menu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.sliding_menu_frame);
	}

	@OnClick({android.R.id.button1, android.R.id.button2, android.R.id.button3})
	public void clicked(View v) {
		if(v.getId() == android.R.id.button1) {
			javaScript.clickOnAndroid();
			Intent intent = new Intent(this, TutorialMainActivity.class);
			startActivity(intent);
			/*Intent intent = new Intent(this, PinyinService.class);
			String words = getWords(this, 1);
			intent.putExtra("Words", words);
			intent.putExtra("CardId", 1);
			startService(intent);*/
		}
		else if(v.getId() == android.R.id.button2) {
			Intent intent = new Intent(this, CreateGestureActivity.class);
            //String words = getWords(this, 1);
			ArrayList<PinYin> words = Lists.newArrayList();
			words.add(new PinYin("好", "good", "hao"));
			words.add(new PinYin("人","people","ren"));
			words.add(new PinYin("一","one","yi"));
			words.add(new PinYin("生","live","sheng"));
			words.add(new PinYin("平","plain","ping"));
			words.add(new PinYin("安","safe","an"));
			intent.putParcelableArrayListExtra("Words", words);
            intent.putExtra("CardId", 1);
			startActivity(intent);
		}
		else if(v.getId() == android.R.id.button3) {
			Intent intent = new Intent(this, AudioMainActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Used to put dark icons on light action bar
		boolean isLight = true;

		//Create the search view
		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint("Search for countries");
		searchView.setOnQueryTextListener(this);
		searchView.setOnSuggestionListener(this);

		if (mSuggestionsAdapter == null) {
			MatrixCursor cursor = new MatrixCursor(COLUMNS);
			cursor.addRow(new String[]{"1", "'Murica"});
			cursor.addRow(new String[]{"2", "Canada"});
			cursor.addRow(new String[]{"3", "Denmark"});
			mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar().getThemedContext(), cursor);
		}

		searchView.setSuggestionsAdapter(mSuggestionsAdapter);

		menu.add("Search")
				.setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.ic_search)
				.setActionView(searchView)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		menu.add(0,0,0,"Refresh")
				.setIcon(isLight ? R.drawable.ic_refresh_inverse : R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(0,1,0,"SignIn")
				.setIcon( R.drawable.xg_sign_in)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case 1:
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				break;
		}
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onSuggestionSelect(int position) {
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		return false;
	}

    private class SuggestionsAdapter extends CursorAdapter {

        public SuggestionsAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tv = (TextView) view;
            final int textIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
            tv.setText(cursor.getString(textIndex));
        }
    }

    class JavaScriptInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;
		}
		
		/** Show a toast from the web page */
		public void showToast(final String toast) {
			Log.i(TAG, "showToast:" + toast);
			handler.post(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		 public void clickOnAndroid() {
			 Log.i(TAG, "clickOnAndroid");
			 handler.post(new Runnable() {
				 public	 void run() {
					 webView.loadUrl("javascript:change()");
				 }
			 });
		 }
	}
	
	public String getWords(Context context, int cardNum) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(context
					.getAssets().open("table.txt")));
			for (int i = 0; i < cardNum - 1; i++) {
				reader.readLine(); // word
			}
			return reader.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	 @Bind(R.id.tv) TextView tv;
	 @Bind(R.id.webview) WebView webView;
	private Handler handler = new Handler();
	private JavaScriptInterface javaScript;
}

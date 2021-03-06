package com.corel.android.gesture;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.corel.android.BaseButterKnifeActivity;
import com.corel.android.HelloAndroidApplication;
import com.corel.android.R;
import com.corel.android.audio.AudioSelectedFailedException;
import com.corel.android.audio.IPinYinAudioService;
import com.corel.android.pinyin.PinYin;
import com.corel.android.pinyin.service.PinyinAPI;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerViewAdapter;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.corel.android.pinyin.ResponsePinyin;
import com.corel.android.pinyin.service.PinyinAPI;

import retrofit.RequestInterceptor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Func0;
import retrofit.RestAdapter;

public class CreateGestureActivity extends BaseButterKnifeActivity {

	public static final String TAG = "CreateGestureActivity";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_gestures);

		((HelloAndroidApplication) getApplication()).inject(this);
		mWords = getIntent().getParcelableArrayListExtra("Words");
		if(settings != null) {
			int card = settings.getInt("card", 1);
			Log.i(TAG, "current card:" + card);
		}

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		MyAdapter adapter = new MyAdapter(mWords, mGestureService);
		recyclerView.setAdapter(adapter);

		//RxRecyclerViewAdapter.dataChanges(adapter).
		mGestureService.load(1);
		mAudioService.load(1);

		 BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
		for(PinYin word: mWords) {
			pinyinObservable(word.getChinese()).subscribeOn(HandlerScheduler.from(backgroundHandler))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (c) -> {
                            Log.d("Demo", "onNext:" + c);
                            Log.d("Demo", "pingyin for " + word + ":" + c);
                            word.setPinyin(c.getPy());
                        },
                        (Throwable e) -> {
                            Log.e("Ops! Error: ", "Rx Did it again!", e);
                        },
                        () -> {
                            Log.d("Demo", "onCompleted!");
                        }
                );
       	}
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
	
	@Inject SharedPreferences settings =  null;

	@Bind(R.id.recyclerView)
	RecyclerView recyclerView;

    public List<PinYin> mWords;
	
	@Inject
	IPinYinGestureService mGestureService;

	@Inject
	IPinYinAudioService mAudioService;

	private Handler backgroundHandler;

	 private static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("SchedulerSample-BackgroundThread", Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

     private static Observable<ResponsePinyin> pinyinObservable(final String word) {
        return Observable.defer(new Func0<Observable<ResponsePinyin>>() {
            @Override
            public Observable<ResponsePinyin> call() {
                // Retrofit section start from here...
                // create an adapter for retrofit with base url
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(com.corel.android.Constant.PINYIN_SERVICE_URL)
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addHeader("apikey", com.corel.android.Constant.APPSTORE_KEY);
                            }
                        })
                        .build();

                // creating a service for adapter with our GET class
                PinyinAPI demo = restAdapter.create(PinyinAPI.class);
                return Observable.just(demo.topinyin(word, "1"));
            }
        });
    }

	public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ListItemViewHolder> {

		private List<PinYin> items;
		private SparseBooleanArray selectedItems;

		public MyAdapter(List<PinYin> words, IPinYinGestureService gestrueService) {
			this.items = words;
			selectedItems = new SparseBooleanArray(words.size());
		}

		@Override
		public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View itemView = LayoutInflater.
					from(viewGroup.getContext()).
					inflate(R.layout.gesture_list_item, viewGroup, false);
			return new ListItemViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
			PinYin model = items.get(position);
			viewHolder.word.setText(model.getChinese());
			viewHolder.pinyin.setText(model.getPinyin());

			List<Gesture> g = mGestureService.getGestures(model.getChinese());
			if(g!=null) {
				viewHolder.gesture.setImageBitmap(g.get(0).toBitmap(50,50,1, Color.CYAN));
			}

			try {
				mAudioService.select(model.getChinese());
				viewHolder.audio.setImageResource(android.R.drawable.ic_media_play);
			}catch (AudioSelectedFailedException e) {
				Log.e(TAG, "no sound for " + model.getChinese());
			}catch (Exception ex) {
				Log.e(TAG, "exception for " + model.getChinese());
			}

			viewHolder.itemView.setActivated(selectedItems.get(position, false));
		}

		@Override
		public int getItemCount() {
			return items.size();
		}

		public final class ListItemViewHolder extends RecyclerView.ViewHolder {
			@Bind(R.id.word)
			TextView word;
			@Bind(R.id.pinyin)
			TextView pinyin;
			@Bind(R.id.gesture)
			ImageView gesture;
			@Bind(R.id.audio)
			ImageView audio;

			public ListItemViewHolder(View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
				RxView.clicks(itemView).throttleFirst(300, TimeUnit.MILLISECONDS).subscribe(e -> {
					Log.d(CreateGestureActivity.TAG, "item clicked.");
					PinYin word = items.get(getAdapterPosition());
					Intent intent = new Intent(CreateGestureActivity.this, AddGestureActivity.class);
					intent.putExtra(GESTURES_NAME, word.getChinese());
					startActivity(intent);
				});
			}
		}
	}
}

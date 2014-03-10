package com.corel.android.audio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.corel.android.R;
import com.google.inject.Inject;

import java.io.File;
import java.util.Set;

import roboguice.activity.RoboListActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.audios_list)
public class ListAudioActivity extends RoboListActivity {
	
	private static final int MENU_ID_RENAME = Menu.FIRST;
	private static final int MENU_ID_REMOVE = Menu.FIRST + 1;
	
	private ArrayAdapter<String> adapter;
	private String currentAudio;
	

	private static final int REQUEST_NEW_AUDIO = 1;
	private static final int DIALOG_RENAME_AUDIO = 1;
	private TextView input;
	private SharedPreferences settings;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
		setTitle("Audio Maker");
		registerForContextMenu(getListView());
		
		settings = getSharedPreferences("Settings", MODE_PRIVATE);
		if(settings != null) {
			//int card = settings.getInt("card", 1);
			//mAudioService.setAudioPath(this, card);
		}
		loadAudio();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		TextView textView = (TextView) info.targetView.findViewById(android.R.id.text1);
		menu.setHeaderTitle(textView.getText());
		
		menu.add(0, MENU_ID_RENAME, 0, R.string.gestures_rename);
		menu.add(0, MENU_ID_REMOVE, 0, R.string.gestures_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		final TextView view = (TextView) menuInfo.targetView;
		final String audio = (String) view.getText();
		switch (item.getItemId()) {
		case MENU_ID_RENAME:
			renameAudio(audio);
			return true;
		case MENU_ID_REMOVE:
			deleteAudio(audio);
			return true;
		}

		return super.onContextItemSelected(item);
	}
	
	private void deleteAudio(String audio) {
		File audioFile =  new File(mAudioService.getAudioPath(), audio);
		if(audioFile.delete()) {
			adapter.setNotifyOnChange(false);
			adapter.remove(audio);
			adapter.notifyDataSetChanged();
			Toast.makeText(this, R.string.audio_delete_success, Toast.LENGTH_SHORT).show();
		}
		else 
			Toast.makeText(this, R.string.audio_delete_failed, Toast.LENGTH_SHORT).show();
		
		checkForEmpty();
		
	}

	private void renameAudio(String audio) {
		currentAudio = audio;
        showDialog(DIALOG_RENAME_AUDIO);
	}
	
	private void checkForEmpty() {
		if (adapter.getCount() == 0) {
			empty.setText(R.string.gestures_empty);
		}
	}

	@Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG_RENAME_AUDIO) {
            input.setText(currentAudio);
        }
    }

	@Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_RENAME_AUDIO) {
            return createRenameDialog();
        }
        return super.onCreateDialog(id);
    }
	
	private Dialog createRenameDialog() {
		 final View layout = View.inflate(this, R.layout.dialog_rename, null);
	        input = (EditText) layout.findViewById(R.id.name);
	        ((TextView) layout.findViewById(R.id.label)).setText(R.string.audio_rename_label);

	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setIcon(0);
	        builder.setTitle(getString(R.string.audio_rename_title));
	        builder.setCancelable(true);
	        builder.setOnCancelListener(new Dialog.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                //cleanupRenameDialog();
	            }
	        });
	        builder.setNegativeButton(getString(R.string.cancel_action),
	            new Dialog.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                   // cleanupRenameDialog();
	                }
	            }
	        );
	        builder.setPositiveButton(getString(R.string.rename_action),
	            new Dialog.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    changeAudioName();
	                }
	            }
	        );
	        builder.setView(layout);
	        return builder.create();
	}
	 
	protected void changeAudioName() {
		final String name = input.getText().toString();
		if (!TextUtils.isEmpty(name)) {
			File rename = new File(mAudioService.getAudioPath(), name);
			if (!new File(mAudioService.getAudioPath(), currentAudio).renameTo(rename)) {
				Toast.makeText(this, R.string.audio_rename_failed, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, R.string.audio_rename_success, Toast.LENGTH_SHORT).show();
				adapter.remove(currentAudio);
				adapter.add(name);
				adapter.notifyDataSetChanged();
			}
			currentAudio = null;
		}
	}

	private void loadAudio() {
		if (!(Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))) {
			getListView().setVisibility(View.GONE);
			empty.setVisibility(View.VISIBLE);
			empty.setText(getString(R.string.storage_not_available, mAudioService.getAudioPath()));
		}
		adapter.clear();
		Set<String> files = mAudioService.getCurrentPinYin();
		if (files == null || files.size() == 0) {
			empty.setText(R.string.audio_empty);
		} else {
			for (String file : files) {
				adapter.add(file);
				adapter.notifyDataSetChanged();
			}
		}
	}

	public void addAudio(View v) {
		Intent intent = new Intent(this, AddAudioActivity.class);
		startActivityForResult(intent, REQUEST_NEW_AUDIO);
	}

	public void reloadAudio(View v) {
		adapter.clear();
		loadAudio();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_NEW_AUDIO:
				loadAudio();
				break;
			}
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		String name = (String) v.getTag();
		if(name == null)
			name = (String) this.getListAdapter().getItem(position);
		mAudioService.select(name);
		mAudioService.play();
	}
	
	@InjectView(android.R.id.empty)
	private TextView empty;
	
	@Inject
	private IPinYinAudioService mAudioService;
}
